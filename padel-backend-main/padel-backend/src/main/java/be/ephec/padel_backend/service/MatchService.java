package be.ephec.padel_backend.service;

import be.ephec.padel_backend.enums.StatutMatch;
import be.ephec.padel_backend.enums.StatutParticipant;
import be.ephec.padel_backend.enums.TypeMatch;
import be.ephec.padel_backend.model.*;
import be.ephec.padel_backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class MatchService {

    private final MatchPadelRepository matchPadelRepository;
    private final ParticipantMatchRepository participantMatchRepository;
    private final CreneauRepository creneauRepository;
    private final FermetureSiteRepository fermetureSiteRepository;
    private final MembreService membreService;

    public MatchService(MatchPadelRepository matchPadelRepository,
                        ParticipantMatchRepository participantMatchRepository,
                        CreneauRepository creneauRepository,
                        FermetureSiteRepository fermetureSiteRepository,
                        MembreService membreService) {
        this.matchPadelRepository = matchPadelRepository;
        this.participantMatchRepository = participantMatchRepository;
        this.creneauRepository = creneauRepository;
        this.fermetureSiteRepository = fermetureSiteRepository;
        this.membreService = membreService;
    }

    public List<MatchPadel> getAllMatchs() {
        return matchPadelRepository.findAll();
    }

    public MatchPadel getMatchById(Long id) {
        return matchPadelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Match introuvable : " + id));
    }

    public List<MatchPadel> getMatchsByOrganisateur(Long organisateurId) {
        return matchPadelRepository.findByOrganisateurId(organisateurId);
    }

    public List<MatchPadel> getMatchsPublics() {
        return matchPadelRepository.findByTypeMatch(TypeMatch.PUBLIC);
    }

    public List<MatchPadel> getMatchsBySite(Long siteId) {
        return matchPadelRepository.findByCreneauTerrainSiteId(siteId);
    }

    public MatchPadel createMatch(Long organisateurId, Long creneauId, TypeMatch typeMatch) {
        Membre organisateur = membreService.getMembreById(organisateurId);

        Creneau creneau = creneauRepository.findById(creneauId)
                .orElseThrow(() -> new RuntimeException("Créneau introuvable : " + creneauId));

        if (Boolean.FALSE.equals(creneau.getDisponible())) {
            throw new RuntimeException("Ce créneau n'est pas disponible");
        }

        if (matchPadelRepository.existsByCreneauId(creneauId)) {
            throw new RuntimeException("Ce créneau est déjà réservé");
        }

        Site site = creneau.getTerrain().getSite();
        Long siteId = site.getId();
        LocalDate dateMatch = creneau.getDateHeureDebut().toLocalDate();
        LocalTime heureMatch = creneau.getDateHeureDebut().toLocalTime();

        // ✅ Vérification jour de fermeture (global ou spécifique au site)
        boolean jourFerme = fermetureSiteRepository.estJourFerme(dateMatch, site);

        if (jourFerme) {
            throw new RuntimeException("Le site est fermé à cette date : " + dateMatch);
        }

        // ✅ Vérification des heures d'ouverture du site
        if (heureMatch.isBefore(site.getHeureOuverture()) ||
                creneau.getDateHeureFin().toLocalTime().isAfter(site.getHeureFermeture())) {
            throw new RuntimeException("Ce créneau est hors des heures d'ouverture du site (" +
                    site.getHeureOuverture() + " - " + site.getHeureFermeture() + ")");
        }

        // ✅ Vérification délai de réservation selon type de membre
        String erreur = membreService.validerReservation(organisateur, dateMatch, siteId);
        if (erreur != null) {
            throw new RuntimeException(erreur);
        }

        MatchPadel match = new MatchPadel();
        match.setTypeMatch(typeMatch);
        match.setStatut(StatutMatch.EN_ATTENTE);
        match.setOrganisateur(organisateur);
        match.setCreneau(creneau);
        match.setMontantTotal(BigDecimal.valueOf(60.00));
        match.setMontantParJoueur(BigDecimal.valueOf(15.00));
        match.setNombreMaxJoueurs(4);
        match.setEstDevenuPublicAutomatiquement(false);
        match.setSoldeOrganisateurRegle(false);

        MatchPadel matchSauve = matchPadelRepository.save(match);

        ParticipantMatch participantOrganisateur = new ParticipantMatch();
        participantOrganisateur.setMatchPadel(matchSauve);
        participantOrganisateur.setMembre(organisateur);
        participantOrganisateur.setMontantPaye(BigDecimal.ZERO);
        participantOrganisateur.setAjouteParOrganisateur(false);
        participantOrganisateur.setStatut(
                typeMatch == TypeMatch.PUBLIC
                        ? StatutParticipant.EN_ATTENTE_PAIEMENT
                        : StatutParticipant.CONFIRME
        );

        participantMatchRepository.save(participantOrganisateur);

        creneau.setDisponible(false);
        creneauRepository.save(creneau);

        return matchSauve;
    }

    public ParticipantMatch ajouterParticipantPrive(Long matchId, Long membreId, Long organisateurId) {
        MatchPadel match = getMatchById(matchId);
        Membre membre = membreService.getMembreById(membreId);

        if (!match.getOrganisateur().getId().equals(organisateurId)) {
            throw new RuntimeException("Seul l'organisateur peut ajouter un joueur à un match privé");
        }

        if (match.getTypeMatch() != TypeMatch.PRIVE) {
            throw new RuntimeException("Cette action est autorisée uniquement pour un match privé");
        }

        long nbParticipants = participantMatchRepository.countByMatchPadelId(matchId);
        if (nbParticipants >= 4) {
            throw new RuntimeException("Le match contient déjà 4 joueurs");
        }

        if (participantMatchRepository.existsByMatchPadelIdAndMembreId(matchId, membreId)) {
            throw new RuntimeException("Ce joueur est déjà inscrit à ce match");
        }

        ParticipantMatch participant = new ParticipantMatch();
        participant.setMatchPadel(match);
        participant.setMembre(membre);
        participant.setStatut(StatutParticipant.EN_ATTENTE_PAIEMENT);
        participant.setMontantPaye(BigDecimal.ZERO);
        participant.setAjouteParOrganisateur(true);

        return participantMatchRepository.save(participant);
    }

    public ParticipantMatch inscrireJoueurMatchPublic(Long matchId, Long membreId) {
        MatchPadel match = getMatchById(matchId);
        Membre membre = membreService.getMembreById(membreId);

        if (match.getTypeMatch() != TypeMatch.PUBLIC) {
            throw new RuntimeException("Ce match n'est pas public");
        }

        long nbParticipants = participantMatchRepository.countByMatchPadelId(matchId);
        if (nbParticipants >= 4) {
            throw new RuntimeException("Le match est complet");
        }

        if (participantMatchRepository.existsByMatchPadelIdAndMembreId(matchId, membreId)) {
            throw new RuntimeException("Ce joueur est déjà inscrit à ce match");
        }

        ParticipantMatch participant = new ParticipantMatch();
        participant.setMatchPadel(match);
        participant.setMembre(membre);
        participant.setStatut(StatutParticipant.EN_ATTENTE_PAIEMENT);
        participant.setMontantPaye(BigDecimal.ZERO);
        participant.setAjouteParOrganisateur(false);

        return participantMatchRepository.save(participant);
    }

    public MatchPadel annulerMatch(Long matchId) {
        MatchPadel match = getMatchById(matchId);
        match.setStatut(StatutMatch.ANNULE);
        Creneau creneau = match.getCreneau();
        creneau.setDisponible(true);
        creneauRepository.save(creneau);
        return matchPadelRepository.save(match);
    }

    public MatchPadel updateMatch(Long id, MatchPadel matchDetails) {
        MatchPadel match = getMatchById(id);
        if (matchDetails.getTypeMatch() != null) match.setTypeMatch(matchDetails.getTypeMatch());
        if (matchDetails.getStatut() != null) match.setStatut(matchDetails.getStatut());
        if (matchDetails.getMontantTotal() != null) match.setMontantTotal(matchDetails.getMontantTotal());
        if (matchDetails.getMontantParJoueur() != null) match.setMontantParJoueur(matchDetails.getMontantParJoueur());
        if (matchDetails.getNombreMaxJoueurs() != null) match.setNombreMaxJoueurs(matchDetails.getNombreMaxJoueurs());
        return matchPadelRepository.save(match);
    }

    public MatchPadel updatePrixMatch(Long id, BigDecimal nouveauPrix) {
        MatchPadel match = getMatchById(id);
        match.setMontantTotal(nouveauPrix);
        if (match.getNombreMaxJoueurs() != null && match.getNombreMaxJoueurs() > 0) {
            match.setMontantParJoueur(nouveauPrix.divide(
                    BigDecimal.valueOf(match.getNombreMaxJoueurs()), 2, java.math.RoundingMode.HALF_UP));
        }
        return matchPadelRepository.save(match);
    }

    public void deleteMatch(Long id) {
        MatchPadel match = getMatchById(id);
        if (match.getCreneau() != null) {
            match.getCreneau().setDisponible(true);
            creneauRepository.save(match.getCreneau());
        }
        matchPadelRepository.delete(match);
    }
}
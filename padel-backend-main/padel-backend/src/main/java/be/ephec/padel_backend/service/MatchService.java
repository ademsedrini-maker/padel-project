package be.ephec.padel_backend.service;

import be.ephec.padel_backend.enums.StatutMatch;
import be.ephec.padel_backend.enums.StatutParticipant;
import be.ephec.padel_backend.enums.TypeMatch;
import be.ephec.padel_backend.model.Creneau;
import be.ephec.padel_backend.model.MatchPadel;
import be.ephec.padel_backend.model.Membre;
import be.ephec.padel_backend.model.ParticipantMatch;
import be.ephec.padel_backend.repository.CreneauRepository;
import be.ephec.padel_backend.repository.MatchPadelRepository;
import be.ephec.padel_backend.repository.ParticipantMatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class MatchService {

    private final MatchPadelRepository matchPadelRepository;
    private final ParticipantMatchRepository participantMatchRepository;
    private final CreneauRepository creneauRepository;
    private final MembreService membreService;

    public MatchService(MatchPadelRepository matchPadelRepository,
                        ParticipantMatchRepository participantMatchRepository,
                        CreneauRepository creneauRepository,
                        MembreService membreService) {
        this.matchPadelRepository = matchPadelRepository;
        this.participantMatchRepository = participantMatchRepository;
        this.creneauRepository = creneauRepository;
        this.membreService = membreService;
    }

    public List<MatchPadel> getAllMatchs() {
        return matchPadelRepository.findAll();
    }

    public MatchPadel getMatchById(Long id) {
        return matchPadelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Match introuvable avec l'id : " + id));
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

        if (matchPadelRepository.existsByCreneauId(creneauId)) {
            throw new RuntimeException("Ce créneau est déjà réservé");
        }

        if (Boolean.FALSE.equals(creneau.getDisponible())) {
            throw new RuntimeException("Ce créneau n'est pas disponible");
        }

        Long siteId = creneau.getTerrain().getSite().getId();
        String erreur = membreService.validerReservation(
                organisateur,
                creneau.getDateHeureDebut().toLocalDate(),
                siteId
        );

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

        MatchPadel savedMatch = matchPadelRepository.save(match);

        ParticipantMatch participantOrganisateur = new ParticipantMatch();
        participantOrganisateur.setMatchPadel(savedMatch);
        participantOrganisateur.setMembre(organisateur);
        participantOrganisateur.setMontantPaye(BigDecimal.ZERO);
        participantOrganisateur.setAjouteParOrganisateur(false);

        if (typeMatch == TypeMatch.PUBLIC) {
            participantOrganisateur.setStatut(StatutParticipant.EN_ATTENTE_PAIEMENT);
        } else {
            participantOrganisateur.setStatut(StatutParticipant.CONFIRME);
        }

        participantMatchRepository.save(participantOrganisateur);

        creneau.setDisponible(false);
        creneauRepository.save(creneau);

        return savedMatch;
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
        MatchPadel updatedMatch = matchPadelRepository.save(match);

        Creneau creneau = match.getCreneau();
        creneau.setDisponible(true);
        creneauRepository.save(creneau);

        return updatedMatch;
    }
}
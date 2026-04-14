package be.ephec.padel_backend.service;

import be.ephec.padel_backend.enums.StatutMatch;
import be.ephec.padel_backend.enums.TypeMatch;
import be.ephec.padel_backend.model.MatchPadel;
import be.ephec.padel_backend.model.Membre;
import be.ephec.padel_backend.repository.MatchPadelRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class MatchService {

    private final MatchPadelRepository matchPadelRepository;
    private final MembreService membreService;

    public MatchService(MatchPadelRepository matchPadelRepository, MembreService membreService) {
        this.matchPadelRepository = matchPadelRepository;
        this.membreService = membreService;
    }

    public List<MatchPadel> getAllMatchs() {
        return matchPadelRepository.findAll();
    }

    public MatchPadel getMatchById(Long id) {
        return matchPadelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Match introuvable : " + id));
    }

    public MatchPadel createMatch(MatchPadel match, Membre organisateur) {
        if (membreService.hasPenalite(organisateur)) {
            throw new RuntimeException("Réservation impossible : pénalité active");
        }

        if (membreService.hasSoldueDu(organisateur)) {
            throw new RuntimeException("Réservation impossible : solde dû");
        }

        LocalDate dateMatch = match.getCreneau().getDateHeure().toLocalDate();
        LocalDate aujourdHui = LocalDate.now();
        long joursAvant = ChronoUnit.DAYS.between(aujourdHui, dateMatch);

        switch (organisateur.getTypeMembre()) {
            case GLOBAL -> {
                if (joursAvant > 21) {
                    throw new RuntimeException("Réservation trop tôt : max 3 semaines");
                }
            }
            case SITE -> {
                if (joursAvant > 14) {
                    throw new RuntimeException("Réservation trop tôt : max 2 semaines");
                }
            }
            case LIBRE -> {
                if (joursAvant > 5) {
                    throw new RuntimeException("Réservation trop tôt : max 5 jours");
                }
            }
        }

        match.setOrganisateur(organisateur);
        match.setStatut(StatutMatch.EN_ATTENTE);
        return matchPadelRepository.save(match);
    }

    public MatchPadel updateMatch(Long id, MatchPadel matchDetails) {
        MatchPadel match = getMatchById(id);
        match.setCreneau(matchDetails.getCreneau());
        match.setOrganisateur(matchDetails.getOrganisateur());
        match.setTypeMatch(matchDetails.getTypeMatch());
        match.setStatut(matchDetails.getStatut());
        match.setMontantTotal(matchDetails.getMontantTotal());
        match.setParticipants(matchDetails.getParticipants());
        return matchPadelRepository.save(match);
    }

    public MatchPadel updatePrixMatch(Long id, BigDecimal nouveauPrix) {
        MatchPadel match = getMatchById(id);
        match.setMontantTotal(nouveauPrix);
        return matchPadelRepository.save(match);
    }

    public MatchPadel rendrePublic(Long id) {
        MatchPadel match = getMatchById(id);
        match.setTypeMatch(TypeMatch.PUBLIC);
        return matchPadelRepository.save(match);
    }

    public void deleteMatch(Long id) {
        MatchPadel match = getMatchById(id);
        matchPadelRepository.delete(match);
    }
}
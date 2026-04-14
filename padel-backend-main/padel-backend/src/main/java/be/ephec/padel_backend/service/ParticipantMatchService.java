package be.ephec.padel_backend.service;

import be.ephec.padel_backend.enums.StatutMatch;
import be.ephec.padel_backend.enums.StatutParticipant;
import be.ephec.padel_backend.enums.TypeMatch;
import be.ephec.padel_backend.model.MatchPadel;
import be.ephec.padel_backend.model.Membre;
import be.ephec.padel_backend.model.ParticipantMatch;
import be.ephec.padel_backend.repository.ParticipantMatchRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ParticipantMatchService {

    private final ParticipantMatchRepository participantRepository;
    private final MatchService matchService;
    private final MembreService membreService;

    public ParticipantMatchService(ParticipantMatchRepository participantRepository,
                                   MatchService matchService,
                                   MembreService membreService) {
        this.participantRepository = participantRepository;
        this.matchService = matchService;
        this.membreService = membreService;
    }

    public List<ParticipantMatch> getAll() {
        return participantRepository.findAll();
    }

    public List<ParticipantMatch> getByMatch(Long matchId) {
        MatchPadel match = matchService.getMatchById(matchId);
        return match.getParticipants();
    }

    // Inscrire un membre dans un match
    public ParticipantMatch inscrire(Long matchId, String matricule) {
        MatchPadel match = matchService.getMatchById(matchId);
        Membre membre = membreService.getMembreByMatricule(matricule);

        // Vérifier que le match n'est pas annulé
        if (match.getStatut() == StatutMatch.ANNULE) {
            throw new RuntimeException("Impossible de s'inscrire : match annulé");
        }

        // Vérifier max 4 joueurs
        long nbParticipants = match.getParticipants().stream()
                .filter(p -> p.getStatut() != StatutParticipant.ABSENT)
                .count();
        if (nbParticipants >= 4) {
            throw new RuntimeException("Match complet : 4 joueurs maximum");
        }

        // Vérifier que le membre n'est pas déjà inscrit
        boolean dejaInscrit = match.getParticipants().stream()
                .anyMatch(p -> p.getMembre().getId().equals(membre.getId()));
        if (dejaInscrit) {
            throw new RuntimeException("Membre déjà inscrit dans ce match");
        }

        // Vérifier pénalité
        if (membreService.hasPenalite(membre)) {
            throw new RuntimeException("Inscription impossible : pénalité active");
        }

        ParticipantMatch participant = new ParticipantMatch();
        participant.setMatch(match);
        participant.setMembre(membre);
        participant.setStatut(StatutParticipant.INSCRIT);
        participant.setDateInscription(LocalDateTime.now());
        participant.setMontantPaye(BigDecimal.ZERO);

        return participantRepository.save(participant);
    }

    // Payer sa place dans un match
    public ParticipantMatch payer(Long participantId) {
        ParticipantMatch participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participant introuvable"));

        MatchPadel match = participant.getMatch();
        BigDecimal montantParJoueur = match.getMontantTotal()
                .divide(BigDecimal.valueOf(4));

        participant.setMontantPaye(montantParJoueur);
        participant.setStatut(StatutParticipant.PAYE);
        participant.setDatePaiement(LocalDateTime.now());

        // Vérifier si le match peut être confirmé (4 joueurs payés)
        long nbPaies = match.getParticipants().stream()
                .filter(p -> p.getStatut() == StatutParticipant.PAYE)
                .count();
        if (nbPaies == 4) {
            match.setStatut(StatutMatch.CONFIRME);
        }

        return participantRepository.save(participant);
    }
}

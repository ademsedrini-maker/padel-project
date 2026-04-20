package be.ephec.padel_backend.service;

import be.ephec.padel_backend.enums.StatutParticipant;
import be.ephec.padel_backend.model.MatchPadel;
import be.ephec.padel_backend.model.Membre;
import be.ephec.padel_backend.model.ParticipantMatch;
import be.ephec.padel_backend.repository.ParticipantMatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ParticipantMatchService {

    private final ParticipantMatchRepository participantMatchRepository;
    private final MatchService matchService;
    private final MembreService membreService;

    public ParticipantMatchService(ParticipantMatchRepository participantMatchRepository,
                                   MatchService matchService,
                                   MembreService membreService) {
        this.participantMatchRepository = participantMatchRepository;
        this.matchService = matchService;
        this.membreService = membreService;
    }

    public List<ParticipantMatch> getAll() {
        return participantMatchRepository.findAll();
    }

    public ParticipantMatch getById(Long id) {
        return participantMatchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Participant introuvable avec l'id : " + id));
    }

    public List<ParticipantMatch> getByMatch(Long matchId) {
        return participantMatchRepository.findByMatchPadelId(matchId);
    }

    public List<ParticipantMatch> getByMembre(Long membreId) {
        return participantMatchRepository.findByMembreId(membreId);
    }

    public ParticipantMatch inscrire(Long matchId, Long membreId, boolean ajouteParOrganisateur) {
        MatchPadel match = matchService.getMatchById(matchId);
        Membre membre = membreService.getMembreById(membreId);

        if (participantMatchRepository.existsByMatchPadelIdAndMembreId(matchId, membreId)) {
            throw new RuntimeException("Ce membre est déjà inscrit à ce match");
        }

        long nbParticipants = participantMatchRepository.countByMatchPadelId(matchId);
        if (nbParticipants >= 4) {
            throw new RuntimeException("Le match est déjà complet");
        }

        ParticipantMatch participant = new ParticipantMatch();
        participant.setMatchPadel(match);
        participant.setMembre(membre);
        participant.setAjouteParOrganisateur(ajouteParOrganisateur);
        participant.setMontantPaye(BigDecimal.ZERO);
        participant.setDateInscription(LocalDateTime.now());
        participant.setStatut(StatutParticipant.EN_ATTENTE_PAIEMENT);

        return participantMatchRepository.save(participant);
    }

    public ParticipantMatch payer(Long participantId, BigDecimal montant) {
        ParticipantMatch participant = getById(participantId);

        participant.setMontantPaye(montant);
        participant.setDatePaiement(LocalDateTime.now());
        participant.setStatut(StatutParticipant.CONFIRME);

        return participantMatchRepository.save(participant);
    }

    public ParticipantMatch annulerParticipation(Long participantId) {
        ParticipantMatch participant = getById(participantId);
        participant.setStatut(StatutParticipant.ANNULE);
        return participantMatchRepository.save(participant);
    }

    public void supprimer(Long participantId) {
        ParticipantMatch participant = getById(participantId);
        participantMatchRepository.delete(participant);
    }

    public long compterParticipantsActifs(Long matchId) {
        return participantMatchRepository.findByMatchPadelId(matchId).stream()
                .filter(p -> p.getStatut() != StatutParticipant.ANNULE)
                .count();
    }
}

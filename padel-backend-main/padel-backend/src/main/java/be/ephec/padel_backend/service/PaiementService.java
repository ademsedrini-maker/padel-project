package be.ephec.padel_backend.service;

import be.ephec.padel_backend.enums.StatutParticipant;
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
public class PaiementService {

    private final ParticipantMatchRepository participantMatchRepository;
    private final MembreService membreService;

    public PaiementService(ParticipantMatchRepository participantMatchRepository,
                           MembreService membreService) {
        this.participantMatchRepository = participantMatchRepository;
        this.membreService = membreService;
    }

    public List<ParticipantMatch> getAllPaiements() {
        return participantMatchRepository.findAll();
    }

    public ParticipantMatch getPaiementById(Long participantId) {
        return participantMatchRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Paiement/participant introuvable : " + participantId));
    }

    public ParticipantMatch createPaiement(Long participantId, BigDecimal montant) {
        ParticipantMatch participant = getPaiementById(participantId);
        Membre membre = participant.getMembre();

        BigDecimal totalAPayer = montant;

        if (membre.getSolde() != null && membre.getSolde().compareTo(BigDecimal.ZERO) > 0) {
            totalAPayer = totalAPayer.add(membre.getSolde());
            membre.setSolde(BigDecimal.ZERO);
            membreService.saveMembre(membre);
        }

        participant.setMontantPaye(totalAPayer);
        participant.setDatePaiement(LocalDateTime.now());
        participant.setStatut(StatutParticipant.CONFIRME);

        return participantMatchRepository.save(participant);
    }

    public List<ParticipantMatch> getPaiementsParMembre(Long membreId) {
        return participantMatchRepository.findByMembreId(membreId);
    }

    public List<ParticipantMatch> getPaiementsParMatch(Long matchId) {
        return participantMatchRepository.findByMatchPadelId(matchId);
    }
}
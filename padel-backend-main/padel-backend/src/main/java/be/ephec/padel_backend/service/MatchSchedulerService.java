package be.ephec.padel_backend.service;

import be.ephec.padel_backend.enums.StatutParticipant;
import be.ephec.padel_backend.enums.TypeMatch;
import be.ephec.padel_backend.model.MatchPadel;
import be.ephec.padel_backend.model.ParticipantMatch;
import be.ephec.padel_backend.repository.MatchPadelRepository;
import be.ephec.padel_backend.repository.ParticipantMatchRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class MatchSchedulerService {

    private final MatchPadelRepository matchPadelRepository;
    private final ParticipantMatchRepository participantMatchRepository;
    private final MembreService membreService;

    public MatchSchedulerService(MatchPadelRepository matchPadelRepository,
                                 ParticipantMatchRepository participantMatchRepository,
                                 MembreService membreService) {
        this.matchPadelRepository = matchPadelRepository;
        this.participantMatchRepository = participantMatchRepository;
        this.membreService = membreService;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void verifierMatchsLaVeille() {
        LocalDate demain = LocalDate.now().plusDays(1);

        List<MatchPadel> matchs = matchPadelRepository.findAll().stream()
                .filter(m -> m.getCreneau().getDateHeureDebut().toLocalDate().isEqual(demain))
                .toList();

        for (MatchPadel match : matchs) {
            long nbParticipants = participantMatchRepository.countByMatchPadelId(match.getId());

            if (match.getTypeMatch() == TypeMatch.PRIVE && nbParticipants < 4) {
                match.setTypeMatch(TypeMatch.PUBLIC);
                match.setEstDevenuPublicAutomatiquement(true);

                if (match.getOrganisateur() != null) {
                    match.getOrganisateur().setPenaliteExpiration(LocalDate.now().plusWeeks(1));
                    membreService.saveMembre(match.getOrganisateur());
                }
            }

            List<ParticipantMatch> participants = participantMatchRepository.findByMatchPadelId(match.getId());

            for (ParticipantMatch participant : participants) {
                if (participant.getStatut() == StatutParticipant.EN_ATTENTE_PAIEMENT) {
                    participant.setStatut(StatutParticipant.ANNULE);
                    participantMatchRepository.save(participant);
                    match.setTypeMatch(TypeMatch.PUBLIC);
                }
            }

            long nbConfirmes = participantMatchRepository
                    .findByMatchPadelIdAndStatut(match.getId(), StatutParticipant.CONFIRME)
                    .size();

            if (nbConfirmes < 4 && match.getOrganisateur() != null) {
                BigDecimal solde = BigDecimal.valueOf((4 - nbConfirmes) * 15.00);
                BigDecimal ancienSolde = match.getOrganisateur().getSolde() == null
                        ? BigDecimal.ZERO
                        : match.getOrganisateur().getSolde();

                match.getOrganisateur().setSolde(ancienSolde.add(solde));
                membreService.saveMembre(match.getOrganisateur());
            }

            matchPadelRepository.save(match);
        }
    }
}
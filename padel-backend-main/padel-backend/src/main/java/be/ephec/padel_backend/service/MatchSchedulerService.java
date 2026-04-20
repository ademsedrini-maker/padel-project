package be.ephec.padel_backend.service;

import be.ephec.padel_backend.enums.StatutParticipant;
import be.ephec.padel_backend.enums.TypeMatch;
import be.ephec.padel_backend.model.MatchPadel;
import be.ephec.padel_backend.model.Membre;
import be.ephec.padel_backend.model.ParticipantMatch;
import be.ephec.padel_backend.repository.MatchPadelRepository;
import be.ephec.padel_backend.repository.MembreRepository;
import be.ephec.padel_backend.repository.ParticipantMatchRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MatchSchedulerService {

    private final MatchPadelRepository matchPadelRepository;
    private final ParticipantMatchRepository participantMatchRepository;
    private final MembreRepository membreRepository;
    private final PaiementService paiementService;

    public MatchSchedulerService(MatchPadelRepository matchPadelRepository,
                                 ParticipantMatchRepository participantMatchRepository,
                                 MembreRepository membreRepository,
                                 PaiementService paiementService) {
        this.matchPadelRepository = matchPadelRepository;
        this.participantMatchRepository = participantMatchRepository;
        this.membreRepository = membreRepository;
        this.paiementService = paiementService;
    }

    /**
     * Tourne chaque nuit à 01h00.
     * Pour chaque match qui a lieu DEMAIN :
     *
     * 1. Si match PRIVE avec < 4 joueurs inscrits
     *    → devient PUBLIC
     *    → pénalité d'1 semaine à l'organisateur
     *
     * 2. Si des joueurs sont encore INSCRITS (non payés)
     *    → leur place est libérée (suppression du participant)
     *
     * 3. Si des places sont vides après libération
     *    → le solde est ajouté à l'organisateur
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void verifierMatchsDuLendemain() {

        LocalDate demain = LocalDate.now().plusDays(1);
        LocalDateTime debut = demain.atStartOfDay();
        LocalDateTime fin = demain.atTime(23, 59, 59);

        // Récupérer tous les matchs qui ont lieu demain
        List<MatchPadel> matchsDemain = matchPadelRepository
                .findByCreneauDateHeureBetween(debut, fin);

        for (MatchPadel match : matchsDemain) {

            // ── RÈGLE 1 : Match PRIVE avec < 4 joueurs → PUBLIC + pénalité ──
            if (match.getTypeMatch() == TypeMatch.PRIVE) {
                long nbInscrits = participantMatchRepository.countByMatch(match);
                if (nbInscrits < 4) {
                    // Rendre public
                    match.setTypeMatch(TypeMatch.PUBLIC);
                    matchPadelRepository.save(match);

                    // Pénalité à l'organisateur
                    Membre organisateur = match.getOrganisateur();
                    organisateur.setPenaliteExpiration(LocalDate.now().plusWeeks(1));
                    membreRepository.save(organisateur);
                }
            }

            // ── RÈGLE 2 : Libérer les places non payées ──────────────────────
            List<ParticipantMatch> nonPayes = participantMatchRepository
                    .findByMatchAndStatut(match, StatutParticipant.INSCRIT);

            for (ParticipantMatch p : nonPayes) {
                participantMatchRepository.delete(p);
            }

            // ── RÈGLE 3 : Solde organisateur si places vides ─────────────────
            paiementService.appliquerSoldeOrganisateur(match);
        }
    }
}

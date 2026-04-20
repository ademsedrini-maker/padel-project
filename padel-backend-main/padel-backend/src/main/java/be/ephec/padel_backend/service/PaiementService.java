package be.ephec.padel_backend.service;

import be.ephec.padel_backend.enums.StatutMatch;
import be.ephec.padel_backend.enums.StatutParticipant;
import be.ephec.padel_backend.model.MatchPadel;
import be.ephec.padel_backend.model.Membre;
import be.ephec.padel_backend.model.Paiement;
import be.ephec.padel_backend.model.ParticipantMatch;
import be.ephec.padel_backend.repository.MatchPadelRepository;
import be.ephec.padel_backend.repository.MembreRepository;
import be.ephec.padel_backend.repository.PaiementRepository;
import be.ephec.padel_backend.repository.ParticipantMatchRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final ParticipantMatchRepository participantMatchRepository;
    private final MatchPadelRepository matchPadelRepository;
    private final MembreRepository membreRepository;

    public PaiementService(PaiementRepository paiementRepository,
                           ParticipantMatchRepository participantMatchRepository,
                           MatchPadelRepository matchPadelRepository,
                           MembreRepository membreRepository) {
        this.paiementRepository = paiementRepository;
        this.participantMatchRepository = participantMatchRepository;
        this.matchPadelRepository = matchPadelRepository;
        this.membreRepository = membreRepository;
    }

    // ─── LECTURE ──────────────────────────────────────────────────────────────

    public List<Paiement> getAllPaiements() {
        return paiementRepository.findAll();
    }

    public List<Paiement> getPaiementsByMembre(Membre membre) {
        return paiementRepository.findByMembre(membre);
    }

    public List<Paiement> getPaiementsByMatch(MatchPadel match) {
        return paiementRepository.findByMatch(match);
    }

    // ─── PAIEMENT D'UNE PLACE ────────────────────────────────────────────────

    /**
     * Un joueur paie sa place dans un match.
     * Règles appliquées :
     * - Vérifie qu'il reste de la place (< 4 joueurs payés)
     * - Ajoute le solde dû éventuel au montant (ex-organisateur avec solde)
     * - Remet le solde à 0 après paiement
     * - Passe le participant à PAYE
     * - Si 4 joueurs payés → match CONFIRME
     */
    public Paiement payerPlace(MatchPadel match, Membre membre) {

        // 1. Vérifier qu'il reste de la place
        long nbPayes = participantMatchRepository
                .countByMatchAndStatut(match, StatutParticipant.PAYE);
        if (nbPayes >= 4) {
            throw new RuntimeException("Match complet, aucune place disponible");
        }

        // 2. Vérifier que le joueur est bien inscrit dans ce match
        ParticipantMatch participant = participantMatchRepository
                .findByMatchAndMembre(match, membre)
                .orElseThrow(() -> new RuntimeException(
                        "Vous n'êtes pas inscrit dans ce match"));

        // 3. Vérifier qu'il n'a pas déjà payé
        if (participant.getStatut() == StatutParticipant.PAYE) {
            throw new RuntimeException("Vous avez déjà payé pour ce match");
        }

        // 4. Calculer le montant : 15€ (60€ / 4) + solde dû éventuel
        BigDecimal montant = new BigDecimal("15.00");
        if (membre.getSolde() != null &&
                membre.getSolde().compareTo(BigDecimal.ZERO) > 0) {
            montant = montant.add(membre.getSolde());
            membre.setSolde(BigDecimal.ZERO); // solde remboursé
            membreRepository.save(membre);
        }

        // 5. Créer le paiement
        Paiement paiement = new Paiement();
        paiement.setMembre(membre);
        paiement.setMatch(match);
        paiement.setMontant(montant);
        paiementRepository.save(paiement);

        // 6. Passer le participant à PAYE
        participant.setStatut(StatutParticipant.PAYE);
        participantMatchRepository.save(participant);

        // 7. Si 4 joueurs payés → confirmer le match
        long totalPayes = participantMatchRepository
                .countByMatchAndStatut(match, StatutParticipant.PAYE);
        if (totalPayes >= 4) {
            match.setStatut(StatutMatch.CONFIRME);
            matchPadelRepository.save(match);
        }

        return paiement;
    }

    // ─── SOLDE ORGANISATEUR ───────────────────────────────────────────────────

    /**
     * Si le match n'est pas complet à la date du match,
     * l'organisateur paie les places vides.
     * Appelé par le scheduler la veille du match.
     * Exemple : 2 joueurs manquants → organisateur doit 2 x 15€ = 30€
     */
    public void appliquerSoldeOrganisateur(MatchPadel match) {
        long nbPayes = participantMatchRepository
                .countByMatchAndStatut(match, StatutParticipant.PAYE);
        long placesVides = 4 - nbPayes;

        if (placesVides > 0) {
            Membre organisateur = match.getOrganisateur();
            BigDecimal soldeActuel = organisateur.getSolde() != null
                    ? organisateur.getSolde()
                    : BigDecimal.ZERO;
            BigDecimal supplement = new BigDecimal("15.00")
                    .multiply(BigDecimal.valueOf(placesVides));
            organisateur.setSolde(soldeActuel.add(supplement));
            membreRepository.save(organisateur);
        }
    }
}
package be.ephec.padel_backend.service;

import be.ephec.padel_backend.enums.TypeMembre;
import be.ephec.padel_backend.model.Membre;
import be.ephec.padel_backend.repository.MembreRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class MembreService {

    private final MembreRepository membreRepository;

    public MembreService(MembreRepository membreRepository) {
        this.membreRepository = membreRepository;
    }

    public List<Membre> getAllMembres() {
        return membreRepository.findAll();
    }

    public Membre getMembreByMatricule(String matricule) {
        return membreRepository.findByMatricule(matricule)
                .orElseThrow(() -> new RuntimeException("Membre introuvable : " + matricule));
    }

    public Membre createMembre(Membre membre) {
        if (membre.getMatricule() == null || membre.getMatricule().isBlank()) {
            throw new RuntimeException("Matricule obligatoire");
        }
        if (membre.getTypeMembre() == null) {
            membre.setTypeMembre(TypeMembre.LIBRE);
        }
        return membreRepository.save(membre);
    }

    public Membre saveMembre(Membre membre) {
        if (membre.getTypeMembre() == null) {
            membre.setTypeMembre(TypeMembre.LIBRE);
        }
        return membreRepository.save(membre);
    }

    public long countMembres() {
        return membreRepository.count();
    }

    public boolean hasPenalite(Membre membre) {
        if (membre.getPenaliteExpiration() == null) return false;
        return membre.getPenaliteExpiration().isAfter(LocalDate.now());
    }

    public boolean hasSoldeDu(Membre membre) {
        return membre.getSolde() != null &&
                membre.getSolde().compareTo(BigDecimal.ZERO) > 0;
    }

    // ─── FILTRES MÉTIER ──────────────────────────────────────────────────────

    /**
     * Vérifie si un membre peut faire une réservation selon son type
     * GLOBAL  → 3 semaines avant
     * SITE    → 2 semaines avant
     * LIBRE   → 5 jours avant
     */
    public boolean peutReserver(Membre membre, LocalDate dateMatch) {
        LocalDate aujourd_hui = LocalDate.now();

        return switch (membre.getTypeMembre()) {
            case GLOBAL -> aujourd_hui.plusWeeks(3).isBefore(dateMatch) ||
                    aujourd_hui.plusWeeks(3).isEqual(dateMatch);
            case SITE   -> aujourd_hui.plusWeeks(2).isBefore(dateMatch) ||
                    aujourd_hui.plusWeeks(2).isEqual(dateMatch);
            case LIBRE  -> aujourd_hui.plusDays(5).isBefore(dateMatch) ||
                    aujourd_hui.plusDays(5).isEqual(dateMatch);
        };
    }

    /**
     * Vérifie si un membre peut réserver sur un site donné
     * GLOBAL → tous les sites
     * SITE   → seulement son site
     * LIBRE  → tous les sites
     */
    public boolean peutReserverSurSite(Membre membre, Long siteId) {
        if (membre.getTypeMembre() == TypeMembre.SITE) {
            if (membre.getSite() == null) return false;
            return membre.getSite().getId().equals(siteId);
        }
        return true; // GLOBAL et LIBRE peuvent réserver partout
    }

    /**
     * Vérifie si un membre peut réserver (penalité + solde + délai + site)
     */
    public String validerReservation(Membre membre, LocalDate dateMatch, Long siteId) {
        if (hasPenalite(membre)) {
            return "Vous avez une pénalité active jusqu'au " + membre.getPenaliteExpiration();
        }
        if (hasSoldeDu(membre)) {
            return "Vous avez un solde dû de " + membre.getSolde() + "€ à régler";
        }
        if (!peutReserverSurSite(membre, siteId)) {
            return "En tant que membre SITE, vous ne pouvez réserver que sur votre site";
        }
        if (!peutReserver(membre, dateMatch)) {
            return switch (membre.getTypeMembre()) {
                case GLOBAL -> "Membres GLOBAL : réservation possible 3 semaines avant";
                case SITE   -> "Membres SITE : réservation possible 2 semaines avant";
                case LIBRE  -> "Membres LIBRE : réservation possible 5 jours avant";
            };
        }
        return null; // null = pas d'erreur, réservation OK
    }
}
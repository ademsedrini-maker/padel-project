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

    public Membre getMembreById(Long id) {
        return membreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Membre introuvable avec l'id : " + id));
    }

    public Membre createMembre(Membre membre) {
        if (membre.getMatricule() == null || membre.getMatricule().isBlank()) {
            throw new RuntimeException("Matricule obligatoire");
        }

        if (membre.getTypeMembre() == null) {
            membre.setTypeMembre(TypeMembre.LIBRE);
        }

        if (membre.getSolde() == null) {
            membre.setSolde(BigDecimal.ZERO);
        }

        return membreRepository.save(membre);
    }

    public Membre saveMembre(Membre membre) {
        if (membre.getTypeMembre() == null) {
            membre.setTypeMembre(TypeMembre.LIBRE);
        }

        if (membre.getSolde() == null) {
            membre.setSolde(BigDecimal.ZERO);
        }

        return membreRepository.save(membre);
    }

    public long countMembres() {
        return membreRepository.count();
    }

    public boolean hasPenalite(Membre membre) {
        return membre.getPenaliteExpiration() != null
                && !membre.getPenaliteExpiration().isBefore(LocalDate.now());
    }

    public boolean hasSoldeDu(Membre membre) {
        return membre.getSolde() != null
                && membre.getSolde().compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean peutReserver(Membre membre, LocalDate dateMatch) {
        LocalDate aujourdHui = LocalDate.now();

        return switch (membre.getTypeMembre()) {
            case GLOBAL -> !dateMatch.isBefore(aujourdHui.plusWeeks(3));
            case SITE -> !dateMatch.isBefore(aujourdHui.plusWeeks(2));
            case LIBRE -> !dateMatch.isBefore(aujourdHui.plusDays(5));
        };
    }

    public boolean peutReserverSurSite(Membre membre, Long siteId) {
        if (membre.getTypeMembre() == TypeMembre.SITE) {
            return membre.getSite() != null && membre.getSite().getId().equals(siteId);
        }
        return true;
    }

    public String validerReservation(Membre membre, LocalDate dateMatch, Long siteId) {
        if (hasPenalite(membre)) {
            return "Vous avez une pénalité active jusqu'au " + membre.getPenaliteExpiration();
        }

        if (hasSoldeDu(membre)) {
            return "Vous avez un solde dû de " + membre.getSolde() + " € à régler";
        }

        if (!peutReserverSurSite(membre, siteId)) {
            return "En tant que membre SITE, vous ne pouvez réserver que sur votre site";
        }

        if (!peutReserver(membre, dateMatch)) {
            return switch (membre.getTypeMembre()) {
                case GLOBAL -> "Membres GLOBAL : réservation possible 3 semaines avant";
                case SITE -> "Membres SITE : réservation possible 2 semaines avant";
                case LIBRE -> "Membres LIBRE : réservation possible 5 jours avant";
            };
        }

        return null;
    }
}
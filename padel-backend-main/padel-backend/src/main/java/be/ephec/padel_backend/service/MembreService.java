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
        if (membre.getPenaliteExpiration() == null) {
            return false;
        }
        return membre.getPenaliteExpiration().isAfter(LocalDate.now());
    }

    public boolean hasSoldueDu(Membre membre) {
        return membre.getSolde() != null &&
                membre.getSolde().compareTo(BigDecimal.ZERO) > 0;
    }
}
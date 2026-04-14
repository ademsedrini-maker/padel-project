package be.ephec.padel_backend.service;

import be.ephec.padel_backend.enums.TypeMembre;
import be.ephec.padel_backend.model.Membre;
import be.ephec.padel_backend.repository.MembreRepository;
import org.springframework.stereotype.Service;
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
        String matricule = membre.getMatricule();

        if (matricule == null || matricule.isEmpty()) {
            throw new RuntimeException("Matricule obligatoire");
        }

        char premiere = Character.toUpperCase(matricule.charAt(0));

        switch (premiere) {
            case 'G' -> membre.setTypeMembre(TypeMembre.GLOBAL);
            case 'S' -> membre.setTypeMembre(TypeMembre.SITE);
            case 'L' -> membre.setTypeMembre(TypeMembre.LIBRE);
            default -> throw new RuntimeException("Matricule invalide. Doit commencer par G, S ou L");
        }

        return membreRepository.save(membre);
    }

    public boolean hasPenalite(Membre membre) {
        if (membre.getPenaliteExpiration() == null) return false;
        return membre.getPenaliteExpiration().isAfter(java.time.LocalDate.now());
    }

    public boolean hasSoldueDu(Membre membre) {
        return membre.getSolde() != null &&
                membre.getSolde().compareTo(java.math.BigDecimal.ZERO) > 0;
    }
}
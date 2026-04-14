package be.ephec.padel_backend.repository;

import be.ephec.padel_backend.model.Membre;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MembreRepository extends JpaRepository<Membre, Long> {
    Optional<Membre> findByMatricule(String matricule);
}
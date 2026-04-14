package be.ephec.padel_backend.repository;

import be.ephec.padel_backend.model.Creneau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreneauRepository extends JpaRepository<Creneau, Long> {
}
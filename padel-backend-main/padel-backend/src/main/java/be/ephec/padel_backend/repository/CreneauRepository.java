package be.ephec.padel_backend.repository;

import be.ephec.padel_backend.model.Creneau;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CreneauRepository extends JpaRepository<Creneau, Long> {

    List<Creneau> findByTerrainId(Long terrainId);

    List<Creneau> findByTerrainSiteId(Long siteId);

    List<Creneau> findByDisponibleTrue();

    List<Creneau> findByTerrainIdAndDisponibleTrue(Long terrainId);

    List<Creneau> findByDateHeureDebutBetween(LocalDateTime debut, LocalDateTime fin);

    List<Creneau> findByTerrainSiteIdAndDateHeureDebutBetween(Long siteId, LocalDateTime debut, LocalDateTime fin);

    boolean existsByTerrainIdAndDateHeureDebut(Long terrainId, LocalDateTime dateHeureDebut);
}
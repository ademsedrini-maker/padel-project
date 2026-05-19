package be.ephec.padel_backend.repository;

import be.ephec.padel_backend.model.Creneau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CreneauRepository extends JpaRepository<Creneau, Long> {

    List<Creneau> findByTerrainIdAndDisponibleTrue(Long terrainId);

    List<Creneau> findByTerrainSiteId(Long siteId);

    boolean existsByTerrainIdAndDateHeureDebut(Long terrainId, LocalDateTime dateHeureDebut);

    @Query("SELECT c FROM Creneau c " +
            "JOIN FETCH c.terrain t " +
            "JOIN FETCH t.site")
    List<Creneau> findAllWithTerrainAndSite();
}
package be.ephec.padel_backend.repository;

import be.ephec.padel_backend.model.Terrain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TerrainRepository extends JpaRepository<Terrain, Long> {

    @Query("SELECT t FROM Terrain t JOIN FETCH t.site")
    List<Terrain> findAllWithSite();
}
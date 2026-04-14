package be.ephec.padel_backend.repository;

import be.ephec.padel_backend.model.Terrain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TerrainRepository extends JpaRepository<Terrain, Long> {
}
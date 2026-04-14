package be.ephec.padel_backend.service;

import be.ephec.padel_backend.model.Terrain;
import be.ephec.padel_backend.repository.TerrainRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TerrainService {

    private final TerrainRepository terrainRepository;

    public TerrainService(TerrainRepository terrainRepository) {
        this.terrainRepository = terrainRepository;
    }

    public List<Terrain> getAllTerrains() {
        return terrainRepository.findAll();
    }

    public Terrain getTerrainById(Long id) {
        return terrainRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Terrain introuvable : " + id));
    }

    public Terrain createTerrain(Terrain terrain) {
        return terrainRepository.save(terrain);
    }

    public Terrain updateTerrain(Long id, Terrain terrainDetails) {
        Terrain terrain = getTerrainById(id);
        terrain.setNumero(terrainDetails.getNumero());
        terrain.setSite(terrainDetails.getSite());
        return terrainRepository.save(terrain);
    }

    public void deleteTerrain(Long id) {
        Terrain terrain = getTerrainById(id);
        terrainRepository.delete(terrain);
    }
}

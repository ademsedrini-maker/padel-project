package be.ephec.padel_backend.controller;

import be.ephec.padel_backend.model.Terrain;
import be.ephec.padel_backend.service.TerrainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terrains")
@CrossOrigin(origins = "*")
public class TerrainController {

    private final TerrainService terrainService;

    public TerrainController(TerrainService terrainService) {
        this.terrainService = terrainService;
    }

    @GetMapping
    public List<Terrain> getAllTerrains() {
        return terrainService.getAllTerrains();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Terrain> getTerrainById(@PathVariable Long id) {
        return ResponseEntity.ok(terrainService.getTerrainById(id));
    }

    @PostMapping
    public ResponseEntity<Terrain> createTerrain(@RequestBody Terrain terrain) {
        return ResponseEntity.ok(terrainService.createTerrain(terrain));
    }
}
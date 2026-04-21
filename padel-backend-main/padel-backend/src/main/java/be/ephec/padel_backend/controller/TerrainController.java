package be.ephec.padel_backend.controller;

import be.ephec.padel_backend.dto.TerrainResponse;
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
    public List<TerrainResponse> getAllTerrains() {
        return terrainService.getAllTerrains()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TerrainResponse> getTerrainById(@PathVariable Long id) {
        Terrain terrain = terrainService.getTerrainById(id);
        return ResponseEntity.ok(toResponse(terrain));
    }

    @PostMapping
    public ResponseEntity<Terrain> createTerrain(@RequestBody Terrain terrain) {
        return ResponseEntity.ok(terrainService.createTerrain(terrain));
    }

    private TerrainResponse toResponse(Terrain terrain) {
        String siteNom = terrain.getSite() != null ? terrain.getSite().getNom() : "";
        String adresse = terrain.getSite() != null ? terrain.getSite().getAdresse() : "";
        var heureOuverture = terrain.getSite() != null ? terrain.getSite().getHeureOuverture() : null;
        var heureFermeture = terrain.getSite() != null ? terrain.getSite().getHeureFermeture() : null;

        String description = "Terrain " + terrain.getNumero() + " situé sur le site de " + siteNom;
        String imageUrl = siteNom.equalsIgnoreCase("Bruxelles")
                ? "assets/images/terrain-bruxelles.jpg"
                : "assets/images/terrain-liege.jpg";

        return new TerrainResponse(
                terrain.getId(),
                terrain.getNumero(),
                siteNom,
                adresse,
                heureOuverture,
                heureFermeture,
                description,
                imageUrl
        );
    }
}
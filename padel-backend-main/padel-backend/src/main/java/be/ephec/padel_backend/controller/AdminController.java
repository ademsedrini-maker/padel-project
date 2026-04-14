package be.ephec.padel_backend.controller;

import be.ephec.padel_backend.model.MatchPadel;
import be.ephec.padel_backend.model.Membre;
import be.ephec.padel_backend.model.Site;
import be.ephec.padel_backend.model.Terrain;
import be.ephec.padel_backend.service.MatchService;
import be.ephec.padel_backend.service.MembreService;
import be.ephec.padel_backend.service.SiteService;
import be.ephec.padel_backend.service.TerrainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final MatchService matchService;
    private final TerrainService terrainService;
    private final MembreService membreService;
    private final SiteService siteService;

    public AdminController(MatchService matchService,
                           TerrainService terrainService,
                           MembreService membreService,
                           SiteService siteService) {
        this.matchService = matchService;
        this.terrainService = terrainService;
        this.membreService = membreService;
        this.siteService = siteService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> testAdminAccess() {
        return ResponseEntity.ok("Accès admin autorisé");
    }

    @GetMapping("/matchs")
    public ResponseEntity<List<MatchPadel>> getAllMatchs() {
        return ResponseEntity.ok(matchService.getAllMatchs());
    }

    @GetMapping("/matchs/{id}")
    public ResponseEntity<MatchPadel> getMatchById(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.getMatchById(id));
    }

    @PutMapping("/matchs/{id}")
    public ResponseEntity<MatchPadel> updateMatch(@PathVariable Long id, @RequestBody MatchPadel matchPadel) {
        return ResponseEntity.ok(matchService.updateMatch(id, matchPadel));
    }

    @PutMapping("/matchs/{id}/prix")
    public ResponseEntity<MatchPadel> updatePrixMatch(@PathVariable Long id, @RequestBody Map<String, BigDecimal> body) {
        BigDecimal nouveauPrix = body.get("montantTotal");
        return ResponseEntity.ok(matchService.updatePrixMatch(id, nouveauPrix));
    }

    @DeleteMapping("/matchs/{id}")
    public ResponseEntity<String> deleteMatch(@PathVariable Long id) {
        matchService.deleteMatch(id);
        return ResponseEntity.ok("Match supprimé avec succès");
    }

    @GetMapping("/terrains")
    public ResponseEntity<List<Terrain>> getAllTerrains() {
        return ResponseEntity.ok(terrainService.getAllTerrains());
    }

    @GetMapping("/terrains/{id}")
    public ResponseEntity<Terrain> getTerrainById(@PathVariable Long id) {
        return ResponseEntity.ok(terrainService.getTerrainById(id));
    }

    @PostMapping("/terrains")
    public ResponseEntity<Terrain> createTerrain(@RequestBody Terrain terrain) {
        return ResponseEntity.ok(terrainService.createTerrain(terrain));
    }

    @PutMapping("/terrains/{id}")
    public ResponseEntity<Terrain> updateTerrain(@PathVariable Long id, @RequestBody Terrain terrain) {
        return ResponseEntity.ok(terrainService.updateTerrain(id, terrain));
    }

    @DeleteMapping("/terrains/{id}")
    public ResponseEntity<String> deleteTerrain(@PathVariable Long id) {
        terrainService.deleteTerrain(id);
        return ResponseEntity.ok("Terrain supprimé avec succès");
    }

    @GetMapping("/sites")
    public ResponseEntity<List<Site>> getAllSites() {
        return ResponseEntity.ok(siteService.getAllSites());
    }

    @GetMapping("/sites/{id}")
    public ResponseEntity<Site> getSiteById(@PathVariable Long id) {
        return ResponseEntity.ok(siteService.getSiteById(id));
    }

    @PostMapping("/sites")
    public ResponseEntity<Site> createSite(@RequestBody Site site) {
        return ResponseEntity.ok(siteService.createSite(site));
    }

    @PutMapping("/sites/{id}")
    public ResponseEntity<Site> updateSite(@PathVariable Long id, @RequestBody Site site) {
        return ResponseEntity.ok(siteService.updateSite(id, site));
    }

    @DeleteMapping("/sites/{id}")
    public ResponseEntity<String> deleteSite(@PathVariable Long id) {
        siteService.deleteSite(id);
        return ResponseEntity.ok("Site supprimé avec succès");
    }

    @GetMapping("/membres")
    public ResponseEntity<List<Membre>> getAllMembres() {
        return ResponseEntity.ok(membreService.getAllMembres());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        List<MatchPadel> matchs = matchService.getAllMatchs();
        List<Terrain> terrains = terrainService.getAllTerrains();
        List<Membre> membres = membreService.getAllMembres();
        List<Site> sites = siteService.getAllSites();

        BigDecimal chiffreAffaires = matchs.stream()
                .map(MatchPadel::getMontantTotal)
                .filter(montant -> montant != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> stats = new HashMap<>();
        stats.put("nombreMatchs", matchs.size());
        stats.put("nombreTerrains", terrains.size());
        stats.put("nombreMembres", membres.size());
        stats.put("nombreSites", sites.size());
        stats.put("chiffreAffaires", chiffreAffaires);

        return ResponseEntity.ok(stats);
    }
}
package be.ephec.padel_backend.controller;

import be.ephec.padel_backend.model.MatchPadel;
import be.ephec.padel_backend.model.Membre;
import be.ephec.padel_backend.service.MatchService;
import be.ephec.padel_backend.service.MembreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/matches")
@CrossOrigin(origins = "*")
public class MatchController {

    private final MatchService matchService;
    private final MembreService membreService;

    public MatchController(MatchService matchService, MembreService membreService) {
        this.matchService = matchService;
        this.membreService = membreService;
    }

    @GetMapping
    public List<MatchPadel> getAllMatchs() {
        return matchService.getAllMatchs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchPadel> getMatchById(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.getMatchById(id));
    }

    @PostMapping("/{matricule}")
    public ResponseEntity<MatchPadel> createMatch(
            @PathVariable String matricule,
            @RequestBody MatchPadel match) {
        Membre organisateur = membreService.getMembreByMatricule(matricule);
        return ResponseEntity.ok(matchService.createMatch(match, organisateur));
    }

    @PutMapping("/{id}/public")
    public ResponseEntity<MatchPadel> rendrePublic(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.rendrePublic(id));
    }
}

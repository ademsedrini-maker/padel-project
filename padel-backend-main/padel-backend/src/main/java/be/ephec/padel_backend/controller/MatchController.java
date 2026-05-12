package be.ephec.padel_backend.controller;

import be.ephec.padel_backend.enums.TypeMatch;
import be.ephec.padel_backend.model.MatchPadel;
import be.ephec.padel_backend.model.Membre;
import be.ephec.padel_backend.model.ParticipantMatch;
import be.ephec.padel_backend.service.MatchService;
import be.ephec.padel_backend.service.MembreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matchs")
@CrossOrigin(origins = "*")
public class MatchController {

    private final MatchService matchService;
    private final MembreService membreService;

    public MatchController(MatchService matchService, MembreService membreService) {
        this.matchService = matchService;
        this.membreService = membreService;
    }

    // ─── LECTURE ──────────────────────────────────────────────────────────────

    @GetMapping
    public List<MatchPadel> getAllMatchs() {
        return matchService.getAllMatchs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchPadel> getMatchById(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.getMatchById(id));
    }

    @GetMapping("/publics")
    public List<MatchPadel> getMatchsPublics() {
        return matchService.getMatchsPublics();
    }

    @GetMapping("/site/{siteId}")
    public List<MatchPadel> getMatchsBySite(@PathVariable Long siteId) {
        return matchService.getMatchsBySite(siteId);
    }

    @GetMapping("/organisateur/{organisateurId}")
    public List<MatchPadel> getMatchsByOrganisateur(@PathVariable Long organisateurId) {
        return matchService.getMatchsByOrganisateur(organisateurId);
    }

    // ─── ENDPOINT MANQUANT — matchs d'un membre par matricule ─────────────────

    @GetMapping("/membre/{matricule}")
    public List<MatchPadel> getMatchsByMembreMatricule(@PathVariable String matricule) {
        Membre membre = membreService.getMembreByMatricule(matricule);
        return matchService.getMatchsByOrganisateur(membre.getId());
    }

    // ─── ACTIONS ──────────────────────────────────────────────────────────────

    @PostMapping("/create")
    public ResponseEntity<MatchPadel> createMatch(@RequestBody Map<String, String> body) {
        Long organisateurId = Long.parseLong(body.get("organisateurId"));
        Long creneauId = Long.parseLong(body.get("creneauId"));
        TypeMatch typeMatch = TypeMatch.valueOf(body.get("typeMatch"));
        return ResponseEntity.ok(matchService.createMatch(organisateurId, creneauId, typeMatch));
    }

    @PostMapping("/{matchId}/ajouter-participant-prive")
    public ResponseEntity<ParticipantMatch> ajouterParticipantPrive(
            @PathVariable Long matchId,
            @RequestBody Map<String, String> body) {
        Long membreId = Long.parseLong(body.get("membreId"));
        Long organisateurId = Long.parseLong(body.get("organisateurId"));
        return ResponseEntity.ok(matchService.ajouterParticipantPrive(matchId, membreId, organisateurId));
    }

    @PostMapping("/{matchId}/inscription-public")
    public ResponseEntity<ParticipantMatch> inscrireJoueurMatchPublic(
            @PathVariable Long matchId,
            @RequestBody Map<String, String> body) {
        Long membreId = Long.parseLong(body.get("membreId"));
        return ResponseEntity.ok(matchService.inscrireJoueurMatchPublic(matchId, membreId));
    }

    @PutMapping("/{matchId}/annuler")
    public ResponseEntity<MatchPadel> annulerMatch(@PathVariable Long matchId) {
        return ResponseEntity.ok(matchService.annulerMatch(matchId));
    }
}
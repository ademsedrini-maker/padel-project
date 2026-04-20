package be.ephec.padel_backend.controller;

import be.ephec.padel_backend.enums.TypeMatch;
import be.ephec.padel_backend.model.MatchPadel;
import be.ephec.padel_backend.model.ParticipantMatch;
import be.ephec.padel_backend.service.MatchService;
import be.ephec.padel_backend.service.MembreService;
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

    @GetMapping
    public List<MatchPadel> getAllMatchs() {
        return matchService.getAllMatchs();
    }

    @GetMapping("/{id}")
    public MatchPadel getMatchById(@PathVariable Long id) {
        return matchService.getMatchById(id);
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

    @PostMapping("/create")
    public MatchPadel createMatch(@RequestBody Map<String, String> body) {
        Long organisateurId = Long.parseLong(body.get("organisateurId"));
        Long creneauId = Long.parseLong(body.get("creneauId"));
        TypeMatch typeMatch = TypeMatch.valueOf(body.get("typeMatch"));

        return matchService.createMatch(organisateurId, creneauId, typeMatch);
    }

    @PostMapping("/{matchId}/ajouter-participant-prive")
    public ParticipantMatch ajouterParticipantPrive(@PathVariable Long matchId,
                                                    @RequestBody Map<String, String> body) {
        Long membreId = Long.parseLong(body.get("membreId"));
        Long organisateurId = Long.parseLong(body.get("organisateurId"));

        return matchService.ajouterParticipantPrive(matchId, membreId, organisateurId);
    }

    @PostMapping("/{matchId}/inscription-public")
    public ParticipantMatch inscrireJoueurMatchPublic(@PathVariable Long matchId,
                                                      @RequestBody Map<String, String> body) {
        Long membreId = Long.parseLong(body.get("membreId"));

        return matchService.inscrireJoueurMatchPublic(matchId, membreId);
    }

    @PutMapping("/{matchId}/annuler")
    public MatchPadel annulerMatch(@PathVariable Long matchId) {
        return matchService.annulerMatch(matchId);
    }
}
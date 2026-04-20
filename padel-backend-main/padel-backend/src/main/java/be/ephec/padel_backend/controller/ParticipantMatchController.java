package be.ephec.padel_backend.controller;

import be.ephec.padel_backend.model.ParticipantMatch;
import be.ephec.padel_backend.service.ParticipantMatchService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/participants")
@CrossOrigin(origins = "*")
public class ParticipantMatchController {

    private final ParticipantMatchService participantMatchService;

    public ParticipantMatchController(ParticipantMatchService participantMatchService) {
        this.participantMatchService = participantMatchService;
    }

    @GetMapping
    public List<ParticipantMatch> getAll() {
        return participantMatchService.getAll();
    }

    @GetMapping("/{id}")
    public ParticipantMatch getById(@PathVariable Long id) {
        return participantMatchService.getById(id);
    }

    @GetMapping("/match/{matchId}")
    public List<ParticipantMatch> getByMatch(@PathVariable Long matchId) {
        return participantMatchService.getByMatch(matchId);
    }

    @GetMapping("/membre/{membreId}")
    public List<ParticipantMatch> getByMembre(@PathVariable Long membreId) {
        return participantMatchService.getByMembre(membreId);
    }

    @PostMapping("/inscrire")
    public ParticipantMatch inscrire(@RequestBody Map<String, String> body) {
        Long matchId = Long.parseLong(body.get("matchId"));
        Long membreId = Long.parseLong(body.get("membreId"));
        boolean ajouteParOrganisateur = Boolean.parseBoolean(
                body.getOrDefault("ajouteParOrganisateur", "false")
        );

        return participantMatchService.inscrire(matchId, membreId, ajouteParOrganisateur);
    }

    @PutMapping("/{participantId}/payer")
    public ParticipantMatch payer(@PathVariable Long participantId,
                                  @RequestBody Map<String, String> body) {
        BigDecimal montant = new BigDecimal(body.get("montant"));
        return participantMatchService.payer(participantId, montant);
    }

    @PutMapping("/{participantId}/annuler")
    public ParticipantMatch annulerParticipation(@PathVariable Long participantId) {
        return participantMatchService.annulerParticipation(participantId);
    }

    @DeleteMapping("/{participantId}")
    public void supprimer(@PathVariable Long participantId) {
        participantMatchService.supprimer(participantId);
    }
}
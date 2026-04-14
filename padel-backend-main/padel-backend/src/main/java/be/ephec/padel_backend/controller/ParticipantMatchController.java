package be.ephec.padel_backend.controller;

import be.ephec.padel_backend.model.ParticipantMatch;
import be.ephec.padel_backend.service.ParticipantMatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participants")
@CrossOrigin(origins = "*")
public class ParticipantMatchController {

    private final ParticipantMatchService participantService;

    public ParticipantMatchController(ParticipantMatchService participantService) {
        this.participantService = participantService;
    }

    // Voir tous les participants
    @GetMapping
    public List<ParticipantMatch> getAll() {
        return participantService.getAll();
    }

    // Voir les participants d'un match
    @GetMapping("/match/{matchId}")
    public ResponseEntity<List<ParticipantMatch>> getByMatch(@PathVariable Long matchId) {
        return ResponseEntity.ok(participantService.getByMatch(matchId));
    }

    // Inscrire un membre dans un match
    @PostMapping("/inscrire/{matchId}/{matricule}")
    public ResponseEntity<ParticipantMatch> inscrire(
            @PathVariable Long matchId,
            @PathVariable String matricule) {
        return ResponseEntity.ok(participantService.inscrire(matchId, matricule));
    }

    // Payer sa place
    @PutMapping("/payer/{participantId}")
    public ResponseEntity<ParticipantMatch> payer(@PathVariable Long participantId) {
        return ResponseEntity.ok(participantService.payer(participantId));
    }
}
package be.ephec.padel_backend.controller;

import be.ephec.padel_backend.model.ParticipantMatch;
import be.ephec.padel_backend.service.PaiementService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/paiements")
@CrossOrigin(origins = "*")
public class PaiementController {

    private final PaiementService paiementService;

    public PaiementController(PaiementService paiementService) {
        this.paiementService = paiementService;
    }

    @GetMapping
    public List<ParticipantMatch> getAll() {
        return paiementService.getAllPaiements();
    }

    @GetMapping("/{participantId}")
    public ParticipantMatch getById(@PathVariable Long participantId) {
        return paiementService.getPaiementById(participantId);
    }

    @PostMapping
    public ParticipantMatch createPaiement(@RequestBody Map<String, String> body) {
        Long participantId = Long.parseLong(body.get("participantId"));
        BigDecimal montant = new BigDecimal(body.get("montant"));

        return paiementService.createPaiement(participantId, montant);
    }

    @GetMapping("/membre/{membreId}")
    public List<ParticipantMatch> getByMembre(@PathVariable Long membreId) {
        return paiementService.getPaiementsParMembre(membreId);
    }

    @GetMapping("/match/{matchId}")
    public List<ParticipantMatch> getByMatch(@PathVariable Long matchId) {
        return paiementService.getPaiementsParMatch(matchId);
    }
}
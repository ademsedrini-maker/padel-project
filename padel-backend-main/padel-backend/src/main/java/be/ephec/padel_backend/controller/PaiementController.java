package be.ephec.padel_backend.controller;

import be.ephec.padel_backend.model.Paiement;
import be.ephec.padel_backend.service.PaiementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paiements")
@CrossOrigin(origins = "*")
public class PaiementController {

    private final PaiementService paiementService;

    public PaiementController(PaiementService paiementService) {
        this.paiementService = paiementService;
    }

    @GetMapping
    public List<Paiement> getAll() {
        return paiementService.getAllPaiements();
    }

    @PostMapping
    public ResponseEntity<Paiement> create(@RequestBody Paiement paiement) {
        return ResponseEntity.ok(paiementService.createPaiement(paiement));
    }
}
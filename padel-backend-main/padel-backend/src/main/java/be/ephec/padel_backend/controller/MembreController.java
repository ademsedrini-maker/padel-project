package be.ephec.padel_backend.controller;

import be.ephec.padel_backend.dto.LoginMembreRequest;
import be.ephec.padel_backend.model.Membre;
import be.ephec.padel_backend.service.MembreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membres")
@CrossOrigin(origins = "*")
public class MembreController {

    private final MembreService membreService;

    public MembreController(MembreService membreService) {
        this.membreService = membreService;
    }

    @GetMapping
    public List<Membre> getAllMembres() {
        return membreService.getAllMembres();
    }

    @GetMapping("/{matricule}")
    public ResponseEntity<Membre> getByMatricule(@PathVariable String matricule) {
        return ResponseEntity.ok(membreService.getMembreByMatricule(matricule));
    }

    @PostMapping
    public ResponseEntity<Membre> createMembre(@RequestBody Membre membre) {
        return ResponseEntity.ok(membreService.createMembre(membre));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginMembreRequest request) {
        try {
            Membre membre = membreService.getMembreByMatricule(request.getMatricule());
            return ResponseEntity.ok(membre);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Matricule introuvable");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Membre membre) {
        try {
            String matricule = "M" + String.format("%04d",
                    membreService.countMembres() + 1);
            membre.setMatricule(matricule);
            Membre saved = membreService.saveMembre(membre);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Erreur lors de la création du compte");
        }
    }
}
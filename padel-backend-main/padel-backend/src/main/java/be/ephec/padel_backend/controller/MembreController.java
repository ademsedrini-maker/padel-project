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
    public ResponseEntity<Membre> login(@RequestBody LoginMembreRequest request) {
        Membre membre = membreService.getMembreByMatricule(request.getMatricule());
        return ResponseEntity.ok(membre);
    }
}
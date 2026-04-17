package be.ephec.padel_backend.controller;

import be.ephec.padel_backend.dto.LoginMembreRequest;
import be.ephec.padel_backend.model.Membre;
import be.ephec.padel_backend.service.MembreService;
import org.springframework.http.HttpStatus;
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

    // ─── LOGIN MEMBRE ─────────────────────────────────────────────────────────

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginMembreRequest request) {
        try {
            Membre membre = membreService.getMembreByMatricule(request.getMatricule());
            return ResponseEntity.ok(membre);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Matricule introuvable");
        }
    }

    // ─── REGISTER ─────────────────────────────────────────────────────────────

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Membre membre) {
        try {
            // Génération du préfixe selon le type
            String prefix = switch (membre.getTypeMembre()) {
                case GLOBAL -> "G";
                case SITE   -> "S";
                case LIBRE  -> "L";
                default     -> "M";
            };

            String matricule = prefix + String.format("%04d", membreService.countMembres() + 1);
            membre.setMatricule(matricule);

            Membre saved = membreService.saveMembre(membre);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erreur lors de la création du compte : " + e.getMessage());
        }
    }

    // ─── CRUD ─────────────────────────────────────────────────────────────────

    @GetMapping
    public List<Membre> getAllMembres() {
        return membreService.getAllMembres();
    }

    @GetMapping("/{matricule}")
    public ResponseEntity<?> getByMatricule(@PathVariable String matricule) {
        try {
            return ResponseEntity.ok(membreService.getMembreByMatricule(matricule));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Matricule introuvable");
        }
    }

    @PostMapping
    public ResponseEntity<Membre> createMembre(@RequestBody Membre membre) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(membreService.createMembre(membre));
    }
}
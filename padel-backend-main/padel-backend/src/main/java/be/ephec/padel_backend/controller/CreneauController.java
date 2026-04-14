package be.ephec.padel_backend.controller;

import be.ephec.padel_backend.model.Creneau;
import be.ephec.padel_backend.repository.CreneauRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/creneaux")
@CrossOrigin(origins = "*")
public class CreneauController {

    private final CreneauRepository creneauRepository;

    public CreneauController(CreneauRepository creneauRepository) {
        this.creneauRepository = creneauRepository;
    }

    @GetMapping
    public List<Creneau> getAll() {
        return creneauRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Creneau> create(@RequestBody Creneau creneau) {
        return ResponseEntity.ok(creneauRepository.save(creneau));
    }
}

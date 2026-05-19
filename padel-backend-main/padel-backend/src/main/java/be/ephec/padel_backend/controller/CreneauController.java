package be.ephec.padel_backend.controller;

import be.ephec.padel_backend.dto.CreneauDTO;
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
    public List<CreneauDTO> getAll() {
        // On récupère les créneaux avec terrain + site déjà chargés
        List<Creneau> creneaux = creneauRepository.findAllWithTerrainAndSite();

        return creneaux.stream()
                .map(c -> new CreneauDTO(
                        c.getId(),
                        c.getDateHeureDebut().toString(),
                        c.getDateHeureFin().toString(),
                        c.getDisponible(),                  // ou isDisponible() selon ton getter
                        c.getTerrain().getNumero(),
                        c.getTerrain().getSite().getNom()
                ))
                .toList();
    }

    @PostMapping
    public ResponseEntity<CreneauDTO> create(@RequestBody Creneau creneau) {
        Creneau saved = creneauRepository.save(creneau);

        CreneauDTO dto = new CreneauDTO(
                saved.getId(),
                saved.getDateHeureDebut().toString(),
                saved.getDateHeureFin().toString(),
                saved.getDisponible(),                  // idem ici
                saved.getTerrain().getNumero(),
                saved.getTerrain().getSite().getNom()
        );

        return ResponseEntity.ok(dto);
    }
}

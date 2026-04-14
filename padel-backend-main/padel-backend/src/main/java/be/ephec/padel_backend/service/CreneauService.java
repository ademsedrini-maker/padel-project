package be.ephec.padel_backend.service;

import be.ephec.padel_backend.model.Creneau;
import be.ephec.padel_backend.repository.CreneauRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CreneauService {

    private final CreneauRepository creneauRepository;

    public CreneauService(CreneauRepository creneauRepository) {
        this.creneauRepository = creneauRepository;
    }

    public List<Creneau> getAllCreneaux() {
        return creneauRepository.findAll();
    }

    public Creneau getCreneauById(Long id) {
        return creneauRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Créneau introuvable"));
    }

    public Creneau createCreneau(Creneau creneau) {
        return creneauRepository.save(creneau);
    }
}
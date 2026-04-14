package be.ephec.padel_backend.service;

import be.ephec.padel_backend.model.Paiement;
import be.ephec.padel_backend.repository.PaiementRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PaiementService {

    private final PaiementRepository paiementRepository;

    public PaiementService(PaiementRepository paiementRepository) {
        this.paiementRepository = paiementRepository;
    }

    public List<Paiement> getAllPaiements() {
        return paiementRepository.findAll();
    }

    public Paiement createPaiement(Paiement paiement) {
        return paiementRepository.save(paiement);
    }
}
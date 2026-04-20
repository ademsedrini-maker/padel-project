package be.ephec.padel_backend.service;

import be.ephec.padel_backend.model.FermetureSite;
import be.ephec.padel_backend.model.Site;
import be.ephec.padel_backend.repository.FermetureSiteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FermetureSiteService {

    private final FermetureSiteRepository fermetureSiteRepository;

    public FermetureSiteService(FermetureSiteRepository fermetureSiteRepository) {
        this.fermetureSiteRepository = fermetureSiteRepository;
    }

    public List<FermetureSite> getAll() {
        return fermetureSiteRepository.findAll();
    }

    public List<FermetureSite> getFermeturesGlobales() {
        return fermetureSiteRepository.findBySiteIsNull();
    }

    public List<FermetureSite> getFermeturesBySite(Site site) {
        return fermetureSiteRepository.findBySite(site);
    }

    // Vérifie si un jour est fermé pour un site (global ou spécifique)
    public boolean estJourFerme(LocalDate date, Site site) {
        return fermetureSiteRepository.estJourFerme(date, site);
    }

    // Ajouter une fermeture globale (site = null)
    public FermetureSite ajouterFermetureGlobale(LocalDate date, String motif) {
        FermetureSite f = new FermetureSite();
        f.setDate(date);
        f.setMotif(motif);
        f.setSite(null); // globale
        return fermetureSiteRepository.save(f);
    }

    // Ajouter une fermeture pour un site spécifique
    public FermetureSite ajouterFermetureSite(Site site, LocalDate date, String motif) {
        FermetureSite f = new FermetureSite();
        f.setSite(site);
        f.setDate(date);
        f.setMotif(motif);
        return fermetureSiteRepository.save(f);
    }

    public void supprimerFermeture(Long id) {
        fermetureSiteRepository.deleteById(id);
    }
}
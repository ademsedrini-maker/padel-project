package be.ephec.padel_backend.service;

import be.ephec.padel_backend.model.Creneau;
import be.ephec.padel_backend.model.Site;
import be.ephec.padel_backend.model.Terrain;
import be.ephec.padel_backend.repository.CreneauRepository;
import be.ephec.padel_backend.repository.FermetureSiteRepository;
import be.ephec.padel_backend.repository.TerrainRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CreneauService {

    private final CreneauRepository creneauRepository;
    private final TerrainRepository terrainRepository;
    private final FermetureSiteRepository fermetureSiteRepository;

    public CreneauService(CreneauRepository creneauRepository,
                          TerrainRepository terrainRepository,
                          FermetureSiteRepository fermetureSiteRepository) {
        this.creneauRepository = creneauRepository;
        this.terrainRepository = terrainRepository;
        this.fermetureSiteRepository = fermetureSiteRepository;
    }

    public List<Creneau> getAllCreneaux() {
        return creneauRepository.findAll();
    }

    public List<Creneau> getCreneauxDisponiblesByTerrain(Long terrainId) {
        return creneauRepository.findByTerrainIdAndDisponibleTrue(terrainId);
    }

    public List<Creneau> getCreneauxBySite(Long siteId) {
        return creneauRepository.findByTerrainSiteId(siteId);
    }

    /**
     * Génère tous les créneaux pour un terrain pour une année civile.
     * Durée de chaque créneau : 1h30. Battement entre matchs : 15 min.
     * → nouveau créneau toutes les 1h45.
     * Les jours de fermeture (globaux et par site) sont ignorés.
     */
    public List<Creneau> genererCreneauxAnnuels(Long terrainId, int annee) {
        Terrain terrain = terrainRepository.findById(terrainId)
                .orElseThrow(() -> new RuntimeException("Terrain introuvable : " + terrainId));

        Site site = terrain.getSite();
        LocalTime ouverture = site.getHeureOuverture();
        LocalTime fermeture = site.getHeureFermeture();

        LocalDate debut = LocalDate.of(annee, 1, 1);
        LocalDate fin = LocalDate.of(annee, 12, 31);

        List<Creneau> creneauxGeneres = new ArrayList<>();

        for (LocalDate jour = debut; !jour.isAfter(fin); jour = jour.plusDays(1)) {

            boolean jourFerme = fermetureSiteRepository.estJourFerme(jour, site);

            if (jourFerme) continue;

            LocalTime heureDebut = ouverture;

            while (!heureDebut.plusMinutes(90).isAfter(fermeture)) {
                LocalDateTime dateHeureDebut = LocalDateTime.of(jour, heureDebut);
                LocalDateTime dateHeureFin = dateHeureDebut.plusMinutes(90);

                boolean dejaExistant = creneauRepository
                        .existsByTerrainIdAndDateHeureDebut(terrainId, dateHeureDebut);

                if (!dejaExistant) {
                    Creneau creneau = new Creneau();
                    creneau.setDateHeureDebut(dateHeureDebut);
                    creneau.setDateHeureFin(dateHeureFin);
                    creneau.setDisponible(true);
                    creneau.setTerrain(terrain);
                    creneauxGeneres.add(creneauRepository.save(creneau));
                }

                // Avancer de 1h45 (1h30 de match + 15 min de battement)
                heureDebut = heureDebut.plusMinutes(105);
            }
        }

        return creneauxGeneres;
    }
}
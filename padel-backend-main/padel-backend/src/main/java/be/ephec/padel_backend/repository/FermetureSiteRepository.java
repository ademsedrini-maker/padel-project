package be.ephec.padel_backend.repository;

import be.ephec.padel_backend.model.FermetureSite;
import be.ephec.padel_backend.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface FermetureSiteRepository extends JpaRepository<FermetureSite, Long> {

    // Fermetures d'un site spécifique
    List<FermetureSite> findBySite(Site site);

    // Fermetures globales uniquement (site = null)
    List<FermetureSite> findBySiteIsNull();

    // Vérifier si une date est fermée pour un site donné
    // (fermeture globale OU fermeture spécifique au site)
    @Query("SELECT COUNT(f) > 0 FROM FermetureSite f WHERE f.date = :date AND (f.site IS NULL OR f.site = :site)")
    boolean estJourFerme(@Param("date") LocalDate date, @Param("site") Site site);
}

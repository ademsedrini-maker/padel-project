package be.ephec.padel_backend.repository;

import be.ephec.padel_backend.model.FermetureSite;
import be.ephec.padel_backend.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FermetureSiteRepository extends JpaRepository<FermetureSite, Long> {

    List<FermetureSite> findBySite(Site site);

    List<FermetureSite> findBySiteIsNull();

    @Query("""
        SELECT COUNT(f) > 0
        FROM FermetureSite f
        WHERE f.date = :date
        AND (f.site IS NULL OR f.site = :site)
    """)
    boolean estJourFerme(@Param("date") LocalDate date, @Param("site") Site site);
}
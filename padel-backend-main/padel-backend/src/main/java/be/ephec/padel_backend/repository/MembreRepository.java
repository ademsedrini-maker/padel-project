package be.ephec.padel_backend.repository;

import be.ephec.padel_backend.enums.TypeMembre;
import be.ephec.padel_backend.model.Membre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MembreRepository extends JpaRepository<Membre, Long> {

    Optional<Membre> findByMatricule(String matricule);

    List<Membre> findByTypeMembre(TypeMembre typeMembre);

    List<Membre> findBySiteId(Long siteId);

    @Query("SELECT m FROM Membre m WHERE m.solde > 0")
    List<Membre> findMembresAvecSolde();

    @Query("SELECT m FROM Membre m WHERE m.penaliteExpiration IS NOT NULL AND m.penaliteExpiration >= :today")
    List<Membre> findMembresEnPenalite(@Param("today") LocalDate today);

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END " +
            "FROM Membre m WHERE m.id = :membreId " +
            "AND m.penaliteExpiration IS NOT NULL AND m.penaliteExpiration >= :today")
    boolean hasPenaliteActive(@Param("membreId") Long membreId, @Param("today") LocalDate today);

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END " +
            "FROM Membre m WHERE m.id = :membreId AND m.solde > 0")
    boolean hasSoldeDu(@Param("membreId") Long membreId);

    @Query("SELECT m FROM Membre m WHERE m.typeMembre = 'GLOBAL' " +
            "OR m.typeMembre = 'LIBRE' " +
            "OR (m.typeMembre = 'SITE' AND m.site.id = :siteId)")
    List<Membre> findMembresVisiblesSurSite(@Param("siteId") Long siteId);
}
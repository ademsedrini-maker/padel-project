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

    // Recherche par matricule (G/S/L + numéro)
    Optional<Membre> findByMatricule(String matricule);

    // Recherche par type de membre (GLOBAL, SITE, LIBRE)
    List<Membre> findByTypeMembre(TypeMembre typeMembre);

    // Tous les membres d'un site spécifique
    List<Membre> findBySiteId(Long siteId);

    // Membres avec un solde dû > 0 (ne peuvent pas créer de réservation)
    @Query("SELECT m FROM Membre m WHERE m.solde > 0")
    List<Membre> findMembresAvecSolde();

    // Membres actuellement en période de pénalité
    @Query("SELECT m FROM Membre m WHERE m.penaliteExpiration IS NOT NULL AND m.penaliteExpiration >= :today")
    List<Membre> findMembresEnPenalite(@Param("today") LocalDate today);

    // Vérifier si un membre a encore une pénalité active (utile dans le service)
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END " +
            "FROM Membre m WHERE m.id = :membreId " +
            "AND m.penaliteExpiration IS NOT NULL AND m.penaliteExpiration >= :today")
    boolean hasPenaliteActive(@Param("membreId") Long membreId, @Param("today") LocalDate today);

    // Vérifier si un membre a un solde dû (utile dans le service avant création de réservation)
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END " +
            "FROM Membre m WHERE m.id = :membreId AND m.solde > 0")
    boolean hasSoldeDu(@Param("membreId") Long membreId);

    // Membres visibles sur un site donné (tous les types peuvent être vus sur tous les sites)
    @Query("SELECT m FROM Membre m WHERE m.typeMembre = 'GLOBAL' " +
            "OR m.typeMembre = 'LIBRE' " +
            "OR (m.typeMembre = 'SITE' AND m.site.id = :siteId)")
    List<Membre> findMembresVisiblesSurSite(@Param("siteId") Long siteId);
}
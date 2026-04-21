package be.ephec.padel_backend.repository;

import be.ephec.padel_backend.enums.StatutMatch;
import be.ephec.padel_backend.enums.TypeMatch;
import be.ephec.padel_backend.model.MatchPadel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MatchPadelRepository extends JpaRepository<MatchPadel, Long> {

    // Par type (PRIVE / PUBLIC)
    List<MatchPadel> findByTypeMatch(TypeMatch typeMatch);

    // Par statut (EN_ATTENTE / CONFIRME / ANNULE)
    List<MatchPadel> findByStatut(StatutMatch statut);

    // Par organisateur
    List<MatchPadel> findByOrganisateurId(Long organisateurId);

    // Par site (pour filtrer les matchs d'un site)
    List<MatchPadel> findByCreneauTerrainSiteId(Long siteId);

    // Par plage de dates → utilisé par le scheduler
    // ⚠️ "DateHeure" correspond au champ dans Creneau.java
    List<MatchPadel> findByCreneauDateHeureDebutBetween(LocalDateTime start, LocalDateTime end);

    // Par type + statut combinés
    List<MatchPadel> findByTypeMatchAndStatut(TypeMatch typeMatch, StatutMatch statut);

    // Vérifier si un créneau est déjà réservé
    boolean existsByCreneauId(Long creneauId);
}
package be.ephec.padel_backend.repository;

import be.ephec.padel_backend.enums.StatutParticipant;
import be.ephec.padel_backend.model.MatchPadel;
import be.ephec.padel_backend.model.Membre;
import be.ephec.padel_backend.model.ParticipantMatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantMatchRepository extends JpaRepository<ParticipantMatch, Long> {

    // Par match (objet)
    List<ParticipantMatch> findByMatch(MatchPadel match);

    // Par membre (objet)
    List<ParticipantMatch> findByMembre(Membre membre);

    // Par match + statut
    List<ParticipantMatch> findByMatchAndStatut(MatchPadel match, StatutParticipant statut);

    // Compter les participants d'un match
    long countByMatch(MatchPadel match);

    // Compter les participants d'un match par statut (ex: combien ont PAYE)
    long countByMatchAndStatut(MatchPadel match, StatutParticipant statut);

    // Vérifier si un membre est déjà dans un match
    boolean existsByMatchAndMembre(MatchPadel match, Membre membre);

    // Trouver un participant spécifique dans un match
    Optional<ParticipantMatch> findByMatchAndMembre(MatchPadel match, Membre membre);

    // Par statut + plage de dates (utilisé par le scheduler)
    List<ParticipantMatch> findByStatut(StatutParticipant statut);
}
package be.ephec.padel_backend.repository;

import be.ephec.padel_backend.enums.StatutParticipant;
import be.ephec.padel_backend.model.ParticipantMatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantMatchRepository extends JpaRepository<ParticipantMatch, Long> {

    List<ParticipantMatch> findByMatchPadelId(Long matchId);

    List<ParticipantMatch> findByMembreId(Long membreId);

    List<ParticipantMatch> findByMatchPadelIdAndStatut(Long matchId, StatutParticipant statut);

    long countByMatchPadelId(Long matchId);

    long countByMatchPadelIdAndStatut(Long matchId, StatutParticipant statut);

    boolean existsByMatchPadelIdAndMembreId(Long matchId, Long membreId);

    Optional<ParticipantMatch> findByMatchPadelIdAndMembreId(Long matchId, Long membreId);
}
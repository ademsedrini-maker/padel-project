package be.ephec.padel_backend.repository;

import be.ephec.padel_backend.model.ParticipantMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantMatchRepository extends JpaRepository<ParticipantMatch, Long> {
}
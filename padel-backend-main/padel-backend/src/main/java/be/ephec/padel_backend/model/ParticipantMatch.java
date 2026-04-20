package be.ephec.padel_backend.model;

import be.ephec.padel_backend.enums.StatutParticipant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "participant_match")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    @JsonIgnore
    private MatchPadel matchPadel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membre_id", nullable = false)
    private Membre membre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutParticipant statut;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montantPaye = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean ajouteParOrganisateur = false;

    @Column(nullable = false)
    private LocalDateTime dateInscription = LocalDateTime.now();

    private LocalDateTime datePaiement;
}
package be.ephec.padel_backend.model;

import be.ephec.padel_backend.enums.StatutMatch;
import be.ephec.padel_backend.enums.TypeMatch;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "match_padel")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchPadel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TypeMatch typeMatch;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutMatch statut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisateur_id", nullable = false)
    private Membre organisateur;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creneau_id", nullable = false, unique = true)
    private Creneau creneau;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTotal = BigDecimal.valueOf(60.00);

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montantParJoueur = BigDecimal.valueOf(15.00);

    @Column(nullable = false)
    private Integer nombreMaxJoueurs = 4;

    @Column(nullable = false)
    private Boolean estDevenuPublicAutomatiquement = false;

    @Column(nullable = false)
    private Boolean soldeOrganisateurRegle = false;

    @Column(nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    @OneToMany(mappedBy = "matchPadel", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ParticipantMatch> participants = new ArrayList<>();
}
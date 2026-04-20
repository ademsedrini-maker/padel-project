package be.ephec.padel_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "creneau")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Creneau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateHeureDebut;

    @Column(nullable = false)
    private LocalDateTime dateHeureFin;

    @Column(nullable = false)
    private Boolean disponible = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terrain_id", nullable = false)
    private Terrain terrain;

    @OneToOne(mappedBy = "creneau")
    @JsonIgnore
    private MatchPadel matchPadel;

    public Creneau(LocalDateTime dateHeureDebut, Terrain terrain) {
        this.dateHeureDebut = dateHeureDebut;
        this.dateHeureFin = dateHeureDebut.plusMinutes(90);
        this.disponible = true;
        this.terrain = terrain;
    }
}
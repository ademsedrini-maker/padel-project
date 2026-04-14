package be.ephec.padel_backend.model;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "site")
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    private String adresse;

    @Column(nullable = false)
    private LocalTime heureOuverture;

    @Column(nullable = false)
    private LocalTime heureFermeture;

    @Column(nullable = false)
    private Integer anneeHoraire;

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public LocalTime getHeureOuverture() { return heureOuverture; }
    public void setHeureOuverture(LocalTime heureOuverture) { this.heureOuverture = heureOuverture; }
    public LocalTime getHeureFermeture() { return heureFermeture; }
    public void setHeureFermeture(LocalTime heureFermeture) { this.heureFermeture = heureFermeture; }
    public Integer getAnneeHoraire() { return anneeHoraire; }
    public void setAnneeHoraire(Integer anneeHoraire) { this.anneeHoraire = anneeHoraire; }
}

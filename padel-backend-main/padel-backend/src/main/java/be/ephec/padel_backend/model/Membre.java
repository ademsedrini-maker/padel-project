package be.ephec.padel_backend.model;

import be.ephec.padel_backend.enums.TypeMembre;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "membre")
public class Membre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String matricule;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeMembre typeMembre;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;

    private LocalDate penaliteExpiration;

    @Column(precision = 10, scale = 2)
    private BigDecimal solde = BigDecimal.ZERO;

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public TypeMembre getTypeMembre() { return typeMembre; }
    public void setTypeMembre(TypeMembre typeMembre) { this.typeMembre = typeMembre; }
    public Site getSite() { return site; }
    public void setSite(Site site) { this.site = site; }
    public LocalDate getPenaliteExpiration() { return penaliteExpiration; }
    public void setPenaliteExpiration(LocalDate penaliteExpiration) { this.penaliteExpiration = penaliteExpiration; }
    public BigDecimal getSolde() { return solde; }
    public void setSolde(BigDecimal solde) { this.solde = solde; }
}
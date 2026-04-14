package be.ephec.padel_backend.model;

import be.ephec.padel_backend.enums.StatutMatch;
import be.ephec.padel_backend.enums.TypeMatch;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "match_padel")
public class MatchPadel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "creneau_id", nullable = false)
    private Creneau creneau;

    @ManyToOne
    @JoinColumn(name = "organisateur_id", nullable = false)
    private Membre organisateur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeMatch typeMatch;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutMatch statut = StatutMatch.EN_ATTENTE;

    @Column(precision = 10, scale = 2)
    private BigDecimal montantTotal = new BigDecimal("60.00");

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL)
    private List<ParticipantMatch> participants;

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Creneau getCreneau() { return creneau; }
    public void setCreneau(Creneau creneau) { this.creneau = creneau; }
    public Membre getOrganisateur() { return organisateur; }
    public void setOrganisateur(Membre organisateur) { this.organisateur = organisateur; }
    public TypeMatch getTypeMatch() { return typeMatch; }
    public void setTypeMatch(TypeMatch typeMatch) { this.typeMatch = typeMatch; }
    public StatutMatch getStatut() { return statut; }
    public void setStatut(StatutMatch statut) { this.statut = statut; }
    public BigDecimal getMontantTotal() { return montantTotal; }
    public void setMontantTotal(BigDecimal montantTotal) { this.montantTotal = montantTotal; }
    public List<ParticipantMatch> getParticipants() { return participants; }
    public void setParticipants(List<ParticipantMatch> participants) { this.participants = participants; }
}

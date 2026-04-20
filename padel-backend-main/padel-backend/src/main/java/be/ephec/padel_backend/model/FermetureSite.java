package be.ephec.padel_backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "fermeture_site")
public class FermetureSite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Si site == null → fermeture GLOBALE (tous les sites)
     * Si site != null → fermeture spécifique à ce site
     */
    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;

    @Column(nullable = false)
    private LocalDate date;

    private String motif;

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Site getSite() { return site; }
    public void setSite(Site site) { this.site = site; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    // Utilitaire : true si c'est une fermeture globale
    public boolean isGlobale() { return this.site == null; }
}
package be.ephec.padel_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "terrain")
public class Terrain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer numero;

    @ManyToOne
    @JoinColumn(name = "site_id", nullable = false)
    @JsonIgnore
    private Site site;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public Site getSite() { return site; }
    public void setSite(Site site) { this.site = site; }
}

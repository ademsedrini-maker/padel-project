package be.ephec.padel_backend.dto;

public class CreneauDTO {

    private Long id;
    private String dateHeureDebut;
    private String dateHeureFin;
    private boolean disponible;
    private Integer terrainNumero;
    private String siteNom;

    public CreneauDTO(Long id,
                      String dateHeureDebut,
                      String dateHeureFin,
                      boolean disponible,
                      Integer terrainNumero,
                      String siteNom) {
        this.id = id;
        this.dateHeureDebut = dateHeureDebut;
        this.dateHeureFin = dateHeureFin;
        this.disponible = disponible;
        this.terrainNumero = terrainNumero;
        this.siteNom = siteNom;
    }

    public Long getId() {
        return id;
    }

    public String getDateHeureDebut() {
        return dateHeureDebut;
    }

    public String getDateHeureFin() {
        return dateHeureFin;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public Integer getTerrainNumero() {
        return terrainNumero;
    }

    public String getSiteNom() {
        return siteNom;
    }
}
package be.ephec.padel_backend.dto;

import java.time.LocalTime;

public record TerrainResponse(
        Long id,
        Integer numero,
        String siteNom,
        String adresse,
        LocalTime heureOuverture,
        LocalTime heureFermeture,
        String description,
        String imageUrl
) {}

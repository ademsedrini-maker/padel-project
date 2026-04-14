package be.ephec.padel_backend.controller;

import be.ephec.padel_backend.model.Site;
import be.ephec.padel_backend.service.SiteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sites")
@CrossOrigin(origins = "*")
public class SiteController {

    private final SiteService siteService;

    public SiteController(SiteService siteService) {
        this.siteService = siteService;
    }

    @GetMapping
    public List<Site> getAllSites() {
        return siteService.getAllSites();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Site> getSiteById(@PathVariable Long id) {
        return ResponseEntity.ok(siteService.getSiteById(id));
    }

    @PostMapping
    public ResponseEntity<Site> createSite(@RequestBody Site site) {
        return ResponseEntity.ok(siteService.createSite(site));
    }
}
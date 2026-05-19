import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../../layout/navbar/navbar';
import { Auth } from '../../core/services/auth';

interface Creneau {
  id: number;
  dateHeureDebut: string;
  dateHeureFin: string;
  disponible: boolean;
  terrainNumero: number;
  siteNom: string;
}

interface TerrainFiltre {
  id: number;
  nom: string;
  site: string;
}

interface Reservation {
  id: number;
  creneau: Creneau;
  statut: string;
  typeMatch: string;
  montantTotal: number;
}

@Component({
  selector: 'app-reservations',
  standalone: true,
  imports: [CommonModule, NavbarComponent, FormsModule],
  templateUrl: './reservations.html',
  styleUrls: ['./reservations.css']
})
export class Reservations implements OnInit {
  private apiUrl = 'http://localhost:8080/api';

  tousCreneaux: Creneau[] = [];
  creneauxFiltres: Creneau[] = [];
  mesReservations: Reservation[] = [];
  terrains: TerrainFiltre[] = [];

  dateSelectionnee: string = '';
  terrainSelectionne: number | null = null;
  creneauSelectionne: number | null = null;
  typeMatch: string = 'SIMPLE';

  message: string = '';
  erreur: string = '';
  onglet: 'reserver' | 'futures' | 'historique' = 'reserver';

  constructor(
    private http: HttpClient,
    public authService: Auth
  ) {}

  ngOnInit(): void {
    this.chargerCreneaux();
    this.chargerMesReservations();
  }

  chargerCreneaux(): void {
    this.http.get<Creneau[]>(`${this.apiUrl}/creneaux`).subscribe({
      next: (data) => {
        const user = this.authService.currentUser();
        const now = new Date();

        // Créneaux disponibles et dans le futur
        let creneauxDispos = data.filter(c => c.disponible && new Date(c.dateHeureDebut) > now);

        // Filtre par site pour les membres de type SITE
        if (user?.typeMembre === 'SITE' && user?.site) {
          creneauxDispos = creneauxDispos.filter(c => c.siteNom === user.site);
        }

        this.tousCreneaux = creneauxDispos;
        this.creneauxFiltres = creneauxDispos;

        // Construire la liste des terrains uniques pour le select
        const ids = new Set<string>();
        this.terrains = creneauxDispos
          .map(c => ({
            id: c.terrainNumero,
            nom: `Terrain ${c.terrainNumero}`,
            site: c.siteNom
          }))
          .filter(t => {
            const key = `${t.site}-${t.id}`;
            if (ids.has(key)) return false;
            ids.add(key);
            return true;
          });
      },
      error: () => {
        this.erreur = 'Impossible de charger les créneaux.';
      }
    });
  }

  filtrerCreneaux(): void {
    this.creneauxFiltres = this.tousCreneaux.filter(c => {
      const matchDate = this.dateSelectionnee
        ? c.dateHeureDebut.startsWith(this.dateSelectionnee)
        : true;
      const matchTerrain = this.terrainSelectionne
        ? c.terrainNumero === +this.terrainSelectionne
        : true;
      return matchDate && matchTerrain;
    });
    this.creneauSelectionne = null;
  }

  chargerMesReservations(): void {
    const matricule = this.authService.currentUser()?.matricule;
    if (!matricule) return;

    this.http.get<Reservation[]>(`${this.apiUrl}/matchs/membre/${matricule}`).subscribe({
      next: (data) => {
        this.mesReservations = data;
      },
      error: () => {}
    });
  }

  get reservationsFutures(): Reservation[] {
    const now = new Date();
    return this.mesReservations.filter(r =>
      new Date(r.creneau.dateHeureDebut) >= now && r.statut !== 'ANNULE'
    );
  }

  get reservationsPassees(): Reservation[] {
    const now = new Date();
    return this.mesReservations.filter(r =>
      new Date(r.creneau.dateHeureDebut) < now || r.statut === 'ANNULE'
    );
  }

  reserver(): void {
    this.message = '';
    this.erreur = '';

    const user = this.authService.currentUser();
    if (!user?.matricule) { this.erreur = 'Vous devez être connecté.'; return; }
    if (!this.creneauSelectionne) { this.erreur = 'Veuillez sélectionner un créneau.'; return; }
    if (!user.id) { this.erreur = 'Impossible de récupérer votre identifiant.'; return; }

    const body = {
      organisateurId: user.id.toString(),
      creneauId: this.creneauSelectionne.toString(),
      typeMatch: this.typeMatch
    };

    this.http.post<Reservation>(`${this.apiUrl}/matchs/create`, body).subscribe({
      next: () => {
        this.message = '✅ Réservation effectuée avec succès !';
        this.creneauSelectionne = null;
        this.dateSelectionnee = '';
        this.terrainSelectionne = null;
        this.chargerCreneaux();
        this.chargerMesReservations();
        this.onglet = 'futures';
      },
      error: (err) => {
        this.erreur = err.error?.message || 'Erreur lors de la réservation.';
      }
    });
  }

  annuler(reservationId: number): void {
    if (!confirm('Confirmer l\'annulation ?')) return;

    this.http.put(`${this.apiUrl}/matchs/${reservationId}/annuler`, {}).subscribe({
      next: () => {
        this.message = 'Réservation annulée.';
        this.chargerMesReservations();
      },
      error: () => {
        this.erreur = 'Impossible d\'annuler.';
      }
    });
  }

  formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('fr-BE', {
      weekday: 'long',
      day: '2-digit',
      month: 'long',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  getStatutClass(statut: string): string {
    switch (statut) {
      case 'EN_ATTENTE': return 'badge-attente';
      case 'CONFIRME':   return 'badge-confirme';
      case 'ANNULE':     return 'badge-annule';
      default:           return '';
    }
  }

  getStatutLabel(statut: string): string {
    switch (statut) {
      case 'EN_ATTENTE': return '⏳ En attente';
      case 'CONFIRME':   return '✅ Confirmé';
      case 'ANNULE':     return '❌ Annulé';
      default:           return statut;
    }
  }

  getTypeMembreInfo(): string {
    const user = this.authService.currentUser();
    switch (user?.typeMembre) {
      case 'GLOBAL': return '🌍 Membre Global — accès à tous les sites';
      case 'SITE':   return `🏠 Membre Site — accès au site ${user.site} uniquement`;
      case 'LIBRE':  return '🎟️ Membre Libre — accès à tous les sites';
      default:       return '';
    }
  }

  today(): string {
    return new Date().toISOString().split('T')[0];
  }
}

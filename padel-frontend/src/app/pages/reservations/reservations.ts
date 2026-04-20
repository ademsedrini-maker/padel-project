import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { NavbarComponent } from '../../layout/navbar/navbar';
import { Auth } from '../../core/services/auth';

interface Terrain {
  id: number;
  nom: string;
  site: string;
}

interface Creneau {
  id: number;
  dateHeure: string;
  terrain: Terrain;
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
  imports: [CommonModule, NavbarComponent, FormsModule, RouterLink],
  templateUrl: './reservations.html',
  styleUrls: ['./reservations.css']
})
export class Reservations implements OnInit {
  private apiUrl = 'http://localhost:8080/api';

  // Données
  tousCreneaux: Creneau[] = [];
  creneauxFiltres: Creneau[] = [];
  mesReservations: Reservation[] = [];
  terrains: Terrain[] = [];

  // Filtres de réservation
  dateSelectionnee: string = '';
  terrainSelectionne: number | null = null;
  creneauSelectionne: number | null = null;
  typeMatch: string = 'SIMPLE';

  // UI
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
        // Filtrage selon type de membre
        const user = this.authService.currentUser();
        const now = new Date();

        let creneauxDispos = data.filter(c => new Date(c.dateHeure) > now);

        if (user?.typeMembre === 'SITE' && user?.site) {
          creneauxDispos = creneauxDispos.filter(c => c.terrain.site === user.site);
        }
        // GLOBAL et LIBRE voient tout

        this.tousCreneaux = creneauxDispos;
        this.creneauxFiltres = creneauxDispos;

        // Extraire les terrains uniques
        const ids = new Set<number>();
        this.terrains = creneauxDispos
          .map(c => c.terrain)
          .filter(t => { if (ids.has(t.id)) return false; ids.add(t.id); return true; });
      },
      error: () => this.erreur = 'Impossible de charger les créneaux.'
    });
  }

  filtrerCreneaux(): void {
    this.creneauxFiltres = this.tousCreneaux.filter(c => {
      const matchDate = this.dateSelectionnee
        ? c.dateHeure.startsWith(this.dateSelectionnee)
        : true;
      const matchTerrain = this.terrainSelectionne
        ? c.terrain.id === +this.terrainSelectionne
        : true;
      return matchDate && matchTerrain;
    });
    this.creneauSelectionne = null;
  }

  chargerMesReservations(): void {
    const matricule = this.authService.currentUser()?.matricule;
    if (!matricule) return;
    this.http.get<Reservation[]>(`${this.apiUrl}/matches/membre/${matricule}`).subscribe({
      next: (data) => this.mesReservations = data,
      error: () => {}
    });
  }

  get reservationsFutures(): Reservation[] {
    const now = new Date();
    return this.mesReservations.filter(r =>
      new Date(r.creneau.dateHeure) >= now && r.statut !== 'ANNULE'
    );
  }

  get reservationsPassees(): Reservation[] {
    const now = new Date();
    return this.mesReservations.filter(r =>
      new Date(r.creneau.dateHeure) < now || r.statut === 'ANNULE'
    );
  }

  reserver(): void {
    this.message = '';
    this.erreur = '';
    const matricule = this.authService.currentUser()?.matricule;

    if (!matricule) { this.erreur = 'Vous devez être connecté.'; return; }
    if (!this.creneauSelectionne) { this.erreur = 'Veuillez sélectionner un créneau.'; return; }

    const body = {
      creneauId: this.creneauSelectionne,
      organisateurMatricule: matricule,
      typeMatch: this.typeMatch
    };

    this.http.post(`${this.apiUrl}/matches`, body).subscribe({
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
    this.http.delete(`${this.apiUrl}/matches/${reservationId}`).subscribe({
      next: () => {
        this.message = 'Réservation annulée.';
        this.chargerMesReservations();
      },
      error: () => this.erreur = 'Impossible d\'annuler.'
    });
  }

  formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('fr-BE', {
      weekday: 'long', day: '2-digit', month: 'long',
      year: 'numeric', hour: '2-digit', minute: '2-digit'
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
}

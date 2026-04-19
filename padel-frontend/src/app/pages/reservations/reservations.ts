import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Navbar } from '../../layout/navbar/navbar';
import { Auth } from '../../core/services/auth';
import { FormsModule } from '@angular/forms';

interface Creneau {
  id: number;
  dateHeure: string;
  terrain: { id: number; nom: string; site: string };
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
  imports: [CommonModule, Navbar, FormsModule],
  templateUrl: './reservations.html',
  styleUrl: './reservations.css'
})
export class Reservations implements OnInit {
  private apiUrl = 'http://localhost:8080/api';

  creneaux: Creneau[] = [];
  mesReservations: Reservation[] = [];
  creneauSelectionne: number | null = null;
  typeMatch: string = 'SIMPLE';
  message: string = '';
  erreur: string = '';

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
      next: data => this.creneaux = data,
      error: () => this.erreur = 'Impossible de charger les créneaux.'
    });
  }

  chargerMesReservations(): void {
    const matricule = this.authService.currentUser()?.matricule;
    if (!matricule) return;
    this.http.get<Reservation[]>(`${this.apiUrl}/matches/membre/${matricule}`).subscribe({
      next: data => this.mesReservations = data,
      error: () => {}
    });
  }

  reserver(): void {
    this.message = '';
    this.erreur = '';
    const matricule = this.authService.currentUser()?.matricule;
    if (!matricule || !this.creneauSelectionne) {
      this.erreur = 'Veuillez sélectionner un créneau.';
      return;
    }
    const body = {
      creneauId: this.creneauSelectionne,
      organisateurMatricule: matricule,
      typeMatch: this.typeMatch
    };
    this.http.post(`${this.apiUrl}/matches`, body).subscribe({
      next: () => {
        this.message = 'Réservation effectuée avec succès !';
        this.creneauSelectionne = null;
        this.chargerMesReservations();
        this.chargerCreneaux();
      },
      error: (err) => {
        this.erreur = err.error?.message || 'Erreur lors de la réservation.';
      }
    });
  }

  annuler(reservationId: number): void {
    this.http.delete(`${this.apiUrl}/matches/${reservationId}`).subscribe({
      next: () => {
        this.message = 'Réservation annulée.';
        this.chargerMesReservations();
      },
      error: () => this.erreur = 'Impossible d\'annuler.'
    });
  }

  formatDate(dateStr: string): string {
    const d = new Date(dateStr);
    return d.toLocaleDateString('fr-BE', {
      weekday: 'long', day: '2-digit', month: 'long',
      year: 'numeric', hour: '2-digit', minute: '2-digit'
    });
  }
}

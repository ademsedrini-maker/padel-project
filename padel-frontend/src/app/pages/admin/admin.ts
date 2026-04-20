import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { NavbarComponent } from '../../layout/navbar/navbar';
import { Auth } from '../../core/services/auth';

interface Reservation {
  id: number;
  creneau: { dateHeure: string; terrain: { nom: string; site: string } };
  organisateur: { nom: string; prenom: string; matricule: string };
  statut: string;
  typeMatch: string;
  montantTotal: number;
}

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './admin.html',
  styleUrls: ['./admin.css']
})
export class Admin implements OnInit {
  private apiUrl = 'http://localhost:8080/api';
  reservations: Reservation[] = [];
  erreur: string = '';
  message: string = '';

  constructor(
    private http: HttpClient,
    public authService: Auth
  ) {}

  ngOnInit(): void {
    this.chargerReservations();
  }

  chargerReservations(): void {
    this.http.get(`${this.apiUrl}/matches`).subscribe({
      next: (data: any) => this.reservations = data,
      error: () => this.erreur = 'Impossible de charger les réservations.'
    });
  }

  confirmer(id: number): void {
    this.http.put(`${this.apiUrl}/matches/${id}/confirmer`, {}).subscribe({
      next: () => { this.message = 'Réservation confirmée.'; this.chargerReservations(); },
      error: () => this.erreur = 'Erreur lors de la confirmation.'
    });
  }

  annuler(id: number): void {
    this.http.put(`${this.apiUrl}/matches/${id}/annuler`, {}).subscribe({
      next: () => { this.message = 'Réservation annulée.'; this.chargerReservations(); },
      error: () => this.erreur = 'Erreur lors de l\'annulation.'
    });
  }

  formatDate(dateStr: string): string {
    const d = new Date(dateStr);
    return d.toLocaleDateString('fr-BE', {
      day: '2-digit', month: '2-digit', year: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });
  }
}

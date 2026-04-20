import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { NavbarComponent } from '../../layout/navbar/navbar';
import { Auth } from '../../core/services/auth';

interface StatsData {
  totalMembres: number;
  totalTerrains: number;
  totalReservations: number;
  recetteTotale: number;
}

@Component({
  selector: 'app-stats',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './stats.html',
  styleUrls: ['./stats.css']
})
export class Stats implements OnInit {
  private apiUrl = 'http://localhost:8080/api';
  stats: StatsData | null = null;
  erreur: string = '';

  constructor(
    private http: HttpClient,
    public authService: Auth
  ) {}

  ngOnInit(): void {
    this.http.get(`${this.apiUrl}/stats`).subscribe({
      next: (data: any) => this.stats = data,
      error: () => this.erreur = 'Erreur lors du chargement des statistiques.'
    });
  }
}

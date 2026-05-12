import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { NavbarComponent } from '../../layout/navbar/navbar';
import { Auth } from '../../core/services/auth';

interface Terrain {
  id: number;
  numero: number;
  site?: {
    id: number;
    nom: string;
    adresse: string;
    heureOuverture: string;
    heureFermeture: string;
  };
}

@Component({
  selector: 'app-terrains',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './terrains.html',
  styleUrls: ['./terrains.css']
})
export class Terrains implements OnInit {
  private apiUrl = 'http://localhost:8080/api';

  terrains: Terrain[] = [];
  erreur: string = '';
  chargement = true;

  constructor(
    private http: HttpClient,
    public authService: Auth
  ) {}

  ngOnInit(): void {
    this.http.get<Terrain[]>(`${this.apiUrl}/terrains`).subscribe({
      next: (data) => { this.terrains = data; this.chargement = false; },
      error: () => { this.erreur = 'Impossible de charger les terrains.'; this.chargement = false; }
    });
  }
}

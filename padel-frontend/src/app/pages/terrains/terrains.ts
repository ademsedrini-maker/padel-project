import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { NavbarComponent } from '../../layout/navbar/navbar';
import { Auth } from '../../core/services/auth';

interface Terrain {
  id: number;
  numero: number;
  siteNom: string;
  adresse: string;
  heureOuverture: string;
  heureFermeture: string;
  description: string;
  imageUrl: string;
}

@Component({
  selector: 'app-terrains',
  standalone: true,
  imports: [CommonModule, RouterLink, NavbarComponent],
  templateUrl: './terrains.html',
  styleUrls: ['./terrains.css']
})
export class Terrains implements OnInit {
  private apiUrl = 'http://localhost:8080/api';
  terrains: Terrain[] = [];
  erreur: string | null = null;
  chargement = false;

  constructor(
    private http: HttpClient,
    public authService: Auth
  ) {}

  ngOnInit(): void {
    this.chargerTerrains();
  }

  chargerTerrains(): void {
    this.chargement = true;
    this.erreur = null;

    this.http.get<Terrain[]>(`${this.apiUrl}/terrains`).subscribe({
      next: (data) => {
        this.terrains = data;
        this.chargement = false;
      },
      error: () => {
        this.erreur = 'Impossible de charger les terrains.';
        this.chargement = false;
      }
    });
  }
}

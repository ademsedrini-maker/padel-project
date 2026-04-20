import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { NavbarComponent } from '../../layout/navbar/navbar';
import { Auth } from '../../core/services/auth';

@Component({
  selector: 'app-terrains',
  standalone: true,
  imports: [CommonModule, RouterLink, NavbarComponent],
  templateUrl: './terrains.html',
  styleUrls: ['./terrains.css']
})
export class Terrains implements OnInit {
  private apiUrl = 'http://localhost:8080/api';
  terrains: any[] = [];
  erreur: string | null = null;
  chargement: boolean = false;

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
    this.http.get(`${this.apiUrl}/terrains`).subscribe({
      next: (data: any) => {
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

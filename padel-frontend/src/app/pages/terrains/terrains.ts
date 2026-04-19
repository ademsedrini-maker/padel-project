import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Navbar } from '../../layout/navbar/navbar';
import { Auth } from '../../core/services/auth';

interface Terrain {
  id: number;
  nom: string;
  site: string;
}

@Component({
  selector: 'app-terrains',
  imports: [CommonModule, Navbar],
  templateUrl: './terrains.html',
  styleUrl: './terrains.css'
})
export class Terrains implements OnInit {
  private apiUrl = 'http://localhost:8080/api';
  terrains: Terrain[] = [];
  erreur: string = '';

  constructor(
    private http: HttpClient,
    public authService: Auth
  ) {}

  ngOnInit(): void {
    this.http.get<Terrain[]>(`${this.apiUrl}/terrains`).subscribe({
      next: data => this.terrains = data,
      error: () => this.erreur = 'Impossible de charger les terrains.'
    });
  }
}

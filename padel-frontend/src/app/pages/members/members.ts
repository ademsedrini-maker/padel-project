import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { NavbarComponent } from '../../layout/navbar/navbar';
import { Auth } from '../../core/services/auth';

interface Membre {
  id: number;
  nom: string;
  prenom: string;
  matricule: string;
  email: string;
  typeMembre: string;
  site?: string;
  solde: number;
}

@Component({
  selector: 'app-members',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './members.html',
  styleUrls: ['./members.css']
})
export class Members implements OnInit {
  private apiUrl = 'http://localhost:8080/api';
  membres: Membre[] = [];
  erreur: string = '';

  constructor(
    private http: HttpClient,
    public authService: Auth
  ) {}

  ngOnInit(): void {
    const endpoint = this.authService.isAdminGlobal()
      ? `${this.apiUrl}/membres`
      : `${this.apiUrl}/membres/site/${this.authService.getSiteAdmin()}`;

    this.http.get(endpoint).subscribe({
      next: (data: any) => this.membres = data,
      error: () => this.erreur = 'Impossible de charger les membres.'
    });
  }

  getTypeBadgeClass(type: string): string {
    switch (type) {
      case 'GLOBAL': return 'badge-global';
      case 'SITE':   return 'badge-site';
      case 'LIBRE':  return 'badge-libre';
      default:       return '';
    }
  }
}

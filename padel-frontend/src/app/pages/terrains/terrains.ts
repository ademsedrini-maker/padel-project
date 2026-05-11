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
  telephone?: string;
  adresse?: string;
  dateNaissance?: string;
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
  profil: Membre | null = null;
  erreur: string = '';
  chargement = true;

  constructor(
    private http: HttpClient,
    public authService: Auth
  ) {}

  ngOnInit(): void {
    if (this.authService.isAdmin()) {
      const endpoint = this.authService.isAdminGlobal()
        ? `${this.apiUrl}/membres`
        : `${this.apiUrl}/membres/site/${this.authService.getSiteAdmin()}`;

      this.http.get<Membre[]>(endpoint).subscribe({
        next: (data) => { this.membres = data; this.chargement = false; },
        error: () => { this.erreur = 'Impossible de charger les membres.'; this.chargement = false; }
      });

    } else if (this.authService.isMember()) {
      const matricule = this.authService.getMatricule();
      this.http.get<Membre>(`${this.apiUrl}/membres/${matricule}`).subscribe({
        next: (data) => { this.profil = data; this.chargement = false; },
        error: () => { this.erreur = 'Impossible de charger votre profil.'; this.chargement = false; }
      });
    }
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

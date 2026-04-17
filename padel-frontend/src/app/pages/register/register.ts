import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-register',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {
  nom = '';
  prenom = '';
  email = '';

  matriculeGenere = '';
  errorMessage = '';
  inscriptionReussie = false;

  constructor(
    private http: HttpClient,
    public router: Router
  ) {}

  onRegister(): void {
    this.errorMessage = '';

    const membre = {
      nom: this.nom,
      prenom: this.prenom,
      email: this.email,
      typeMembre: 'LIBRE'
    };

    this.http.post<any>('http://localhost:8080/api/membres/register', membre).subscribe({
      next: (membreCreé) => {
        this.matriculeGenere = membreCreé.matricule;
        this.inscriptionReussie = true;
      },
      error: () => this.errorMessage = 'Erreur lors de l\'inscription'
    });
  }
}

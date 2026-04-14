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
  telephone = '';
  typeMembre = 'LIBRE';
  errorMessage = '';
  successMessage = '';

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  onRegister(): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.http.post('http://localhost:8080/api/membres/register', {
      nom: this.nom,
      prenom: this.prenom,
      email: this.email,
      telephone: this.telephone,
      typeMembre: this.typeMembre
    }).subscribe({
      next: () => {
        this.successMessage = 'Compte créé ! Vous pouvez maintenant vous connecter.';
        setTimeout(() => this.router.navigate(['/login-membre']), 2000);
      },
      error: () => this.errorMessage = 'Erreur lors de la création du compte'
    });
  }
}

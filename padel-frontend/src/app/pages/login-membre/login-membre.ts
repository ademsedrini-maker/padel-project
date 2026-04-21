import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { Auth } from '../../core/services/auth';

@Component({
  selector: 'app-login-membre',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './login-membre.html',
  styleUrl: './login-membre.css'
})
export class LoginMembre {
  matricule = '';
  errorMessage = '';

  constructor(private auth: Auth, private router: Router) {}

  onLogin(): void {
    if (!this.matricule.trim()) {
      this.errorMessage = 'Veuillez entrer votre matricule';
      return;
    }

    this.auth.login({ matricule: this.matricule.trim() }).subscribe({
      next: (user) => {
        this.errorMessage = '';
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        if (err.status === 404) {
          this.errorMessage = 'Matricule introuvable. Vérifiez votre saisie.';
        } else {
          this.errorMessage = 'Erreur de connexion. Réessayez plus tard.';
        }
      }
    });
  }
}

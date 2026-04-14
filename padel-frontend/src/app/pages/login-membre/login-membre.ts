import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { Auth } from '../../core/services/auth';

@Component({
  selector: 'app-login-membre',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login-membre.html',
  styleUrl: './login-membre.css'
})
export class LoginMembre {
  matricule = '';
  errorMessage = '';

  constructor(
    private authService: Auth,
    private router: Router
  ) {}

  onLogin(): void {
    this.errorMessage = '';

    this.authService.login({ matricule: this.matricule }).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: () => this.errorMessage = 'Matricule introuvable'
    });
  }
}

import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Auth } from '../../core/services/auth';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  matricule = '';
  password = '';
  errorMessage = '';

  constructor(
    private authService: Auth,
    private router: Router
  ) {}

  onLogin(): void {
    this.errorMessage = '';

    this.authService.login({
      matricule: this.matricule,
      password: this.password
    }).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: () => this.errorMessage = 'Connexion échouée'
    });
  }
}

import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { Auth } from '../../core/services/auth';

@Component({
  selector: 'app-login-admin',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login-admin.html',
  styleUrl: './login-admin.css'
})
export class LoginAdmin {
  username = '';
  password = '';
  errorMessage = '';

  constructor(
    private authService: Auth,
    private router: Router
  ) {}

  onLogin(): void {
    this.errorMessage = '';

    this.authService.loginAdmin({
      username: this.username,
      password: this.password
    }).subscribe({
      next: () => this.router.navigate(['/admin']),
      error: () => this.errorMessage = 'Identifiants admin incorrects'
    });
  }
}

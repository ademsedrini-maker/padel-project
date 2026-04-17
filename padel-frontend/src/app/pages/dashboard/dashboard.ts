import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Navbar } from '../../layout/navbar/navbar';
import { Auth } from '../../core/services/auth';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, Navbar],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard {
  constructor(public authService: Auth) {}
}

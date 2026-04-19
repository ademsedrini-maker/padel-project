import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Auth } from '../../core/services/auth';

@Component({
  selector: 'app-terrains',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './terrains.html',
  styleUrl: './terrains.css'
})
export class Terrains {
  constructor(public authService: Auth) {}
}

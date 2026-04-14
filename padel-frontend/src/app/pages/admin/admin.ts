import { Component } from '@angular/core';
import { Navbar } from '../../layout/navbar/navbar';

@Component({
  selector: 'app-admin',
  imports: [Navbar],
  templateUrl: './admin.html',
  styleUrl: './admin.css'
})
export class Admin {}

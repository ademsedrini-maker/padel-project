import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface User {
  id: number;
  nom: string;
  matricule: string;
  role: 'MEMBER' | 'ADMIN';
}

export interface LoginRequest {
  matricule: string;
  password?: string;
}

@Injectable({
  providedIn: 'root'
})
export class Auth {
  private apiUrl = 'http://localhost:8080/api/auth';
  currentUser = signal<User | null>(null);

  constructor(private http: HttpClient) {}

  login(data: LoginRequest): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/login`, data).pipe(
      tap(user => this.currentUser.set(user))
    );
  }

  logout(): void {
    this.currentUser.set(null);
  }

  isLoggedIn(): boolean {
    return this.currentUser() !== null;
  }

  isAdmin(): boolean {
    return this.currentUser()?.role === 'ADMIN';
  }
}

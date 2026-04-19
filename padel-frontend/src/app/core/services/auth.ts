import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface User {
  id?: number;
  nom: string;
  prenom?: string;
  matricule?: string;
  username?: string;
  role: 'MEMBER' | 'ADMIN';
  type?: 'GLOBAL' | 'SITE';
  typeMembre?: 'GLOBAL' | 'SITE' | 'LIBRE';
  site?: string;
}

export interface LoginMembreRequest {
  matricule: string;
}

export interface LoginAdminRequest {
  username: string;
  password: string;
}

@Injectable({
  providedIn: 'root'
})
export class Auth {
  private apiUrl = 'http://localhost:8080/api';
  currentUser = signal<User | null>(null);

  constructor(private http: HttpClient) {}

  login(data: LoginMembreRequest): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/membres/login`, data).pipe(
      tap((user) => this.currentUser.set({ ...user, role: 'MEMBER' }))
    );
  }

  loginAdmin(data: LoginAdminRequest): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/admin/login`, data).pipe(
      tap((user) => this.currentUser.set({ ...user, role: 'ADMIN' }))
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

  isMember(): boolean {
    return this.currentUser()?.role === 'MEMBER';
  }

  isAdminGlobal(): boolean {
    return this.currentUser()?.role === 'ADMIN' && this.currentUser()?.type === 'GLOBAL';
  }

  isAdminSite(): boolean {
    return this.currentUser()?.role === 'ADMIN' && this.currentUser()?.type === 'SITE';
  }

  getCurrentUser(): User | null {
    return this.currentUser();
  }

  getSiteAdmin(): string | undefined {
    return this.currentUser()?.site;
  }
}

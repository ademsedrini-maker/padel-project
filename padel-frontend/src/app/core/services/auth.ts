import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

// ─── INTERFACES ───────────────────────────────────────────────────────────────

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

// ─── SERVICE ──────────────────────────────────────────────────────────────────

@Injectable({ providedIn: 'root' })
export class Auth {
  private readonly apiUrl = 'http://localhost:8080/api';
  private readonly STORAGE_KEY = 'padel_current_user';

  currentUser = signal<User | null>(null);

  constructor(private http: HttpClient) {
    this.restoreSession();
  }

  // ─── SESSION PERSISTENCE ────────────────────────────────────────────────────

  private restoreSession(): void {
    try {
      const stored = localStorage.getItem(this.STORAGE_KEY);
      if (stored) {
        const user: User = JSON.parse(stored);
        this.currentUser.set(user);
      }
    } catch {
      localStorage.removeItem(this.STORAGE_KEY);
    }
  }

  private saveSession(user: User): void {
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(user));
  }

  private clearSession(): void {
    localStorage.removeItem(this.STORAGE_KEY);
  }

  // ─── LOGIN ──────────────────────────────────────────────────────────────────

  login(data: LoginMembreRequest): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/membres/login`, data).pipe(
      tap(user => {
        const fullUser: User = { ...user, role: 'MEMBER' };
        this.currentUser.set(fullUser);
        this.saveSession(fullUser);
      })
    );
  }

  loginAdmin(data: LoginAdminRequest): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/admin/login`, data).pipe(
      tap(user => {
        const fullUser: User = { ...user, role: 'ADMIN' };
        this.currentUser.set(fullUser);
        this.saveSession(fullUser);
      })
    );
  }

  logout(): void {
    this.currentUser.set(null);
    this.clearSession();
  }

  // ─── ÉTAT DE CONNEXION ──────────────────────────────────────────────────────

  isLoggedIn(): boolean {
    return this.currentUser() !== null;
  }

  // ─── RÔLES ──────────────────────────────────────────────────────────────────

  isAdmin(): boolean {
    return this.currentUser()?.role === 'ADMIN';
  }

  isMember(): boolean {
    return this.currentUser()?.role === 'MEMBER';
  }

  isAdminGlobal(): boolean {
    return this.isAdmin() && this.currentUser()?.type === 'GLOBAL';
  }

  isAdminSite(): boolean {
    return this.isAdmin() && this.currentUser()?.type === 'SITE';
  }

  isMemberGlobal(): boolean {
    return this.isMember() && this.currentUser()?.typeMembre === 'GLOBAL';
  }

  isMemberSite(): boolean {
    return this.isMember() && this.currentUser()?.typeMembre === 'SITE';
  }

  isMemberLibre(): boolean {
    return this.isMember() && this.currentUser()?.typeMembre === 'LIBRE';
  }

  // ─── ACCESSEURS ─────────────────────────────────────────────────────────────

  getCurrentUser(): User | null {
    return this.currentUser();
  }

  getMatricule(): string | undefined {
    return this.currentUser()?.matricule;
  }

  getNom(): string | undefined {
    return this.currentUser()?.nom;
  }

  getSite(): string | undefined {
    return this.currentUser()?.site;
  }

  /** @deprecated Utilise getSite() à la place */
  getSiteAdmin(): string | undefined {
    return this.getSite();
  }
}

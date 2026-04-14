import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface User {
  id: number;
  nom: string;
  matricule: string;
  role: 'MEMBER' | 'ADMIN';
}

@Injectable({ providedIn: 'root' })
export class Auth {
  private apiUrl = 'http://localhost:8080/api';
  currentUser = signal<User | null>(null);

  constructor(private http: HttpClient) {}

  login(data: { matricule: string }): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/membres/login`, data).pipe(
      tap(user => this.currentUser.set(user))
    );
  }

  loginAdmin(data: { username: string; password: string }): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/admin/login`, data).pipe(
      tap(user => this.currentUser.set({ ...user, role: 'ADMIN' }))
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

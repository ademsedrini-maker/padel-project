import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Dashboard } from './pages/dashboard/dashboard';
import { Reservations } from './pages/reservations/reservations';
import { Terrains } from './pages/terrains/terrains';
import { Members } from './pages/members/members';
import { Admin } from './pages/admin/admin';
import { Stats } from './pages/stats/stats';
import { authGuard } from './core/guards/auth-guard';
import { adminGuard } from './core/guards/admin-guard';

export const routes: Routes = [
  { path: 'login', component: Login },

  { path: 'dashboard', component: Dashboard, canActivate: [authGuard] },
  { path: 'reservations', component: Reservations, canActivate: [authGuard] },
  { path: 'terrains', component: Terrains, canActivate: [authGuard] },
  { path: 'members', component: Members, canActivate: [authGuard] },
  { path: 'stats', component: Stats, canActivate: [authGuard] },
  { path: 'admin', component: Admin, canActivate: [authGuard, adminGuard] },

  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }
];

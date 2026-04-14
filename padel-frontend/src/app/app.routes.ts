import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { LoginMembre } from './pages/login-membre/login-membre';
import { LoginAdmin } from './pages/login-admin/login-admin';
import { Register } from './pages/register/register';
import { Dashboard } from './pages/dashboard/dashboard';
import { Reservations } from './pages/reservations/reservations';
import { Terrains } from './pages/terrains/terrains';
import { Members } from './pages/members/members';
import { Admin } from './pages/admin/admin';
import { Stats } from './pages/stats/stats';
import { authGuard } from './core/guards/auth-guard';
import { adminGuard } from './core/guards/admin-guard';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'login-membre', component: LoginMembre },
  { path: 'login-admin', component: LoginAdmin },
  { path: 'register', component: Register },

  { path: 'dashboard', component: Dashboard, canActivate: [authGuard] },
  { path: 'reservations', component: Reservations, canActivate: [authGuard] },
  { path: 'terrains', component: Terrains, canActivate: [authGuard] },
  { path: 'members', component: Members, canActivate: [authGuard] },
  { path: 'stats', component: Stats, canActivate: [authGuard] },
  { path: 'admin', component: Admin, canActivate: [authGuard, adminGuard] },

  { path: '**', redirectTo: '' }
];

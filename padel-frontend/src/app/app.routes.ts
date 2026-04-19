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

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'login-membre', component: LoginMembre },
  { path: 'login-admin', component: LoginAdmin },
  { path: 'register', component: Register },

  { path: 'dashboard', component: Dashboard },
  { path: 'reservations', component: Reservations },
  { path: 'terrains', component: Terrains },
  { path: 'members', component: Members },
  { path: 'stats', component: Stats },
  { path: 'admin', component: Admin },

  { path: '**', redirectTo: '' }
];

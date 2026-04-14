import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Auth } from '../services/auth';

export const adminGuard: CanActivateFn = () => {
  const authService = inject(Auth);
  const router = inject(Router);

  if (authService.isAdmin()) {
    return true;
  }

  // Si connecté mais pas admin → renvoie vers le dashboard normal
  return router.createUrlTree(['/dashboard']);
};

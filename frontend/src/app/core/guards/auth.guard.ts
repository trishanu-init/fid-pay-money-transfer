import { Injectable } from '@angular/core';
import {
    ActivatedRouteSnapshot,
    CanActivate,
    Router,
    RouterStateSnapshot,
    UrlTree
} from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

/**
 * Auth Guard
 * 
 * Protects routes that require authentication.
 * Redirects unauthenticated users to the login page.
 */
@Injectable({
    providedIn: 'root'
})
export class AuthGuard implements CanActivate {

    constructor(
        private authService: AuthService,
        private router: Router
    ) { }

    canActivate(
        route: ActivatedRouteSnapshot,
        state: RouterStateSnapshot
    ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

        if (this.authService.isAuthenticated()) {
            return true;
        }

        // Store the attempted URL for redirecting after login
        const returnUrl = state.url;

        // Navigate to login with return URL
        return this.router.createUrlTree(['/login'], {
            queryParams: { returnUrl }
        });
    }
}

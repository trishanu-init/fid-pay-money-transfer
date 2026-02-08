import { Injectable } from '@angular/core';
import {
    HttpRequest,
    HttpHandler,
    HttpEvent,
    HttpInterceptor,
    HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

/**
 * Auth Interceptor
 * 
 * Automatically attaches the Basic Auth header to all outgoing
 * API requests and handles authentication errors.
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {

    constructor(
        private authService: AuthService,
        private router: Router
    ) { }

    intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
        // Get the Basic Auth header
        const authHeader = this.authService.getBasicAuthHeader();

        // Clone the request and add the authorization header if available
        if (authHeader) {
            request = request.clone({
                setHeaders: {
                    Authorization: authHeader
                }
            });
        }

        return next.handle(request).pipe(
            catchError((error: HttpErrorResponse) => {
                // Handle 401 Unauthorized - redirect to login
                if (error.status === 401) {
                    this.authService.logout();
                    this.router.navigate(['/login'], {
                        queryParams: { returnUrl: this.router.url }
                    });
                }

                // Handle 403 Forbidden
                if (error.status === 403) {
                    console.error('Access forbidden:', error.message);
                }

                return throwError(() => error);
            })
        );
    }
}

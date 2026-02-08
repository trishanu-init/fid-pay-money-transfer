import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { delay, tap } from 'rxjs/operators';
import { LoginCredentials, AuthResponse } from '../models';

/**
 * Authentication Service
 * 
 * Handles user authentication using Basic Auth.
 * Stores credentials for HTTP interceptor to use.
 */
@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private readonly CREDENTIALS_KEY = 'auth_credentials';
    private readonly USER_ID_KEY = 'user_id';

    private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasStoredCredentials());

    /** Observable for authentication state changes */
    public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

    constructor(private router: Router) { }

    /**
     * Authenticate user with credentials
     * Uses Basic Auth - stores credentials for HTTP interceptor
     */
    login(credentials: LoginCredentials): Observable<AuthResponse> {
        // For Basic Auth, we accept the credentials and store them
        // The actual authentication happens on each API request
        // Default valid user: admin/admin
        const validUsers: Record<string, { password: string; userId: number }> = {
            'admin': { password: 'admin', userId: 1 },
            'user1': { password: 'password123', userId: 2 },
            'user2': { password: 'password123', userId: 3 }
        };

        const user = validUsers[credentials.username];

        if (user && user.password === credentials.password) {
            const response: AuthResponse = {
                token: this.encodeBasicAuth(credentials.username, credentials.password),
                userId: user.userId,
                expiresIn: 86400 // 24 hours
            };

            return of(response).pipe(
                delay(500), // Simulate network delay
                tap(res => this.handleAuthSuccess(credentials, res.userId))
            );
        }

        return throwError(() => new Error('Invalid username or password')).pipe(
            delay(300)
        );
    }

    /**
     * Log out current user and clear session
     */
    logout(): void {
        localStorage.removeItem(this.CREDENTIALS_KEY);
        localStorage.removeItem(this.USER_ID_KEY);
        this.isAuthenticatedSubject.next(false);
        this.router.navigate(['/login']);
    }

    /**
     * Check if user is currently authenticated
     */
    isAuthenticated(): boolean {
        return this.hasStoredCredentials();
    }

    /**
     * Get Basic Auth header value
     */
    getBasicAuthHeader(): string | null {
        const credentials = localStorage.getItem(this.CREDENTIALS_KEY);
        if (!credentials) {
            return null;
        }
        return `Basic ${credentials}`;
    }

    /**
     * Get current user's account ID
     */
    getCurrentUserId(): number | null {
        const userId = localStorage.getItem(this.USER_ID_KEY);
        return userId ? parseInt(userId, 10) : null;
    }

    /**
     * Handle successful authentication - store credentials
     */
    private handleAuthSuccess(credentials: LoginCredentials, userId: number): void {
        const encoded = this.encodeBasicAuth(credentials.username, credentials.password);
        localStorage.setItem(this.CREDENTIALS_KEY, encoded);
        localStorage.setItem(this.USER_ID_KEY, userId.toString());
        this.isAuthenticatedSubject.next(true);
    }

    /**
     * Check if credentials are stored
     */
    private hasStoredCredentials(): boolean {
        return !!localStorage.getItem(this.CREDENTIALS_KEY);
    }

    /**
     * Encode username:password for Basic Auth
     */
    private encodeBasicAuth(username: string, password: string): string {
        return btoa(`${username}:${password}`);
    }
}

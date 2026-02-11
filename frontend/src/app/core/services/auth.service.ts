import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { HttpClient } from '@angular/common/http'; // Import HttpClient
import { catchError, tap } from 'rxjs/operators';
import { LoginCredentials, AuthResponse } from '../models';

/**
 * Authentication Service
 * 
 * Handles user authentication and registration.
 * Uses HTTP to interact with the backend for registration.
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

    private apiUrl = 'http://localhost:9890/api/v1/auth'; // Your backend URL for registration

    constructor(
        private router: Router,
        private http: HttpClient // Inject HttpClient for backend calls
    ) { }

    /**
     * Authenticate user with credentials (Using demo data)
     */
    login(credentials: LoginCredentials): Observable<AuthResponse> {
        // Demo users (without hitting the backend)
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

            return new Observable((observer) => {
                observer.next(response);
                observer.complete();
            });
        }

        return throwError(() => new Error('Invalid username or password'));
    }

    /**
     * Register a new user by sending data to the backend
     */
    register(userData: { username: string, email: string, password: string }): Observable<AuthResponse> {
        console.log('Sending registration data:', userData); // Log the data being sent
        return this.http.post<AuthResponse>(`${this.apiUrl}/register`, userData).pipe(
            tap((res: AuthResponse) => {
                console.log('Registration response:', res); // Log response data
                this.handleAuthSuccess(userData, res.userId);
            }),
            catchError(this.handleError)
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

    /**
     * Error handler for HTTP requests
     */
    private handleError(error: any): Observable<never> {
        console.error(error);
        return throwError(() => new Error(error.message || 'An error occurred'));
    }

    /**
     * Get Basic Auth header
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
}

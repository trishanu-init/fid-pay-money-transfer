import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { HttpClient } from '@angular/common/http'; // Import HttpClient
import { catchError, tap } from 'rxjs/operators';
import { LoginCredentials, AuthResponse } from '../models';

/**
 * Authentication Service
 *
 * Handles user authentication and registration with JWT tokens.
 * Uses HTTP to interact with the backend for login/registration.
 */
@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private readonly TOKEN_KEY = 'jwt_token';
    private readonly EMAIL_KEY = 'user_email';
    private readonly ACCOUNT_ID_KEY = 'account_id';
    private readonly EXPIRATION_KEY = 'token_expiration';
    private readonly ROLE_KEY = 'user_role';

    private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasValidToken());

    /** Observable for authentication state changes */
    public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

    private apiUrl = 'http://localhost:9890/api/v1/auth'; // Backend URL for authentication

    constructor(
        private router: Router,
        private http: HttpClient
    ) { }

    /**
     * Authenticate user with credentials via backend API
     */
    login(credentials: LoginCredentials): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
            tap((response: AuthResponse) => {
                this.handleAuthSuccess(credentials, response);
            }),
            catchError((error) => {
                return this.handleError(error);
            })
        );
    }

    /**
     * Register a new user by sending data to the backend
     */
    register(userData: { username: string, email: string, password: string }): Observable<any> {
        return this.http.post<any>(`${this.apiUrl}/register`, userData).pipe(
            catchError((error) => {
                return this.handleError(error);
            })
        );
    }

    /**
     * Log out current user and clear session
     */
    logout(): void {
        localStorage.removeItem(this.TOKEN_KEY);
        localStorage.removeItem(this.EMAIL_KEY);
        localStorage.removeItem(this.ACCOUNT_ID_KEY);
        localStorage.removeItem(this.EXPIRATION_KEY);
        localStorage.removeItem(this.ROLE_KEY);
        this.isAuthenticatedSubject.next(false);
        this.router.navigate(['/login']);
    }

    /**
     * Check if user is currently authenticated
     */
    isAuthenticated(): boolean {
        return this.hasValidToken();
    }

    /**
     * Handle successful authentication - store JWT token
     */
    private handleAuthSuccess(credentials: LoginCredentials, response: AuthResponse): void {
        localStorage.setItem(this.TOKEN_KEY, response.token);
        localStorage.setItem(this.EMAIL_KEY, response.email);
        localStorage.setItem(this.ACCOUNT_ID_KEY, response.accountId.toString());
        localStorage.setItem(this.EXPIRATION_KEY, (Date.now() + response.expiresIn).toString());
        localStorage.setItem(this.ROLE_KEY, response.role || 'USER');
        this.isAuthenticatedSubject.next(true);
    }

    /**
     * Check if JWT token is valid and not expired
     */
    private hasValidToken(): boolean {
        const token = localStorage.getItem(this.TOKEN_KEY);
        const expiration = localStorage.getItem(this.EXPIRATION_KEY);

        if (!token || !expiration) {
            return false;
        }

        // Check if token is expired
        return Date.now() < parseInt(expiration, 10);
    }

    /**
     * Get JWT Bearer token header
     */
    getAuthHeader(): string | null {
        const token = localStorage.getItem(this.TOKEN_KEY);
        if (!token) {
            return null;
        }
        return `Bearer ${token}`;
    }

    /**
     * Get current user's email
     */
    getCurrentUserEmail(): string | null {
        return localStorage.getItem(this.EMAIL_KEY);
    }

    /**
     * Get current user's account ID
     */
    getCurrentUserId(): string | null {
        return localStorage.getItem(this.ACCOUNT_ID_KEY);
    }

    /**
     * Get current user's role
     */
    getUserRole(): string | null {
        return localStorage.getItem(this.ROLE_KEY);
    }

    /**
     * Check if current user is an admin
     */
    isAdmin(): boolean {
        return this.getUserRole() === 'ADMIN';
    }

    /**
     * Request OTP for forgot password flow
     */
    sendForgotPasswordOtp(email: string): Observable<any> {
        return this.http.post<any>(`${this.apiUrl}/forgot-password/send-otp`, { email }).pipe(
            catchError((error) => this.handleError(error))
        );
    }

    /**
     * Verify OTP for forgot password flow
     */
    verifyForgotPasswordOtp(email: string, otp: string): Observable<any> {
        return this.http.post<any>(`${this.apiUrl}/forgot-password/verify-otp`, { email, otp }).pipe(
            catchError((error) => this.handleError(error))
        );
    }

    /**
     * Reset password for forgot password flow
     */
    resetPassword(passwordData: { email: string; resetToken: string; newPassword: string }): Observable<any> {
        return this.http.post<any>(`${this.apiUrl}/forgot-password/reset-password`, passwordData).pipe(
            catchError((error) => this.handleError(error))
        );
    }

    /**
     * Error handler for HTTP requests
     */
    private handleError(error: any): Observable<never> {
        let errorMessage = 'An error occurred';

        if (error.status === 401) {
            errorMessage = 'Invalid credentials. Please check your email and password.';
        } else if (error.status === 409) {
            errorMessage = error.error?.message || 'Email already exists. Please use a different email.';
        } else if (error.status === 400) {
            if (error.error?.fieldErrors) {
                // Extract field-level validation errors
                const fieldErrors = error.error.fieldErrors;
                errorMessage = fieldErrors.map((fe: any) => fe.message).join(', ');
            } else {
                errorMessage = error.error?.message || 'Invalid input. Please check your form.';
            }
        } else if (error.error?.message) {
            errorMessage = error.error.message;
        }

        console.error('Auth error:', error);
        return throwError(() => new Error(errorMessage));
    }
}

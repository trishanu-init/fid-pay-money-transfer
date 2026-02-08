import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

/**
 * Login Component
 * 
 * Handles user authentication with username/password form.
 * Features gradient background, centered card layout, and error handling.
 */
@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
    loginForm: FormGroup;
    isLoading = false;
    errorMessage = '';
    hidePassword = true;
    returnUrl = '/dashboard';

    constructor(
        private fb: FormBuilder,
        private authService: AuthService,
        private router: Router,
        private route: ActivatedRoute
    ) {
        this.loginForm = this.fb.group({
            username: ['', [Validators.required, Validators.minLength(3)]],
            password: ['', [Validators.required, Validators.minLength(4)]]
        });
    }

    ngOnInit(): void {
        // Get return URL from route parameters or default to dashboard
        this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/dashboard';

        // Redirect if already authenticated
        if (this.authService.isAuthenticated()) {
            this.router.navigate([this.returnUrl]);
        }
    }

    /**
     * Handle form submission
     */
    onSubmit(): void {
        if (this.loginForm.invalid) {
            this.markFormGroupTouched();
            return;
        }

        this.isLoading = true;
        this.errorMessage = '';

        const { username, password } = this.loginForm.value;

        this.authService.login({ username, password }).subscribe({
            next: () => {
                this.router.navigate([this.returnUrl]);
            },
            error: (error) => {
                this.isLoading = false;
                this.errorMessage = error.message || 'Login failed. Please try again.';
            }
        });
    }

    /**
     * Mark all form controls as touched to show validation errors
     */
    private markFormGroupTouched(): void {
        Object.keys(this.loginForm.controls).forEach(key => {
            this.loginForm.get(key)?.markAsTouched();
        });
    }

    /**
     * Get error message for username field
     */
    getUsernameError(): string {
        const control = this.loginForm.get('username');
        if (control?.hasError('required')) {
            return 'Username is required';
        }
        if (control?.hasError('minlength')) {
            return 'Username must be at least 3 characters';
        }
        return '';
    }

    /**
     * Get error message for password field
     */
    getPasswordError(): string {
        const control = this.loginForm.get('password');
        if (control?.hasError('required')) {
            return 'Password is required';
        }
        if (control?.hasError('minlength')) {
            return 'Password must be at least 4 characters';
        }
        return '';
    }
}

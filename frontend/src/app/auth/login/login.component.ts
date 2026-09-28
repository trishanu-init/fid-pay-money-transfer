import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ForgotPasswordDialogComponent } from '../forgot-password-dialog/forgot-password-dialog.component';

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
        private route: ActivatedRoute,
        private dialog: MatDialog,
        private snackBar: MatSnackBar
    ) {
        this.loginForm = this.fb.group({
            email: ['', [Validators.required, Validators.email]],
            password: ['', [Validators.required, Validators.minLength(6)]]
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

        const { email, password } = this.loginForm.value;

        this.authService.login({ email, password }).subscribe({
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
     * Get error message for email field
     */
    getEmailError(): string {
        const control = this.loginForm.get('email');
        if (control?.hasError('required')) {
            return 'Email is required';
        }
        if (control?.hasError('email')) {
            return 'Please enter a valid email address';
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
            return 'Password must be at least 6 characters';
        }
        return '';
    }

    /**
     * Open forgot password dialog
     */
    openForgotPasswordDialog(): void {
        const dialogRef = this.dialog.open(ForgotPasswordDialogComponent, {
            width: '450px',
            data: { email: this.loginForm.get('email')?.value },
            disableClose: true
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result && result.success) {
                // Show success snackbar
                this.snackBar.open(result.message || 'Password reset successfully', 'Close', {
                    duration: 5000,
                    panelClass: ['success-snackbar'],
                    horizontalPosition: 'center',
                    verticalPosition: 'bottom'
                });
                
                // Redirect user back to login page
                this.router.navigate(['/login']);
            }
        });
    }
}

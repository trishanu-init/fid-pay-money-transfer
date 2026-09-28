import { Component, Inject, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Subject, interval } from 'rxjs';
import { takeUntil, take } from 'rxjs/operators';
import { AuthService } from '../../core/services/auth.service';

export interface ForgotPasswordDialogData {
    email?: string;
}

@Component({
    selector: 'app-forgot-password-dialog',
    templateUrl: './forgot-password-dialog.component.html',
    styleUrls: ['./forgot-password-dialog.component.scss']
})
export class ForgotPasswordDialogComponent implements OnInit, OnDestroy {
    step: 'EMAIL' | 'OTP' | 'PASSWORD' = 'EMAIL';
    emailForm: FormGroup;
    otpForm: FormGroup;
    passwordForm: FormGroup;
    
    isLoading = false;
    errorMessage = '';
    successMessage = '';
    resetToken = '';
    
    hidePassword = true;
    hideConfirmPassword = true;
    
    expiryCountdown = 120; // 2 minutes (120 seconds)
    resendCooldown = 0;
    
    private destroy$ = new Subject<void>();
    private countdownInterval$ = new Subject<void>();

    constructor(
        private fb: FormBuilder,
        private dialogRef: MatDialogRef<ForgotPasswordDialogComponent>,
        private authService: AuthService,
        @Inject(MAT_DIALOG_DATA) public data: ForgotPasswordDialogData
    ) {
        this.emailForm = this.fb.group({
            email: [this.data?.email || '', [Validators.required, Validators.email]]
        });

        this.otpForm = this.fb.group({
            otp: ['', [Validators.required, Validators.pattern('^[0-9]{6}$')]]
        });

        this.passwordForm = this.fb.group({
            password: ['', [Validators.required, Validators.minLength(6)]],
            confirmPassword: ['', [Validators.required]]
        }, { validators: this.passwordMatchValidator });
    }

    ngOnInit(): void {
        // If email was pre-populated, user can review and send OTP
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
        this.countdownInterval$.next();
        this.countdownInterval$.complete();
    }

    /**
     * Custom validator to check if password and confirm password match
     */
    private passwordMatchValidator(control: AbstractControl): { [key: string]: boolean } | null {
        const password = control.get('password');
        const confirmPassword = control.get('confirmPassword');
        if (!password || !confirmPassword) return null;
        return password.value === confirmPassword.value ? null : { 'mismatch': true };
    }

    /**
     * Step 1: Send OTP to email
     */
    sendOtp(): void {
        if (this.emailForm.invalid) {
            return;
        }

        this.isLoading = true;
        this.errorMessage = '';
        this.successMessage = '';

        const email = this.emailForm.get('email')?.value;
        this.authService.sendForgotPasswordOtp(email)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (response) => {
                    this.isLoading = false;
                    this.step = 'OTP';
                    this.successMessage = 'OTP has been sent to your email';
                    this.startResendCooldown();
                    this.startExpiryCountdown();
                },
                error: (error) => {
                    this.isLoading = false;
                    this.errorMessage = error.message || 'Failed to send OTP. Please check your email.';
                }
            });
    }

    /**
     * Step 2: Verify OTP
     */
    verifyOtp(): void {
        if (this.otpForm.invalid) {
            return;
        }

        this.isLoading = true;
        this.errorMessage = '';
        this.successMessage = '';

        const email = this.emailForm.get('email')?.value;
        const otp = this.otpForm.get('otp')?.value;

        this.authService.verifyForgotPasswordOtp(email, otp)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (response) => {
                    this.isLoading = false;
                    if (response.success) {
                        this.resetToken = response.resetToken;
                        this.step = 'PASSWORD';
                        // Stop countdown timer
                        this.countdownInterval$.next();
                    } else {
                        this.errorMessage = response.message || 'Invalid OTP';
                    }
                },
                error: (error) => {
                    this.isLoading = false;
                    this.errorMessage = error.message || 'OTP verification failed. Please try again.';
                }
            });
    }

    /**
     * Step 3: Reset Password
     */
    resetPassword(): void {
        if (this.passwordForm.invalid) {
            return;
        }

        this.isLoading = true;
        this.errorMessage = '';
        this.successMessage = '';

        const email = this.emailForm.get('email')?.value;
        const newPassword = this.passwordForm.get('password')?.value;

        this.authService.resetPassword({
            email,
            resetToken: this.resetToken,
            newPassword
        })
        .pipe(takeUntil(this.destroy$))
        .subscribe({
            next: (response) => {
                this.isLoading = false;
                this.dialogRef.close({ success: true, message: 'Password reset successfully!' });
            },
            error: (error) => {
                this.isLoading = false;
                this.errorMessage = error.message || 'Failed to reset password. Please start over.';
            }
        });
    }

    /**
     * Start resend cooldown timer (30 seconds)
     */
    private startResendCooldown(): void {
        this.resendCooldown = 30;
        interval(1000)
            .pipe(takeUntil(this.destroy$), take(30))
            .subscribe({
                next: () => {
                    this.resendCooldown--;
                }
            });
    }

    /**
     * Start OTP expiry countdown (2 minutes)
     */
    private startExpiryCountdown(): void {
        this.countdownInterval$.next(); // Reset any existing countdown
        this.expiryCountdown = 120;
        interval(1000)
            .pipe(takeUntil(this.countdownInterval$), takeUntil(this.destroy$))
            .subscribe({
                next: () => {
                    this.expiryCountdown--;
                    if (this.expiryCountdown <= 0) {
                        this.errorMessage = 'OTP has expired. Please request a new one.';
                    }
                }
            });
    }

    /**
     * Format seconds to MM:SS
     */
    formatTime(seconds: number): string {
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${mins}:${secs.toString().padStart(2, '0')}`;
    }

    /**
     * Close dialog
     */
    cancel(): void {
        this.dialogRef.close({ success: false });
    }

    /**
     * Go back to email entry step
     */
    goBackToEmail(): void {
        this.step = 'EMAIL';
        this.errorMessage = '';
        this.successMessage = '';
        this.otpForm.reset();
        this.countdownInterval$.next(); // Stop timer
    }

    /**
     * Check if resend is available
     */
    get canResend(): boolean {
        return (this.resendCooldown === 0 || this.expiryCountdown <= 0) && !this.isLoading;
    }
}

import { Component, Inject, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Subject, interval } from 'rxjs';
import { takeUntil, take } from 'rxjs/operators';
import { TransferService } from '../../core/services/transfer.service';

export interface OtpDialogData {
    accountId: number;
    email?: string;
}

export interface OtpDialogResult {
    verified: boolean;
}

@Component({
    selector: 'app-otp-dialog',
    templateUrl: './otp-dialog.component.html',
    styleUrls: ['./otp-dialog.component.scss']
})
export class OtpDialogComponent implements OnInit, OnDestroy {
    otpForm: FormGroup;
    isLoading = false;
    isSending = false;
    errorMessage = '';
    successMessage = '';
    resendCooldown = 0;
    expiryCountdown = 300; // 5 minutes in seconds
    private destroy$ = new Subject<void>();
    private countdownInterval$ = new Subject<void>();

    constructor(
        private fb: FormBuilder,
        private dialogRef: MatDialogRef<OtpDialogComponent>,
        private transferService: TransferService,
        @Inject(MAT_DIALOG_DATA) public data: OtpDialogData
    ) {
        this.otpForm = this.fb.group({
            otp: ['', [Validators.required, Validators.pattern('^[0-9]{6}$')]]
        });
    }

    ngOnInit(): void {
        this.sendOtp();
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
        this.countdownInterval$.next();
        this.countdownInterval$.complete();
    }

    /**
     * Send OTP to user's email
     */
    sendOtp(): void {
        this.isSending = true;
        this.errorMessage = '';
        this.successMessage = '';

        this.transferService.sendOtp(this.data.accountId)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (response) => {
                    this.isSending = false;
                    if (response.success) {
                        this.successMessage = 'OTP sent to your registered email';
                        this.startResendCooldown();
                        this.startExpiryCountdown();
                    } else {
                        this.errorMessage = response.message || 'Failed to send OTP';
                    }
                },
                error: (error) => {
                    this.isSending = false;
                    this.errorMessage = error.error?.message || 'Failed to send OTP. Please try again.';
                }
            });
    }

    /**
     * Verify entered OTP
     */
    verifyOtp(): void {
        if (this.otpForm.invalid) {
            return;
        }

        this.isLoading = true;
        this.errorMessage = '';

        const otp = this.otpForm.get('otp')?.value;
        this.transferService.verifyOtp(this.data.accountId, otp)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (response) => {
                    this.isLoading = false;
                    if (response.success) {
                        this.dialogRef.close({ verified: true });
                    } else {
                        this.errorMessage = response.message || 'Invalid OTP';
                        this.otpForm.reset();
                    }
                },
                error: (error) => {
                    this.isLoading = false;
                    this.errorMessage = error.error?.message || 'Verification failed. Please try again.';
                    this.otpForm.reset();
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
            .subscribe(() => {
                this.resendCooldown--;
            });
    }

    /**
     * Start OTP expiry countdown (5 minutes)
     */
    private startExpiryCountdown(): void {
        this.countdownInterval$.next(); // Reset any existing countdown
        this.expiryCountdown = 300;
        interval(1000)
            .pipe(takeUntil(this.countdownInterval$), takeUntil(this.destroy$))
            .subscribe(() => {
                this.expiryCountdown--;
                if (this.expiryCountdown <= 0) {
                    this.errorMessage = 'OTP has expired. Please request a new one.';
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
     * Cancel dialog
     */
    cancel(): void {
        this.dialogRef.close({ verified: false });
    }

    /**
     * Check if resend is available
     */
    get canResend(): boolean {
        return this.resendCooldown === 0 && !this.isSending;
    }
}

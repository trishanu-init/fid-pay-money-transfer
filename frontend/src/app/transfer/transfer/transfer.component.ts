import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthService } from '../../core/services/auth.service';
import { AccountService } from '../../core/services/account.service';
import { TransferService } from '../../core/services/transfer.service';
import { Account } from '../../core/models';
import { ConfirmTransferDialogComponent } from '../confirm-dialog/confirm-dialog.component';
import { OtpDialogComponent, OtpDialogData, OtpDialogResult } from '../otp-dialog/otp-dialog.component';

/**
 * Transfer Component
 * 
 * Money transfer form with:
 * - Source account (readonly, current user)
 * - Destination account input
 * - Amount input with validation
 * - Confirmation dialog
 * - OTP verification
 * - Success/error feedback
 */
@Component({
    selector: 'app-transfer',
    templateUrl: './transfer.component.html',
    styleUrls: ['./transfer.component.scss']
})
export class TransferComponent implements OnInit, OnDestroy {
    transferForm: FormGroup;
    account: Account | null = null;
    isLoading = true;
    isSubmitting = false;
    private destroy$ = new Subject<void>();

    constructor(
        private fb: FormBuilder,
        private authService: AuthService,
        private accountService: AccountService,
        private transferService: TransferService,
        private dialog: MatDialog,
        private snackBar: MatSnackBar,
        private router: Router
    ) {
        this.transferForm = this.fb.group({
            toAccountId: ['', [Validators.required, Validators.pattern('^FIDPY[A-Z0-9]{6}$')]],
            amount: ['', [Validators.required, Validators.min(0.01)]],
            message: ['', [Validators.maxLength(255)]]
        });
    }

    ngOnInit(): void {
        this.loadAccount();
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    /**
     * Load current user's account
     */
    loadAccount(): void {
        this.accountService.getAccount()
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (account) => {
                    this.account = account;
                    this.isLoading = false;
                },
                error: (error) => {
                    this.isLoading = false;
                    this.showError('Failed to load account');
                    console.error('Error loading account:', error);
                }
            });
    }

    /**
     * Handle form submission - show confirmation dialog, then OTP dialog
     */
    onSubmit(): void {
        if (this.transferForm.invalid || !this.account) {
            this.markFormTouched();
            return;
        }

        const { toAccountId, amount, message } = this.transferForm.value;

        // Validate not transferring to self
        if (toAccountId === this.account.accountId) {
            this.showError('Cannot transfer to your own account');
            return;
        }

        // Validate sufficient balance
        if (amount > this.account.balance) {
            this.showError('Insufficient balance for this transfer');
            return;
        }

        // Open confirmation dialog
        const dialogRef = this.dialog.open(ConfirmTransferDialogComponent, {
            width: '400px',
            data: { toAccountId, amount: parseFloat(amount), message }
        });

        dialogRef.afterClosed().subscribe(confirmed => {
            if (confirmed) {
                this.showOtpDialog();
            }
        });
    }

    /**
     * Show OTP verification dialog
     */
    private showOtpDialog(): void {
        if (!this.account) return;

        const dialogRef = this.dialog.open(OtpDialogComponent, {
            width: '450px',
            disableClose: true,
            data: {
                accountId: this.account.accountId
            } as OtpDialogData
        });

        dialogRef.afterClosed().subscribe((result: OtpDialogResult) => {
            if (result?.verified) {
                this.executeTransfer();
            } else {
                this.showError('Transfer cancelled - OTP verification required');
            }
        });
    }

    /**
     * Execute the transfer after OTP verification
     */
    private executeTransfer(): void {
        if (!this.account) return;

        this.isSubmitting = true;
        const { toAccountId, amount, message } = this.transferForm.value;

        this.transferService.transfer(
            this.account.accountId,
            toAccountId,
            parseFloat(amount),
            message
        ).pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (response) => {
                    this.isSubmitting = false;
                    this.showSuccess(`Transfer of ₹${amount} completed successfully!`);
                    this.router.navigate(['/dashboard']);
                },
                error: (error) => {
                    this.isSubmitting = false;
                    const message = error.error?.message || error.message || 'Transfer failed';
                    this.showError(message);
                }
            });
    }

    /**
     * Cancel and go back to dashboard
     */
    cancel(): void {
        this.router.navigate(['/dashboard']);
    }

    /**
     * Log out user
     */
    logout(): void {
        this.authService.logout();
    }

    /**
     * Mark all form controls as touched
     */
    private markFormTouched(): void {
        Object.keys(this.transferForm.controls).forEach(key => {
            this.transferForm.get(key)?.markAsTouched();
        });
    }

    /**
     * Show success snackbar
     */
    private showSuccess(message: string): void {
        this.snackBar.open(message, 'Close', {
            duration: 5000,
            panelClass: ['success-snackbar'],
            horizontalPosition: 'center',
            verticalPosition: 'bottom'
        });
    }

    /**
     * Show error snackbar
     */
    private showError(message: string): void {
        this.snackBar.open(message, 'Close', {
            duration: 5000,
            panelClass: ['error-snackbar'],
            horizontalPosition: 'center',
            verticalPosition: 'bottom'
        });
    }

    /**
     * Get error message for amount field
     */
    getAmountError(): string {
        const control = this.transferForm.get('amount');
        if (control?.hasError('required')) {
            return 'Amount is required';
        }
        if (control?.hasError('min')) {
            return 'Amount must be greater than 0';
        }
        return '';
    }

    /**
     * Get error message for account field
     */
    getAccountError(): string {
        const control = this.transferForm.get('toAccountId');
        if (control?.hasError('required')) {
            return 'Account ID is required';
        }
        if (control?.hasError('pattern')) {
            return 'Please enter a valid account number';
        }
        return '';
    }
}

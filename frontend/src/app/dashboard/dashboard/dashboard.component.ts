import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthService } from '../../core/services/auth.service';
import { AccountService } from '../../core/services/account.service';
import { Account } from '../../core/models';

/**
 * Dashboard Component
 * 
 * Main landing page after login showing:
 * - Welcome banner with user name
 * - Account balance card
 * - Quick action buttons
 */
@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, OnDestroy {
    account: Account | null = null;
    isLoading = true;
    errorMessage = '';
    private destroy$ = new Subject<void>();

    constructor(
        private authService: AuthService,
        private accountService: AccountService,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.loadAccountData();
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    /**
     * Load account data from API
     */
    loadAccountData(): void {
        this.isLoading = true;
        this.errorMessage = '';

        this.accountService.getAccount()
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (account) => {
                    this.account = account;
                    this.isLoading = false;
                },
                error: (error) => {
                    this.isLoading = false;
                    this.errorMessage = error.message || 'Failed to load account data';
                    console.error('Dashboard error:', error);
                }
            });
    }

    /**
     * Log out and redirect to login
     */
    logout(): void {
        this.authService.logout();
    }

    /**
     * Navigate to transfer page
     */
    goToTransfer(): void {
        this.router.navigate(['/transfer']);
    }

    /**
     * Navigate to transaction history
     */
    goToHistory(): void {
        this.router.navigate(['/history']);
    }

    /**
     * Refresh account data
     */
    refresh(): void {
        this.loadAccountData();
    }
}

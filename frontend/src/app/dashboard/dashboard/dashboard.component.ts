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
    bannerGradient = 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)';
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
                    this.bannerGradient = this.getGradientForId(account.accountId);
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
     * Navigate to rewards details page
     */
    goToRewards(): void {
        this.router.navigate(['/rewards']);
    }

    /**
     * Refresh account data
     */
    refresh(): void {
        this.loadAccountData();
    }

    /**
     * Generate deterministic gradient based on account ID string
     */
    getGradientForId(id: string): string {
        let hash1 = 0;
        let hash2 = 0;
        
        // Compute first hash
        for (let i = 0; i < id.length; i++) {
            hash1 = id.charCodeAt(i) + ((hash1 << 5) - hash1);
        }
        
        // Compute second hash using reversed ID
        const reversed = id.split('').reverse().join('');
        for (let i = 0; i < reversed.length; i++) {
            hash2 = reversed.charCodeAt(i) + ((hash2 << 5) - hash2);
        }
        
        const hue1 = Math.abs(hash1) % 360;
        // Shift second color's hue by at least 40 degrees to guarantee a gradient contrast
        const hue2 = (hue1 + 40 + (Math.abs(hash2) % 120)) % 360;
        
        // Output a custom linear gradient with fixed saturation and dark lightness for white text readability
        return `linear-gradient(135deg, hsl(${hue1}, 75%, 42%) 0%, hsl(${hue2}, 70%, 38%) 100%)`;
    }
}

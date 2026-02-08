import { Component, OnInit, OnDestroy, ViewChild, AfterViewInit } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthService } from '../../core/services/auth.service';
import { AccountService } from '../../core/services/account.service';
import { TransactionLog } from '../../core/models';

/**
 * History Component
 * 
 * Displays transaction history with:
 * - Material table with sorting and pagination
 * - DEBIT/CREDIT type styling
 * - Status badges
 * - Responsive card layout on mobile
 */
@Component({
    selector: 'app-history',
    templateUrl: './history.component.html',
    styleUrls: ['./history.component.scss']
})
export class HistoryComponent implements OnInit, OnDestroy, AfterViewInit {
    displayedColumns = ['createdOn', 'type', 'amount', 'status'];
    dataSource = new MatTableDataSource<TransactionLog>([]);
    currentAccountId: number | null = null;
    isLoading = true;
    errorMessage = '';
    isMobile = false;

    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;

    private destroy$ = new Subject<void>();

    constructor(
        private authService: AuthService,
        private accountService: AccountService
    ) {
        // Check if mobile
        this.checkMobile();
        window.addEventListener('resize', () => this.checkMobile());
    }

    ngOnInit(): void {
        this.currentAccountId = this.authService.getCurrentUserId();
        this.loadTransactions();
    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    /**
     * Check if viewport is mobile size
     */
    checkMobile(): void {
        this.isMobile = window.innerWidth < 768;
    }

    /**
     * Load transaction history
     */
    loadTransactions(): void {
        this.isLoading = true;
        this.errorMessage = '';

        this.accountService.getTransactions()
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (transactions) => {
                    // Sort by date descending (newest first)
                    transactions.sort((a, b) =>
                        new Date(b.createdOn).getTime() - new Date(a.createdOn).getTime()
                    );
                    this.dataSource.data = transactions;
                    this.isLoading = false;
                },
                error: (error) => {
                    this.isLoading = false;
                    this.errorMessage = error.message || 'Failed to load transactions';
                    console.error('Error loading transactions:', error);
                }
            });
    }

    /**
     * Get transaction type for current account
     */
    getTransactionType(transaction: TransactionLog): 'DEBIT' | 'CREDIT' {
        return this.accountService.getTransactionType(transaction, this.currentAccountId!);
    }

    /**
     * Refresh transactions
     */
    refresh(): void {
        this.loadTransactions();
    }

    /**
     * Log out user
     */
    logout(): void {
        this.authService.logout();
    }
}

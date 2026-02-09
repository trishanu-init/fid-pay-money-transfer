import { Component, OnInit, OnDestroy, ViewChild, AfterViewInit } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthService } from '../../core/services/auth.service';
import { AccountService } from '../../core/services/account.service';
import { TransactionLog } from '../../core/models';

export type TransactionFilter = 'all' | 'sent' | 'received';

/**
 * History Component
 * 
 * Displays transaction history with:
 * - Filter tabs for All/Sent/Received transactions
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
    allTransactions: TransactionLog[] = [];
    currentAccountId: number | null = null;
    isLoading = true;
    errorMessage = '';
    isMobile = false;

    // Filter tab state
    activeFilter: TransactionFilter = 'all';
    transactionCounts = { all: 0, sent: 0, received: 0 };

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
                    this.allTransactions = transactions;
                    this.updateTransactionCounts();
                    this.applyFilter(this.activeFilter);
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
     * Update transaction counts for each filter tab
     */
    updateTransactionCounts(): void {
        this.transactionCounts = {
            all: this.allTransactions.length,
            sent: this.allTransactions.filter(tx => this.getTransactionType(tx) === 'DEBIT').length,
            received: this.allTransactions.filter(tx => this.getTransactionType(tx) === 'CREDIT').length
        };
    }

    /**
     * Apply filter to transactions
     */
    applyFilter(filter: TransactionFilter): void {
        this.activeFilter = filter;

        let filtered: TransactionLog[];
        switch (filter) {
            case 'sent':
                filtered = this.allTransactions.filter(tx => this.getTransactionType(tx) === 'DEBIT');
                break;
            case 'received':
                filtered = this.allTransactions.filter(tx => this.getTransactionType(tx) === 'CREDIT');
                break;
            default:
                filtered = this.allTransactions;
        }

        this.dataSource.data = filtered;

        // Reset paginator to first page when filter changes
        if (this.paginator) {
            this.paginator.firstPage();
        }
    }

    /**
     * Handle tab change event
     */
    onTabChange(index: number): void {
        const filters: TransactionFilter[] = ['all', 'sent', 'received'];
        this.applyFilter(filters[index]);
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

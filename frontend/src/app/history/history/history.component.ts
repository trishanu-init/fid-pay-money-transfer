import { Component, OnInit, OnDestroy, ViewChild, AfterViewInit } from '@angular/core';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
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
 * - Server-side pagination via Spring Pageable
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
    allPageTransactions: TransactionLog[] = []; // All transactions for current page (before client filter)
    currentAccountId: number | null = null;
    isLoading = true;
    errorMessage = '';
    isMobile = false;

    // Server-side pagination state
    totalElements = 0;
    pageSize = 10;
    pageIndex = 0;
    pageSizeOptions = [5, 10, 25];

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
        // Sort is still client-side within the current page
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
     * Load transaction history from server with pagination
     */
    loadTransactions(): void {
        this.isLoading = true;
        this.errorMessage = '';

        this.accountService.getTransactionsPaginated(this.pageIndex, this.pageSize)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (page) => {
                    this.allPageTransactions = page.content;
                    this.totalElements = page.totalElements;
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
     * Handle paginator page change event
     */
    onPageChange(event: PageEvent): void {
        this.pageIndex = event.pageIndex;
        this.pageSize = event.pageSize;
        this.loadTransactions();
    }

    /**
     * Update transaction counts for each filter tab (within current page)
     */
    updateTransactionCounts(): void {
        this.transactionCounts = {
            all: this.allPageTransactions.length,
            sent: this.allPageTransactions.filter(tx => this.getTransactionType(tx) === 'DEBIT').length,
            received: this.allPageTransactions.filter(tx => this.getTransactionType(tx) === 'CREDIT').length
        };
    }

    /**
     * Apply filter to transactions (client-side within current page)
     */
    applyFilter(filter: TransactionFilter): void {
        this.activeFilter = filter;

        let filtered: TransactionLog[];
        switch (filter) {
            case 'sent':
                filtered = this.allPageTransactions.filter(tx => this.getTransactionType(tx) === 'DEBIT');
                break;
            case 'received':
                filtered = this.allPageTransactions.filter(tx => this.getTransactionType(tx) === 'CREDIT');
                break;
            default:
                filtered = this.allPageTransactions;
        }

        this.dataSource.data = filtered;
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
     * Refresh transactions (reload current page)
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

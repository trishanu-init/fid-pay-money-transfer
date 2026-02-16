import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Account, TransactionLog, Page } from '../models';
import { AuthService } from './auth.service';

/**
 * Account Service
 * 
 * Handles all account-related API operations including
 * fetching account details, balance, and transaction history.
 */
@Injectable({
    providedIn: 'root'
})
export class AccountService {
    private readonly baseUrl = `${environment.apiUrl}/accounts`;

    constructor(
        private http: HttpClient,
        private authService: AuthService
    ) { }

    /**
     * Get account details for the current user
     */
    getAccount(): Observable<Account> {
        const userId = this.authService.getCurrentUserId();
        if (!userId) {
            throw new Error('User not authenticated');
        }
        return this.getAccountById(userId);
    }

    /**
     * Get account details by account ID
     */
    getAccountById(accountId: number): Observable<Account> {
        return this.http.get<Account>(`${this.baseUrl}/${accountId}`).pipe(
            catchError(error => {
                console.error('Error fetching account:', error);
                throw error;
            })
        );
    }

    /**
     * Get account balance for the current user
     */
    getBalance(): Observable<number> {
        const userId = this.authService.getCurrentUserId();
        if (!userId) {
            throw new Error('User not authenticated');
        }
        return this.getBalanceById(userId);
    }

    /**
     * Get account balance by account ID
     */
    getBalanceById(accountId: number): Observable<number> {
        return this.http.get<number>(`${this.baseUrl}/${accountId}/balance`).pipe(
            catchError(error => {
                console.error('Error fetching balance:', error);
                throw error;
            })
        );
    }

    /**
     * Get transaction history for the current user
     */
    getTransactions(): Observable<TransactionLog[]> {
        const userId = this.authService.getCurrentUserId();
        if (!userId) {
            throw new Error('User not authenticated');
        }
        return this.getTransactionsById(userId);
    }

    /**
     * Get transaction history by account ID
     */
    getTransactionsById(accountId: number): Observable<TransactionLog[]> {
        return this.http.get<TransactionLog[]>(`${this.baseUrl}/${accountId}/transactions`).pipe(
            map(transactions => this.enrichTransactions(transactions, accountId)),
            catchError(error => {
                console.error('Error fetching transactions:', error);
                throw error;
            })
        );
    }

    /**
     * Get paginated transaction history for the current user
     */
    getTransactionsPaginated(page: number = 0, size: number = 10): Observable<Page<TransactionLog>> {
        const userId = this.authService.getCurrentUserId();
        if (!userId) {
            throw new Error('User not authenticated');
        }
        return this.getTransactionsByIdPaginated(userId, page, size);
    }

    /**
     * Get paginated transaction history by account ID
     */
    getTransactionsByIdPaginated(accountId: number, page: number, size: number): Observable<Page<TransactionLog>> {
        const params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString())
            .set('sort', 'createdOn,desc');

        return this.http.get<Page<TransactionLog>>(`${this.baseUrl}/${accountId}/transactions`, { params }).pipe(
            catchError(error => {
                console.error('Error fetching paginated transactions:', error);
                throw error;
            })
        );
    }

    /**
     * Enrich transactions with computed type (DEBIT/CREDIT) based on current account
     */
    private enrichTransactions(transactions: TransactionLog[], currentAccountId: number): TransactionLog[] {
        return transactions.map(tx => ({
            ...tx,
            // Add computed property if needed in the future
        }));
    }

    /**
     * Determine if transaction is a debit or credit for given account
     */
    getTransactionType(transaction: TransactionLog, accountId: number): 'DEBIT' | 'CREDIT' {
        return transaction.fromAccountId === accountId ? 'DEBIT' : 'CREDIT';
    }
}


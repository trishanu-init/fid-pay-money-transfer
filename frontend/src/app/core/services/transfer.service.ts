import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { TransferRequest, TransferResponse } from '../models';

/**
 * Transfer Service
 * 
 * Handles money transfer operations between accounts.
 */
@Injectable({
    providedIn: 'root'
})
export class TransferService {
    private readonly baseUrl = `${environment.apiUrl}/transfers`;

    constructor(private http: HttpClient) { }

    /**
     * Execute a money transfer
     * 
     * @param fromAccountId - Source account ID
     * @param toAccountId - Destination account ID
     * @param amount - Transfer amount (must be > 0)
     * @returns Observable with transfer response
     */
    transfer(fromAccountId: number, toAccountId: number, amount: number): Observable<TransferResponse> {
        const request: TransferRequest = {
            fromAccountId,
            toAccountId,
            amount,
            idempotencyKey: this.generateIdempotencyKey()
        };

        return this.http.post<TransferResponse>(this.baseUrl, request).pipe(
            tap(response => {
                console.log('Transfer successful:', response);
            }),
            catchError(error => {
                console.error('Transfer failed:', error);
                throw error;
            })
        );
    }

    /**
     * Generate a unique idempotency key for the transfer
     * This ensures the same transfer is not processed twice
     */
    private generateIdempotencyKey(): string {
        return `TXN-${Date.now()}-${Math.random().toString(36).substring(2, 9)}`;
    }
}

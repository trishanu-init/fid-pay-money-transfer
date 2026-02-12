import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { TransferRequest, TransferResponse, OtpRequest, OtpVerifyRequest, OtpResponse } from '../models';

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
    private readonly otpUrl = `${environment.apiUrl}/otp`;

    constructor(private http: HttpClient) { }

    /**
     * Send OTP to sender's email
     * 
     * @param accountId - Sender's account ID
     * @returns Observable with OTP response
     */
    sendOtp(accountId: number): Observable<OtpResponse> {
        const request: OtpRequest = { accountId };
        return this.http.post<OtpResponse>(`${this.otpUrl}/send-transfer`, request).pipe(
            tap(response => {
                console.log('OTP sent:', response);
            }),
            catchError(error => {
                console.error('Failed to send OTP:', error);
                throw error;
            })
        );
    }

    /**
     * Verify OTP for transfer
     * 
     * @param accountId - Sender's account ID
     * @param otp - OTP code entered by user
     * @returns Observable with verification response
     */
    verifyOtp(accountId: number, otp: string): Observable<OtpResponse> {
        const request: OtpVerifyRequest = { accountId, otp };
        return this.http.post<OtpResponse>(`${this.otpUrl}/verify-transfer`, request).pipe(
            tap(response => {
                console.log('OTP verification:', response);
            }),
            catchError(error => {
                console.error('OTP verification failed:', error);
                throw error;
            })
        );
    }

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

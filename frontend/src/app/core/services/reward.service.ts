import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { RewardDetail } from '../models';
import { AuthService } from './auth.service';

/**
 * Reward Service
 * 
 * Handles reward points retrieval and reward credit history logs.
 */
@Injectable({
    providedIn: 'root'
})
export class RewardService {
    private readonly baseUrl = `${environment.apiUrl}/rewards`;

    // Static mock data fallback for Phase 1 (UI development)
    private readonly mockHistoryList: RewardDetail[] = [
        {
            id: 1,
            accountId: '',
            transactionId: 'TXN-1718873000000-a1b2c3d',
            pointsEarned: 5,
            transactionAmount: 550.00,
            createdOn: new Date(Date.now() - 3600000 * 48).toISOString() // 2 days ago
        },
        {
            id: 2,
            accountId: '',
            transactionId: 'TXN-1718959400000-e5f6g7h',
            pointsEarned: 12,
            transactionAmount: 1250.50,
            createdOn: new Date(Date.now() - 3600000 * 6).toISOString() // 6 hours ago
        },
        {
            id: 3,
            accountId: '',
            transactionId: 'TXN-1719001200000-i9j0k1l',
            pointsEarned: 2,
            transactionAmount: 200.00,
            createdOn: new Date(Date.now() - 3600000 * 2).toISOString() // 2 hours ago
        }
    ];

    constructor(
        private http: HttpClient,
        private authService: AuthService
    ) { }

    /**
     * Get total reward points for the current user
     */
    getRewardPoints(): Observable<number> {
        const accountId = this.authService.getCurrentUserId();
        if (!accountId) {
            return of(0);
        }
        return this.http.get<number>(`${this.baseUrl}/points`).pipe(
            catchError(() => {
                console.warn('Rewards backend not deployed yet. Returning mock reward points.');
                // Return total points from mock data
                const userPoints = this.mockHistoryList.reduce((sum, item) => sum + item.pointsEarned, 0);
                return of(userPoints);
            })
        );
    }

    /**
     * Get reward history logs for the current user
     */
    getRewardHistory(): Observable<RewardDetail[]> {
        const accountId = this.authService.getCurrentUserId();
        if (!accountId) {
            return of([]);
        }
        return this.http.get<RewardDetail[]>(`${this.baseUrl}/history`).pipe(
            catchError(() => {
                console.warn('Rewards backend not deployed yet. Returning mock reward history.');
                // Map the mock data to have the correct current accountId
                const userHistory = this.mockHistoryList.map(item => ({
                    ...item,
                    accountId: accountId
                }));
                return of(userHistory);
            })
        );
    }
}

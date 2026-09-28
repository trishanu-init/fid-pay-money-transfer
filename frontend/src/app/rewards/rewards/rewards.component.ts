import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject, forkJoin } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { RewardService } from '../../core/services/reward.service';
import { RewardDetail } from '../../core/models';
import { MatTableDataSource } from '@angular/material/table';

/**
 * Rewards Component
 * 
 * Displays total reward points, rewards system rules, and transaction reward details history.
 */
@Component({
    selector: 'app-rewards',
    templateUrl: './rewards.component.html',
    styleUrls: ['./rewards.component.scss']
})
export class RewardsComponent implements OnInit, OnDestroy {
    rewardPoints = 0;
    rewardHistory: RewardDetail[] = [];
    dataSource = new MatTableDataSource<RewardDetail>([]);
    displayedColumns = ['createdOn', 'transactionId', 'transactionAmount', 'pointsEarned'];
    isLoading = true;
    errorMessage = '';
    isMobile = false;

    private destroy$ = new Subject<void>();

    constructor(
        private rewardService: RewardService,
        private router: Router
    ) {
        this.checkMobile();
        window.addEventListener('resize', () => this.checkMobile());
    }

    ngOnInit(): void {
        this.loadRewardsData();
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
        window.removeEventListener('resize', () => this.checkMobile());
    }

    /**
     * Check viewport width for responsive card list layout
     */
    checkMobile(): void {
        this.isMobile = window.innerWidth < 768;
    }

    /**
     * Fetch reward points and history log list
     */
    loadRewardsData(): void {
        this.isLoading = true;
        this.errorMessage = '';

        forkJoin({
            points: this.rewardService.getRewardPoints(),
            history: this.rewardService.getRewardHistory()
        })
        .pipe(takeUntil(this.destroy$))
        .subscribe({
            next: (result) => {
                this.rewardPoints = result.points;
                // Sort history logs desc by creation date
                this.rewardHistory = result.history.sort((a, b) => 
                    new Date(b.createdOn).getTime() - new Date(a.createdOn).getTime()
                );
                this.dataSource.data = this.rewardHistory;
                this.isLoading = false;
            },
            error: (error) => {
                this.isLoading = false;
                this.errorMessage = error.message || 'Failed to load rewards data. Please try again later.';
                console.error('Error fetching rewards data:', error);
            }
        });
    }

    /**
     * Navigate back to dashboard
     */
    goBack(): void {
        this.router.navigate(['/dashboard']);
    }

    /**
     * Refresh data
     */
    refresh(): void {
        this.loadRewardsData();
    }
}

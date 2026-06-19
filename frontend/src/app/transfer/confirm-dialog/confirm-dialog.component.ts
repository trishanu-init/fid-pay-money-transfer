import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

export interface ConfirmDialogData {
    toAccountId: string;
    amount: number;
}

/**
 * Confirm Transfer Dialog
 * 
 * Displays transfer details for user confirmation before submission.
 */
@Component({
    selector: 'app-confirm-transfer-dialog',
    template: `
    <h2 mat-dialog-title>Confirm Transfer</h2>
    <mat-dialog-content>
      <div class="confirm-details">
        <p>You are about to transfer:</p>
        <div class="transfer-summary">
          <div class="amount-display">
            <span class="currency"></span>
            <span class="amount">{{ data.amount | appCurrency:false }}</span>
          </div>
          <mat-icon class="arrow-icon">arrow_downward</mat-icon>
          <div class="account-display">
            <mat-icon>account_circle</mat-icon>
            <span>Account: {{ data.toAccountId }}</span>
          </div>
        </div>
        <p class="confirm-warning">
          <mat-icon>info</mat-icon>
          This action cannot be undone. Please verify the details.
        </p>
      </div>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancel</button>
      <button mat-raised-button color="primary" [mat-dialog-close]="true" id="confirm-transfer-btn">
        <mat-icon>check</mat-icon>
        Confirm Transfer
      </button>
    </mat-dialog-actions>
  `,
    styles: [`
    .confirm-details {
      padding: var(--spacing-md) 0;
    }
    
    .transfer-summary {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: var(--spacing-lg);
      background: rgba(102, 126, 234, 0.05);
      border-radius: var(--radius-md);
      margin: var(--spacing-md) 0;
    }
    
    .amount-display {
      display: flex;
      align-items: baseline;
      gap: 4px;
      
      .currency {
        font-size: 1.25rem;
        color: var(--color-primary);
      }
      
      .amount {
        font-size: 2rem;
        font-weight: 700;
        color: var(--text-primary);
      }
    }
    
    .arrow-icon {
      margin: var(--spacing-md) 0;
      color: var(--color-primary);
    }
    
    .account-display {
      display: flex;
      align-items: center;
      gap: var(--spacing-sm);
      color: var(--text-secondary);
      font-size: 0.875rem;
      
      mat-icon {
        font-size: 20px;
        width: 20px;
        height: 20px;
      }
    }
    
    .confirm-warning {
      display: flex;
      align-items: center;
      gap: var(--spacing-sm);
      padding: var(--spacing-md);
      background: rgba(255, 152, 0, 0.1);
      border-radius: var(--radius-sm);
      color: #e65100;
      font-size: 0.813rem;
      margin: 0;
      
      mat-icon {
        font-size: 18px;
        width: 18px;
        height: 18px;
      }
    }
    
    mat-dialog-actions button {
      display: flex;
      align-items: center;
      gap: var(--spacing-xs);
    }
  `]
})
export class ConfirmTransferDialogComponent {
    constructor(
        public dialogRef: MatDialogRef<ConfirmTransferDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: ConfirmDialogData
    ) { }
}

import { Component, Input } from '@angular/core';

/**
 * Loading Spinner Component
 * 
 * Reusable loading indicator with optional overlay mode.
 */
@Component({
    selector: 'app-loading-spinner',
    template: `
    <div class="spinner-container" [class.overlay]="overlay">
      <div class="spinner">
        <div class="spinner-ring"></div>
        <div class="spinner-ring"></div>
        <div class="spinner-ring"></div>
      </div>
      <p *ngIf="message" class="spinner-message">{{ message }}</p>
    </div>
  `,
    styles: [`
    .spinner-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: var(--spacing-lg);
      
      &.overlay {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(255, 255, 255, 0.9);
        z-index: 1000;
      }
    }
    
    .spinner {
      width: 50px;
      height: 50px;
      position: relative;
    }
    
    .spinner-ring {
      position: absolute;
      width: 100%;
      height: 100%;
      border: 3px solid transparent;
      border-top-color: var(--color-primary);
      border-radius: 50%;
      animation: spin 1.2s cubic-bezier(0.5, 0, 0.5, 1) infinite;
      
      &:nth-child(1) {
        animation-delay: -0.45s;
      }
      
      &:nth-child(2) {
        animation-delay: -0.3s;
        width: 80%;
        height: 80%;
        top: 10%;
        left: 10%;
      }
      
      &:nth-child(3) {
        animation-delay: -0.15s;
        width: 60%;
        height: 60%;
        top: 20%;
        left: 20%;
      }
    }
    
    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }
    
    .spinner-message {
      margin-top: var(--spacing-md);
      color: var(--text-secondary);
      font-size: 0.875rem;
    }
  `]
})
export class LoadingSpinnerComponent {
    @Input() overlay = false;
    @Input() message?: string;
}

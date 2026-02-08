import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

// Angular Material Modules
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDividerModule } from '@angular/material/divider';
import { MatRippleModule } from '@angular/material/core';
import { MatBadgeModule } from '@angular/material/badge';

// Shared Components
import { LoadingSpinnerComponent } from './components/loading-spinner/loading-spinner.component';

// Shared Pipes
import { CurrencyFormatPipe } from './pipes/currency-format.pipe';

const materialModules = [
    MatToolbarModule,
    MatCardModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatIconModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatSnackBarModule,
    MatDialogModule,
    MatProgressSpinnerModule,
    MatMenuModule,
    MatTooltipModule,
    MatDividerModule,
    MatRippleModule,
    MatBadgeModule
];

/**
 * Shared Module
 * 
 * Contains reusable components, directives, pipes, and Material imports.
 * Import this module in every feature module that needs shared functionality.
 */
@NgModule({
    declarations: [
        LoadingSpinnerComponent,
        CurrencyFormatPipe
    ],
    imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        RouterModule,
        ...materialModules
    ],
    exports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        RouterModule,
        ...materialModules,
        LoadingSpinnerComponent,
        CurrencyFormatPipe
    ]
})
export class SharedModule { }

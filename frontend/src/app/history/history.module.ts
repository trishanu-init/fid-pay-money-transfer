import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../shared/shared.module';
import { HistoryComponent } from './history/history.component';
import { AuthGuard } from '../core/guards/auth.guard';

const routes: Routes = [
    {
        path: '',
        component: HistoryComponent,
        canActivate: [AuthGuard]
    }
];

/**
 * History Module
 * 
 * Transaction history display with table and responsive card layouts.
 */
@NgModule({
    declarations: [
        HistoryComponent
    ],
    imports: [
        SharedModule,
        RouterModule.forChild(routes)
    ]
})
export class HistoryModule { }

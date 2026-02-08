import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../shared/shared.module';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AuthGuard } from '../core/guards/auth.guard';

const routes: Routes = [
    {
        path: '',
        component: DashboardComponent,
        canActivate: [AuthGuard]
    }
];

/**
 * Dashboard Module
 * 
 * Main dashboard showing account overview and quick actions.
 */
@NgModule({
    declarations: [
        DashboardComponent
    ],
    imports: [
        SharedModule,
        RouterModule.forChild(routes)
    ]
})
export class DashboardModule { }

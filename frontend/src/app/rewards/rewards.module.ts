import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../shared/shared.module';
import { RewardsComponent } from './rewards/rewards.component';
import { AuthGuard } from '../core/guards/auth.guard';

const routes: Routes = [
    {
        path: '',
        component: RewardsComponent,
        canActivate: [AuthGuard]
    }
];

/**
 * Rewards Module
 * 
 * Displays total points, instructions/rules, and history log table.
 */
@NgModule({
    declarations: [
        RewardsComponent
    ],
    imports: [
        SharedModule,
        RouterModule.forChild(routes)
    ]
})
export class RewardsModule { }

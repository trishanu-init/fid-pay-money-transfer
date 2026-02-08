import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../shared/shared.module';
import { TransferComponent } from './transfer/transfer.component';
import { ConfirmTransferDialogComponent } from './confirm-dialog/confirm-dialog.component';
import { AuthGuard } from '../core/guards/auth.guard';

const routes: Routes = [
    {
        path: '',
        component: TransferComponent,
        canActivate: [AuthGuard]
    }
];

/**
 * Transfer Module
 * 
 * Money transfer functionality with confirmation dialog.
 */
@NgModule({
    declarations: [
        TransferComponent,
        ConfirmTransferDialogComponent
    ],
    imports: [
        SharedModule,
        RouterModule.forChild(routes)
    ]
})
export class TransferModule { }

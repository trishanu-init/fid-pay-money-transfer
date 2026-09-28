import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../shared/shared.module';
import { TransferComponent } from './transfer/transfer.component';
import { ConfirmTransferDialogComponent } from './confirm-dialog/confirm-dialog.component';
import { OtpDialogComponent } from './otp-dialog/otp-dialog.component';
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
 * Money transfer functionality with confirmation dialog and OTP verification.
 */
@NgModule({
    declarations: [
        TransferComponent,
        ConfirmTransferDialogComponent,
        OtpDialogComponent
    ],
    imports: [
        SharedModule,
        RouterModule.forChild(routes)
    ]
})
export class TransferModule { }


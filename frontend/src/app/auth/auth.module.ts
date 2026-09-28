import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../shared/shared.module';
import { LoginComponent } from './login/login.component';
import { RegistrationComponent } from './registration/registration.component';
import { ForgotPasswordDialogComponent } from './forgot-password-dialog/forgot-password-dialog.component';

const routes: Routes = [
    {
        path: '',
        component: LoginComponent
    },
    {
        path: 'register',
        component: RegistrationComponent // Add route for registration
    }
];

/**
 * Auth Module
 * 
 * Contains authentication-related components (Login and Register).
 */
@NgModule({
    declarations: [
        LoginComponent,
        RegistrationComponent, // Declare RegistrationComponent
        ForgotPasswordDialogComponent
    ],
    imports: [
        SharedModule,
        RouterModule.forChild(routes) // Use the routes with login and register paths
    ]
})
export class AuthModule { }

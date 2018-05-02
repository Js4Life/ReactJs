import { NgModule } from '@angular/core';
import { CommonModule, HashLocationStrategy, LocationStrategy } from '@angular/common';
import { BrowserModule  } from '@angular/platform-browser';
import { Routes, RouterModule } from '@angular/router';

import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './pages/login/login.component';
import { SignupComponent } from './pages/signup/signup.component';
import { DeviceDetailsComponent } from 'app/pages/device-details/device-details.component';
// import { NotFoundComponent } from './pages/not-found/not-found.component';


const routes: Routes =[
      { path: 'home',      component: HomeComponent },
      { path: 'login',     component: LoginComponent },
      { path: 'signup',    component: SignupComponent },
      { path: '',          redirectTo: 'home', pathMatch: 'full' },
      // { path: '**',        component: NotFoundComponent },
      { path: 'device-details/:deviceId',     component: DeviceDetailsComponent},
];

@NgModule({
  imports: [
    CommonModule,
    BrowserModule,
    RouterModule.forRoot(routes, { useHash: true })
  ],
  providers: [
    { provide: LocationStrategy, useClass: HashLocationStrategy }
  ],
  exports: [
    RouterModule
  ],
})
export class AppRoutingModule { }


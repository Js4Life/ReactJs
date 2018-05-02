import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HomeModule } from './home/home.module';
import { LoginComponent } from './login/login.component';
import { SignupComponent } from './signup/signup.component';
import { NotFoundComponent } from './not-found/not-found.component';
import { DeviceDetailsComponent } from './device-details/device-details.component';
import { ServicesModule } from 'app/services/services.module';
import { AngularEchartsModule } from 'ngx-echarts';
import { ComponentsModule } from 'app/components/components.module';

import { AgmCoreModule } from '@agm/core';

@NgModule({
  imports: [
    CommonModule,
    HomeModule,
    AngularEchartsModule,
    ComponentsModule,
    AgmCoreModule.forRoot({
      apiKey: 'AIzaSyBeMiufX0-7mpOnlyd0Q-vuLktyV4FMrOw'
    })
  ],
  declarations: [
    LoginComponent, 
    SignupComponent, 
    NotFoundComponent, DeviceDetailsComponent,
  ],
  providers : [ServicesModule]
})
export class PagesModule { }

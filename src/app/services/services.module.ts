import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Http, Response } from '@angular/http';


@NgModule({
  imports: [
    CommonModule
  ],
  declarations: []
})
export class ServicesModule {

  deviceCount: number;
  devices: any;
  awsHost = 'https://j0o1ovq2m7.execute-api.ap-south-1.amazonaws.com/dev'; 
  // https://25ggnyf2vk.execute-api.ap-southeast-2.amazonaws.com/dev/devices
  constructor(private http: Http) { }

  getUserCount() {
    this.http.get(this.awsHost + '/devices')
      .subscribe(data => {
        let response = data.json();
        this.deviceCount = response.length;
        this.devices = response;
      }, err => {
        console.log(err);
      });
  }

  getDeviceList() {
    return this.http.get(this.awsHost + '/devices')
      .map(
        (response: Response) => response.json()
      );
  }

  getDevice(deviceId) {
    return this.http.get(this.awsHost + '/devices/' + deviceId)
      .map(
      (response: Response) => response.json()
      );
  }
  getDeviceReadings(deviceId) {
    return this.http.get(this.awsHost + '/devices/' + deviceId + '/readings')
      .map(
      (response: Response) => response.json()
      );
    // .subscribe(data => {
    //     let response = data.json();
    //     this.deviceCount = response.length;
    //     this.devices = response;
    // }, err => {
    //     console.log(err);
    // });
  }

  getAlerts() {
    return this.http.get(this.awsHost + '/alerts/')
      .map(
      (response: Response) => response.json()
      );
  }

}

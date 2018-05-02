import { Component, OnInit } from '@angular/core';

import { Router, ActivatedRoute } from '@angular/router';
import { Http } from '@angular/http';
import { ServicesModule } from '../../../services/services.module';

declare function require(path: string): any;

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  data: any = 'Sushil';
  title = 'app';

  deviceCount: number;
  alertCount: number;
  devices: any;
  // devices: any = [{"updated":1516814540,"alerts":{},"z_mms":4,"y_mms":11,"x_mms":11,"id":"222","x_hz":25,
  // "name":"Sensor-300577","z_hz":36,"y_hz":7},{"updated":1516814095,"alerts":{"y_hz":40,"z_mms":18,"x_mms":24,
  // "x_hz":55},"z_mms":18,"y_mms":2,"x_mms":23,"id":"555","x_hz":55,"name":"Sensor-195732","z_hz":11,"y_hz":40},
  // {"updated":1516813894,"alerts":{"y_hz":40,"z_mms":17},"z_mms":17,"y_mms":4,"x_mms":2,"id":"444","x_hz":35,
  // "name":"Sensor-300579","z_hz":15,"y_hz":40},{"updated":1516814095,"alerts":{"z_mms":22,"x_mms":24,"z_hz":60},
  // "z_mms":22,"y_mms":10,"x_mms":24,"id":"111","x_hz":10,"name":"Sensor-300578","z_hz":60,"y_hz":24},
  // {"id":"OM","thiscolumn":{"message":"Missing required node: state","code":400}},
  // {"updated":1516814540,"alerts":{"x_hz":50},"z_mms":6,"y_mms":20,"x_mms":17,"id":"333","x_hz":50,
  // "name":"Sensor-300575","z_hz":13,"y_hz":8}]



  constructor(private http: Http,
    private router: Router,
    private serviceModule: ServicesModule,
    private activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    require('../../../../assets/js/charts.js')();
    this.getDeviceList();
    this.getAlertCount();
  }

  getDeviceList() {
    this.serviceModule.getDeviceList().
    subscribe(data => {
        let response = data;
        this.deviceCount = response.length;
        this.devices = response;
    }, err => {
        console.log(err);
    });
  }
  
  getAlertCount() {
    this.serviceModule.getAlerts().
    subscribe(data => {
        this.alertCount = data.length;
    });
    // this.http.get( 'https://gzohp9nx5g.execute-api.ap-southeast-2.amazonaws.com/dev/alerts')
    // .subscribe(data => {
    //     let response = data.json();
    //     this.alertCount = response.length;
    // }, err => {
    //     console.log(err);
    // });
  }

  goToDeviceDetails(deviceId) {
    this.router.navigate(['device-details', deviceId]);
  }
}

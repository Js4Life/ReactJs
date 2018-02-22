import { Component ,OnInit} from '@angular/core';

import { BaseHttpService } from './services/base-http.service';
import { AppRouteConfig } from './app.router-config';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'app';

  dataLoading:boolean=true;

  constructor(private baseSvc: BaseHttpService, private arc:AppRouteConfig) {
      baseSvc.loading.subscribe(flag => this.showLoading(flag));
      baseSvc.gotoLogin.subscribe(flag => this.gotoLogin(flag));
  }

  showLoading(flag: boolean):void{
    this.dataLoading=flag;
  }
  gotoLogin(flag: boolean):void{
    if(flag)
      this.arc.gotoLogin();
  }
  ngOnInit(){
    // this.baseSvc.loading.subscribe(flag => this.showLoading(flag));
  }
}

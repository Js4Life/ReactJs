import { Component, OnInit } from '@angular/core';
import { AnalysisService } from '../services/analysis/analysis.service';
import {AnalysisResponseVM} from "../models/analysisresponse.model";
import {Constants} from '../models/constants.model';
import { ClientState} from '../providers/clientstate.provider';

@Component({
  selector: 'home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  //hasAnalysisHistory:boolean=true;
  recentAnalysis:AnalysisResponseVM[]=[];
  loading : boolean = false;
  user:any;

  constructor(private analysisSvc:AnalysisService, private clientState:ClientState) { }

  onInitialise(){
    //return this.baseService.invokeService(this._url,'',1);
    this.loading = true;
    this.user = JSON.parse(localStorage.getItem(Constants.LOGGED_USER));
    if(this.user.role.roleId==1){
      this.analysisSvc.getAllAnalysisForCCO().then(data=>{
        this.loading = false;
        this.recentAnalysis= data;
      })
    }
    else{
      this.analysisSvc.getAllAnalysis().then(data=>{
        this.loading = false;
        this.recentAnalysis= data;
      })
    }
  }

  ngOnInit() {
    this.onInitialise();
  }

}

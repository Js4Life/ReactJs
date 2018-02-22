import { Component, OnInit, Input, ViewContainerRef} from '@angular/core';
import * as _ from 'underscore';
import { Http } from "@angular/http";
import { ToastsManager } from 'ng2-toastr/ng2-toastr';
import { AppRouteConfig} from '../app.router-config';

import { BaseHttpService } from "../services/base-http.service";
import { environment } from "../../environments/environment";

import { AnalysisService } from '../services/analysis/analysis.service';
import {AnalysisResponseVM} from "../models/analysisresponse.model";

@Component({
  selector: 'app-analysis',
  templateUrl: './analysis.component.html',
  styleUrls: ['./analysis.component.css'],
})

export class AnalysisComponent implements OnInit  {
 
  @Input() recentAnalysis : AnalysisResponseVM[];
  filedata:any = [];
  allFiles :boolean=true;
  MarkedFiles :boolean=false;
  hoverBtns:any=[];

  analysisList : Array<AnalysisResponseVM>;
  constructor(private arc:AppRouteConfig,   private analysisSvc:AnalysisService, public toastr: ToastsManager, vcr: ViewContainerRef) {
    this.toastr.setRootViewContainerRef(vcr);
     }

  onInitialise(){
    this.filedata = this.recentAnalysis;
  }

  hoverItem(item){
    if(item.mark){
      this.hoverBtns=[{name:'Delete'},{name:'Rename'},{name:'Unmark'}];
    }
    else{
      this.hoverBtns=[{name:'Delete'},{name:'Rename'},{name:'Mark'}];
    }
  }
 
  ngOnInit(){
    // this.getList().then( data => {
    //   this.newfiledata = this.filedata= data  
    // })
    this.onInitialise();
   }

  onsearch(value){
    this.recentAnalysis = _.filter(this.filedata, function(obj) { 
      if(obj.name.toLowerCase().indexOf(value.toLowerCase()) >= 0){
        return obj;
      }
      if(obj.id.toLowerCase().indexOf(value.toLowerCase()) >= 0){
        return obj;
      }
    });
    if(this.filedata.length == 0) {
      this.filedata = this.filedata;
    }
  }

  onClickBtn(analysisId,btnName,e,idx){
    e.stopPropagation();
     console.log('analysis : ' + analysisId + " " + btnName);
    switch(btnName){
      case 'Mark':
      // console.log('mark : ');
        break;
      case 'Unmark':
        break;
      case 'Rename':
        break;
      case 'Delete':
      this.analysisSvc.deleteAnalysisById(analysisId).then(data=>{
        this.recentAnalysis.splice(idx,1);
        this.toastr.success('Analysis deleted');
      })  
      // this.recentAnalysis.splice(analysisId,1);
        break;
    }
  }

  onMark() {
    this.recentAnalysis = _.filter(this.filedata,function(obj){
      if(obj.mark == true){
        return obj;
      }
    })
  }

  all(opt,e){
    $(e.currentTarget).removeClass('Marked').addClass('All').siblings().removeClass('All').addClass('Marked');
    if(opt=='all'){
      this.recentAnalysis = this.filedata;
    }
    else{
      this.onMark();
    }
  }

  goToSavedAnalysis(analysisData){
    console.log(analysisData);
    this.arc.gotoSelectedStates(analysisData.id);
  }
}

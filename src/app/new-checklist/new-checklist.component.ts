import { Component, OnInit, ViewContainerRef } from '@angular/core';
import { AppRouteConfig } from '../app.router-config';
import { DOCUMENT } from '@angular/platform-browser';
import { Inject } from '@angular/core';
import { ToastsManager } from 'ng2-toastr/ng2-toastr';
import { UserSvc } from '../services/user/user.service';
import { AnalysisService } from '../services/analysis/analysis.service';
import { NewChecklistRequestVM } from '../models/newchecklistrequest.model';
import { ClientState } from '../providers/clientstate.provider';

import { Constants , HttpVerbs} from '../models/constants.model';

import * as _ from 'underscore';


@Component({
  selector: 'new-checklist',
  templateUrl: './new-checklist.component.html',
  styleUrls: ['./new-checklist.component.css']
})
export class NewChecklistComponent implements OnInit {

  checklist:NewChecklistRequestVM = new NewChecklistRequestVM;
  userList:any=[];
  updatedUserList:any=[];
  currUser:any;
  msg:string;
  errorMsg:string;
  constructor(@Inject(DOCUMENT) private document: any, private arc:AppRouteConfig, private userSvc:UserSvc,
    private analysisSvc:AnalysisService, private clientState:ClientState, public toastr: ToastsManager, vcr: ViewContainerRef) {
      this.toastr.setRootViewContainerRef(vcr);
     }

  selectUser(user){
    if(this.currUser.id.replace('#','') == user.id.replace('#','')){}
    else{
      user.isSelected=!user.isSelected;
      if(!this.checklist.users){
        this.checklist.users=[];
        this.checklist.users.push(user.id);
      }
      else {
        if(_.contains(this.checklist.users,user.id)){
          this.checklist.users = _.reject(this.checklist.users,function(id){ return id == user.id; });
        }
        else{
          this.checklist.users.push(user.id);
        }
      }
    }
  }

  getAllUsers(){
    this.userSvc.getAllUsers().then(data=>{
      this.userList = data;
      this.userList.forEach(obj=>{
        obj.isSelected=false;
        if(this.currUser.role.roleId==1){
            if(obj.role.roleId!=1){
              this.updatedUserList.push(obj);
            }
        }
        if(this.currUser.role.roleId==2){
          if(obj.role.roleId!=1 && obj.role.roleId!=2){
            this.updatedUserList.push(obj);
          }
        }
      })
    })
  }

  createChecklist(){
     if(!this.checklist.name || !this.checklist.description || !this.checklist.users){
        // this.errorMsg="name,description and user can't be null";
        // setTimeout(()=>{ this.errorMsg = "" }, 4000);
        this.toastr.error('name,description and user can not be null');
      }
    else{
          this.analysisSvc.createChecklist(this.clientState.lastAnalysisId,this.checklist).then(data=>{
            if(data){
              console.log(data);
              this.checklist = new NewChecklistRequestVM;
              // alert("checklist "+data+" created" );
              // this.msg="checklist created";
              // setTimeout(()=>{ this.msg = "" }, 4000);
              this.toastr.success('checklist created');
        
            }
          })
      }
  }
  ngOnInit() {
    this.currUser =  JSON.parse(localStorage.getItem(Constants.LOGGED_USER));
    this.getAllUsers();
  }
}

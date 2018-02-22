import { Component, OnInit, ViewContainerRef } from '@angular/core';
import { AppRouteConfig } from '../app.router-config';
import { ActivatedRoute } from '@angular/router';
import { ToastsManager } from 'ng2-toastr/ng2-toastr';

import { checklistItemVM} from '../models/checklistItem.model';

import { ClientState } from '../providers/clientstate.provider';
import { ChecklistSvc } from '../services/checklist/checklist.service';
import { MasterDataSvc } from '../services/masterdata/masterdata.service';
import {UserSvc } from '../services/user/user.service';
import { Constants , HttpVerbs} from '../models/constants.model';



import * as _ from 'underscore';

declare var CKEDITOR : any;

@Component({
  selector: 'checklist-details-view',
  templateUrl: './checklist-details-view.component.html',
  styleUrls: ['./checklist-details-view.component.css']
})
export class ChecklistDetailsViewComponent implements OnInit {

  sub :any;
  currUser:any;
  // showItem:boolean=true;
  statusList:any = ['NEW','INPROGRESS','PENDING','APPROVED'];
  componentList:any = [];
  statusListOpen:boolean=false;
  userListOpen:boolean = false;
  userList:any;
  // selectedUserList:any=[];
  associatedContexts_doc_details:any;
  currentTaggedParaList:any;
  checklistItemList:Array<checklistItemVM>=[];
  currentRequirement:any;
  referenceList:any=[];
  counter:any=[1,2,3];
  editorInstanceLength:number=0;
  currentReqIdx:any = 0;
  checklist:any;
  linkedAnalysisId:any;
  savedmsg:string;
  unsavedmsg:string;
  showOrHide:string;
  loading : boolean = false;
  showTaggedPara:boolean = false;
  showTopContext:boolean = false;
  onTabClickEvent:any;

  constructor(private arc:AppRouteConfig, private route: ActivatedRoute, private clientstate:ClientState,
   private checklistSvc: ChecklistSvc, private masterDataSvc: MasterDataSvc, private userSvc:UserSvc, public toastr: ToastsManager, vcr: ViewContainerRef) {
    this.toastr.setRootViewContainerRef(vcr);
    }

  gotoPrevious(){
    this.arc.goback();
  }

  getInitial(name){
    return name.substring(0,2).toUpperCase();
  }

  addRequirement(len){
    var checklistItemObj = new checklistItemVM();
    checklistItemObj.itemStatus='NEW';
    this.checklistItemList.push(checklistItemObj);
    this.onTabClick(this.checklistItemList.length - 1, null, this.checklistItemList[this.checklistItemList.length - 1]);
    this.deselectOlderItems(event);
  }
  deselectOlderItems(e){
    $(e.currentTarget).parent().siblings().children().removeClass('activeTab');
  }

  changeStatus(idx,status){
    this.currentRequirement.itemStatus = status;
    this.statusListOpen=false;
  }

  showStatusList(current){
    if(( this.currUser.role.roleId==3 && current.currentAssigneeId==undefined ) || (this.currUser.role.roleId==3 && current.currentAssigneeId!=undefined && current.currentAssigneeId.replace('#','')==this.currUser.id)){
        if(this.showOrHide=='show'){
          this.statusListOpen=true;
        }
    }
    else if (this.currUser.role.roleId!=3 && this.showOrHide=='show') {
      this.statusListOpen=true;
    } else {
        this.statusListOpen=false;
    }
  }

  showUserList(current){
    if(( this.currUser.role.roleId==3 && current.currentAssigneeId==undefined ) || (this.currUser.role.roleId==3 && current.currentAssigneeId!=undefined && current.currentAssigneeId.replace('#','')==this.currUser.id)){
      if(this.showOrHide=='show'){
        this.userListOpen=true;
      }
    }
    else if (this.currUser.role.roleId!=3 && this.showOrHide=='show') {
    this.userListOpen=true;
    } else {
      this.userListOpen=false;
    }
  }

  changeUserAssignment(index, user){
    if(this.currUser.id.replace('#','') == user.id.replace('#','')){}
    else{
      this.currentRequirement.assigneeId = user.id;
      // this.checklistItemList[this.currentReqIdx].assignedUsers.push(user);
      this.currentRequirement.currentAssigneeName = user.name;
      this.userListOpen=false;
    }
  }

  getComponentByContextIds(){
        this.associatedContexts_doc_details.contributingContexts.forEach(obj=>{
          //call the service call for each context to get the componentType
          this.masterDataSvc.getComponentTypeByContextId(obj.id).then(data=>{
            if(data.length > 0){
                data.forEach(obj=>{
                  var obj1 = _.findWhere(this.componentList,obj);
                  if(!obj1)
                      this.componentList.push(obj);
                })
            } 
          })
        })
  }

  onTabClick(idx,e,chkItem){
    // this.showItem = false;
    this.showOrHide="show";
    this.cancelTaggedPane();
    this.cancelTopContextPane();

    // disable user list and status assginment drop down when user switch to other requirements
    this.userListOpen=false;
    this.statusListOpen=false;
    if(e)
      $(e.currentTarget).addClass('activeTab').siblings().removeClass('activeTab');
    this.currentReqIdx = idx;
    this.currentRequirement = chkItem;
    if(!chkItem){
      this.currentRequirement = this.checklistItemList[this.currentReqIdx];
      console.log('inside ontab');
      console.log(this.currentRequirement);
      if(this.currentRequirement.itemStatus=='APPROVED'){
          this.showOrHide="hide";    
      }
      console.log(this.showOrHide);
    }
    if(chkItem && chkItem.linkedAnalysis){
      this.associatedContexts_doc_details = chkItem.linkedAnalysis;
      console.log('inside ontab');
      console.log(chkItem);
      if(chkItem.itemStatus=='APPROVED'){
          this.showOrHide="hide";    
      }
      console.log(this.showOrHide);
    }
    else{
      this.associatedContexts_doc_details = this.checklist.primaryAnalysis;
    }

    this.currentTaggedParaList = [];
    if(this.currentRequirement.linkedAnalysis)
      this.currentTaggedParaList = this.currentRequirement.linkedAnalysis.taggedParagraphList;

    //generate component list for the context of current analysis
    this.getComponentByContextIds();

    // this.showItem = true;
    var i=0;
    for(var key in CKEDITOR.instances){
      if (CKEDITOR.instances.hasOwnProperty(key)) {
        if(i==0)
          CKEDITOR.instances[key].setData(this.currentRequirement.requirementDetails);
        if(i==1)
          CKEDITOR.instances[key].setData(this.currentRequirement.implementationSteps);
      }
      i++;
    }

    
  }

  onTaggedParaClick(){
    if(this.currentTaggedParaList && this.currentTaggedParaList.length>0){
      this.showTaggedPara = true;
    }
  }
  cancelTaggedPane(){
    this.showTaggedPara = false;
  }

  onTopContextClick(){
    if(this.associatedContexts_doc_details.contributingContexts && this.associatedContexts_doc_details.contributingContexts.length>0){
      this.showTopContext = true;
    }
  }
  cancelTopContextPane(){
    this.showTopContext = false;
  }

  deleteTopContext(index, event){
    
    console.log(this.associatedContexts_doc_details.contributingContexts);
    this.associatedContexts_doc_details.contributingContexts.splice(index,1);
    console.log(index);
    console.log(this.associatedContexts_doc_details.contributingContexts);
  }

  saveChecklistItem(){
    this.checklistSvc.getEditorChanges(true);
    this.onTabClick(this.currentReqIdx, this.onTabClickEvent, null);
  }

  saveEmitter(emittedData){
    this.editorInstanceLength++;
    if(emittedData.type=="requirement")
      this.currentRequirement.requirementDetails=emittedData.data;
    if(emittedData.type=="implementation")
      this.currentRequirement.implementationSteps=emittedData.data;
    if(this.editorInstanceLength == Object.keys(CKEDITOR.instances).length)
      this.save();
  }

  save(){
    this.editorInstanceLength=0;
    if(this.currentRequirement && !this.currentRequirement.id ){
      this.checklistSvc.saveChecklistItem(this.clientstate.currentChecklistId, this.currentRequirement).then(data=>{
        this.linkedAnalysisId=data.linkedAnalysisId;
        this.currentRequirement = _.extend(this.currentRequirement, data);
        // this.savedmsg="Checklist Saved";
        // setTimeout(()=>{ this.savedmsg = "" }, 4000);
        this.toastr.success('Checklist Saved');
        this.getChecklistById(this.clientstate.currentChecklistId);
      })
    }
    else {
      this.checklistSvc.updateChecklistItem(this.clientstate.currentChecklistId, this.currentRequirement).then(data=>{
        this.linkedAnalysisId=data.linkedAnalysisId;
        this.currentRequirement = _.extend(this.currentRequirement, data);
        // this.savedmsg="Checklist Saved";
        // setTimeout(()=>{ this.savedmsg = "" }, 4000);
        this.toastr.success('Checklist Saved');
        this.getChecklistById(this.clientstate.currentChecklistId);      
      })
    }
    
  }

  openInAnalysis(checklistItem){
    if(checklistItem.linkedAnalysisId)
      this.arc.gotoSelectedStates(checklistItem.linkedAnalysisId, this.currentRequirement.id);
    else{
      // this.unsavedmsg="save item first";
      // setTimeout(()=>{ this.unsavedmsg = "" }, 4000);
      this.toastr.error('save item first');
    }
  }

  getChecklistById(id){
    this.loading = true;
    this.checklistSvc.getChecklistById(this.clientstate.currentChecklistId).then(data=>{
      this.checklist = data;
      this.checklistItemList = data.checkListItems;

      if(this.checklistItemList.length==0){
        var checklistItemObj = new checklistItemVM();
        checklistItemObj.itemStatus='NEW';
        this.checklistItemList.push(checklistItemObj);
        this.associatedContexts_doc_details = data.primaryAnalysis;
      }
      else{
        this.associatedContexts_doc_details = data.checkListItems[0].linkedAnalysis;
      }
      // this.formatAssociatedDetails();
      //generate component list for the context of current analysis
      this.getComponentByContextIds();
      this.loading = false;
     // this.onTabClick(0,null,null);
     this.onTabClick(this.currentReqIdx, this.onTabClickEvent, null);
    })
  }

  getAllUsers(){
    this.userSvc.getAllUsers().then(data=>{
      this.userList = data;
      this.userList.forEach(obj=>{
        obj.isSelected=false;
      })
    })
  }

  signOut(){
    localStorage.clear();
    this.arc.gotoLogin();
  }

  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      this.clientstate.currentChecklistId = params['id'];
      this.getChecklistById(this.clientstate.currentChecklistId);
    });

    this.getAllUsers();
    this.currUser = JSON.parse(localStorage.getItem(Constants.LOGGED_USER));
  }

}

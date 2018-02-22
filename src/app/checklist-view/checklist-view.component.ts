import { Component, OnInit } from '@angular/core';
import { AppRouteConfig} from '../app.router-config';
import { ClientState } from '../providers/clientstate.provider';
import { UserSvc } from '../services/user/user.service';
import { Constants , HttpVerbs} from '../models/constants.model';
import { ChecklistSvc } from '../services/checklist/checklist.service';


declare var $:any;
import * as _ from 'underscore';
import * as vis from 'vis';

@Component({
  selector: 'checklist-view',
  templateUrl: './checklist-view.component.html',
  styleUrls: ['./checklist-view.component.css']
})
export class ChecklistViewComponent implements OnInit {

  checklists:any = [];
  checklistsTemp:any;
  checklistDataForGraph:any = [];
  currUser:any;
  graphView:boolean = false;
  graphData:any=[];
  groups:any=[];
  ctxtFilter:string=null;
  itemtypeFilter:string = null;
  // labels:any=[{name:"NEW", color:'grey'},{name:"INPROGRESS", color:'#0eaed2'},
  //   {name:"PENDING", color:'#eaaa34'},{name:"APPROVED", color:'#8bd418'}];
  loading = false;
  downloadLink:string

  constructor(private arc:AppRouteConfig, private clientstate:ClientState, private userSvc:UserSvc, private checklistSvc:ChecklistSvc) { }

/*
  showGraphView(){
    var len = this.checklistsTemp.length;
    var checklistDataForGraph = [];
    this.checklistsTemp.forEach(obj=>{
      this.checklistSvc.getChecklistById(obj.id).then(data=>{
        checklistDataForGraph.push(data);
        len--;
        if(len==0){
          this.checklistDataForGraph = checklistDataForGraph;
          // this.createGraphData();
          this.graphView = !this.graphView;
        }
      })
    })
  }

  */

  showGraphView(){
    var checklistDataForGraph = [];
      this.checklistSvc.getDashboard().then(data=>{
        console.log(data);
          this.checklistDataForGraph = data;
          // this.createGraphData();
          this.graphView = !this.graphView;
      })
  }

  // createGraphData(){
  //   var obj = {};
  //   this.groups = [];
  //   this.graphData = [];
    
  //   this.labels.forEach(key=>{
  //     var obj = {label:key.name, data:[],backgroundColor:key.color};
  //     this.checklistDataForGraph.forEach(chk=>{
  //       if(this.groups.length<this.checklistDataForGraph.length)
  //         this.groups.push(chk.name); 
  //       var x = _.where(chk.checkListItems, {itemStatus: key.name});
  //       obj.data.push(x.length);
  //     })
  //     this.graphData.push(obj);
  //   })
  //   this.graphView = !this.graphView;
  // }

  pieClick(emittedData){
    var chkList = [];
    this.ctxtFilter=emittedData.contextName;
    this.itemtypeFilter = emittedData.itemtype;
    let checkListIds = emittedData.checkListIds;
    checkListIds.forEach(id=>{
      var obj = _.findWhere(this.checklists,{ id: id.replace('#','')});
      if(obj)
        chkList.push(obj);
    })
    this.checklistsTemp = chkList;
    this.graphView = !this.graphView;
  }

  showAllChecklist(){
    this.checklistsTemp = this.checklists;
    this.ctxtFilter=this.itemtypeFilter = null;
  }

  gotodetails(chk){
    this.clientstate.currentChecklistId = chk.id;
    this.arc.gotoChecklistDetails(chk.id);
  }
  searchFocusIn(){
    $('.searchChecklistBtn').addClass('searchChecklistFocus').removeClass('searchChecklistBtn');
  }
  searchFocusOut(){
    $('.searchChecklistFocus').addClass('searchChecklistBtn').removeClass('searchChecklistFocus');
  }
  onSearch(searchStr){
    if(searchStr.length>0)
    this.checklistsTemp = _.filter(this.checklists, function(obj) { 
      if(obj.name.toLowerCase().indexOf(searchStr.toLowerCase()) >= 0){
        return obj;
      }
    });
    if(searchStr.length==0){
      this.checklistsTemp = Object.assign([], this.checklists);
    }
  }
  downloadChecklistFile(chk,e){
    e.stopPropagation();
    this.checklistSvc.getChecklistReportById(chk.id).then(data=>{
      this.downloadLink = data.reportLink;
      window.open(this.downloadLink);
    })
  }
  deleteChecklist(chk,e,idx){
    e.stopPropagation();
    console.log(chk);
    this.checklistSvc.deleteChecklistById(chk.id).then(data=>{
      this.checklistsTemp.splice(idx,1);
    })
  }

  ngOnInit() {
    this.loading = true;
    this.currUser = JSON.parse(localStorage.getItem(Constants.LOGGED_USER));
    
    if(this.currUser.role.roleId==3){
      this.userSvc.getChecklistByUserId(this.currUser.id).then(data=>{
        this.checklists = data;
        this.checklistsTemp = Object.assign([], this.checklists);
        this.loading = false;
      })
    }
    else{
      // this.userSvc.getChecklistByUserId(this.currUser.id).then(data=>{
      this.checklistSvc.getAllChecklist().then(data=>{
        this.checklists = data;
        this.checklistsTemp = Object.assign([], this.checklists);
        this.loading = false;
      })
    }
  }
  

}

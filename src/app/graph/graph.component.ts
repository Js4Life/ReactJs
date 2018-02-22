import { Component, OnInit, Input,Output, EventEmitter } from '@angular/core';
import { AppRouteConfig} from '../app.router-config';
import { ClientState } from '../providers/clientstate.provider';
import { ChecklistSvc } from '../services/checklist/checklist.service';

import * as vis from 'vis';
import * as _ from 'underscore';


@Component({
  selector: 'graph',
  templateUrl: './graph.component.html',
  styleUrls: ['./graph.component.css']
})
export class GraphComponent implements OnInit {

  items:any;
  groups:any;
  @Input() data:any;
  @Output() change: EventEmitter<any> = new EventEmitter<any>();
  pieChartDataList:any=[];
  loadPie:boolean = false;

  graphType:string;

  typeBtns:any=[
    {id:'checklist',name:'Checklist'},
    {id:'context',name:'Context'},
    {id:'componentType',name:'Component Type'},
    {id: 'businessSegment', name: 'Business Segment'},
    {id: 'product', name: 'Product'}
  ]
  labels:any=[{name:"newCount", color:'#C0C0C0'},{name:"inProgressCount", color:'#eaaa34'},
    {name:"pendingCount", color:'#0eaed2'},{name:"completedCount", color:'#8bd418'}];

  doughnutChartColors: any[] = [{ backgroundColor: ['#8bd418', '#0eaed2','#C0C0C0', '#eaaa34'] }];
  barColors:any = [{backgroundColor:[]}]; 

  constructor(private arc:AppRouteConfig, private clientstate:ClientState, private checklistSvc:ChecklistSvc) { }

  public barChartOptions:any = {
    scaleShowVerticalLines: false,
    responsive: true,
    maintainAspectRatio:false,
    stack : true,
     scales: {
          xAxes: [{
              stacked: true,
          }],
          yAxes: [{
              stacked: true
          }]
      }
  };
  public chartClicked(e:any):void {
    var chklistName = e.active[0]._view.label;
    this.gotodetails(_.findWhere(this.data,{name:chklistName}));
  }
  public chartHovered(e:any):void {
    console.log(e);
  }

  public piechartClicked(e:any,checkListIds,ctxt):void {
    this.change.emit({checkListIds:checkListIds, contextName:ctxt.name, itemtype:ctxt.itemtype});
  }

  public piechartHovered(e:any):void {
    console.log(e);
  }



  gotodetails(chk){
    this.clientstate.currentChecklistId = chk.id;
    this.arc.gotoChecklistDetails(chk.id);
  }
  typeClick(typeId,e){
    this.loadPie = false;
    if(e){
      $(e.currentTarget).addClass('typeActive').siblings().removeClass('typeActive');
    }
    this.graphType=typeId;
    
    switch(typeId){
      case 'checklist':
        this.createBargraphData();
        break;
      case 'context':
        this.checklistSvc.getContextCompliance().then(data=>{
          this.createPieChart(data);
        })
        break;
      case 'componentType':
        this.checklistSvc.getComponentCompliance().then(data=>{
          this.createPieChart(data);
        })
        break;
      case 'businessSegment':
        this.checklistSvc.getBusinessSegmentCompliance().then(data=>{
          this.createPieChart(data);
        })
        break;
      case 'product':
        this.checklistSvc.getProductCompliance().then(data=>{
          this.createPieChart(data);
        })
        break;
    }
  }
  createPieChart(data){
    var dataList=[];
    this.pieChartDataList=[];
    data.forEach(obj=>{
      var emptyObj={name:obj.name,data:{},checkListIds:obj.checkListIds,itemtype:obj.itemtype};
      var pieLabels=[], pieData=[];
      
      for (var key in obj.complianceCounter) {
          if (obj.complianceCounter.hasOwnProperty(key)) {
              pieLabels.push(key.replace('Count',''));
              pieData.push(obj.complianceCounter[key])
          }
      }
      emptyObj.data = {labels:pieLabels, labelData:pieData};
      dataList.push(emptyObj);
    })
    this.pieChartDataList = dataList;
    this.loadPie = true;
  }

  createBargraphData(){
    console.log(this.data);
    var obj = {}, items = [];
    this.groups = [];
    this.items = [];
    this.labels.forEach(key=>{
      var obj = {label:key.name, data:[],backgroundColor:key.color};
      this.data.forEach(chk=>{
        if(this.groups.length<this.data.length)
          this.groups.push(chk.name); 
        // var x = _.where(chk.complianceCounter, {itemStatus: key.name});
        obj.data.push(chk.complianceCounter[key.name]);
      })
      items.push(obj);      
    })
    this.items = items;
  }

  ngOnInit() {
    this.typeClick('checklist', null);
  }

}

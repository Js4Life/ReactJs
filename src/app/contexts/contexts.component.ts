import { Component, OnInit, Output,Input, EventEmitter } from '@angular/core';
declare var $:any;
import * as _ from 'underscore';

@Component({
  selector: 'contexts',
  templateUrl: './contexts.component.html',
  styleUrls: ['./contexts.component.css']
})
export class ContextsComponent implements OnInit {

  @Input() lists:any;
  @Input() isOverlay:boolean;
  tempList:any;
  searchText:string=null;
  // selectAll:boolean=false;
  @Output() change: EventEmitter<any> = new EventEmitter<any>();

  constructor() { }

  select(opt){
    if(opt=='all'){
      this.lists.forEach( obj => {
        obj.isSelected=true;
      })
      this.change.emit('all');
    }
    else{
      this.lists.forEach( obj => {
        obj.isSelected=false;
      })
      this.change.emit('none');
    }
    // this.selectAll=!this.selectAll;
  }

  selectContext(obj){
    obj.isSelected=!obj.isSelected;
    this.change.emit(obj);
    // $(idx).removeClass('grey').addClass('active').siblings().removeClass('active').addClass('grey');
  }

  search(value){
    this.tempList = _.filter(this.lists, function(obj)
    { 
      if(obj.contextName.toLowerCase().indexOf(value) >= 0)
      return obj;
    });
  }

  clear(){
    this.searchText=null;
    this.lists=Object.assign([], this.tempList);
  }
  
  ngOnInit() {
    this.tempList=Object.assign([], this.lists);
    console.log(this.lists);
    $('#all').removeClass('grey').addClass('active').siblings().removeClass('active').addClass('grey');
  }

}

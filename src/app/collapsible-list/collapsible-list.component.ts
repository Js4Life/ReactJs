import { Component, OnInit,Input } from '@angular/core';

@Component({
  selector: 'collapsible-list',
  templateUrl: './collapsible-list.component.html',
  styleUrls: ['./collapsible-list.component.css']
})
export class CollapsibleListComponent implements OnInit {

  @Input() data:any;
  @Input() category:string;
  constructor() { }

  onClick(id){
    $('#'+id).siblings().removeClass('active').addClass('list');
    $('#'+id).removeClass('list').addClass('active');
  }
  onParentClick(id){
    $('#'+id).toggle('collapse');
  }

  ngOnInit() {
  }

}

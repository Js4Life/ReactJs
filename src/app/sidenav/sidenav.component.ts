import { Component, OnInit } from '@angular/core';
declare var $:any;

@Component({
  selector: 'sidenav',
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.css']
})
export class SidenavComponent implements OnInit {

  options:any=[
    {id:'regulations',name:'Regulations'},
    {id:'amexInternal',name:'Amexinternal'}
  ]

  constructor() { }

  // onClick(opt){
  //   $('#'+opt).parent().children().removeClass('active').addClass('list');
  //   $('#'+opt).removeClass('list').addClass('active');
  //   switch(opt){
  //     case "regulations":
  //       break;
  //     case "amexInternal":
  //       break;
  //   }
  // }

  ngOnInit() {
  }

}

import { Component, OnInit,Input,Output,EventEmitter  } from '@angular/core';

@Component({
  selector: 'list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.css']
})
export class ListComponent implements OnInit {

  @Input() data:any;
  // @Input() category:string;
  @Output() change: EventEmitter<any> = new EventEmitter<any>();

  selectedFile:any={};
  editName:boolean=false;
  folderName:string='';
  

  constructor() { }

  onClick(opt,e){
    $(e.currentTarget).addClass('active').siblings().removeClass('active');
    this.selectedFile=opt;
    console.log(this.selectedFile);
    this.change.emit(opt);
  }

  renameFolder(obj,e){
    // this.editName=true;
    // $(e.currentTarget).contentEditable=true;
    this.change.emit({parentFolderName:obj.parentFolderId, name:this.folderName, function:'rename'});
  }

  ngOnInit() {
  }

}

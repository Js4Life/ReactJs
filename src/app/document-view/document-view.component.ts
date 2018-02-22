import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'document-view',
  templateUrl: './document-view.component.html',
  styleUrls: ['./document-view.component.css']
})
export class DocumentViewComponent implements OnInit {

  @Input() documents:any; 
  selectedFileIdx:number=0;
  constructor() { }

  navigateToAddedFiles(opt){
    if(opt=='next' && this.selectedFileIdx!=this.documents.length-1){
      this.selectedFileIdx+=1;
    }
    if(opt=='prev' && this.selectedFileIdx>0){
      this.selectedFileIdx-=1;
    }
  }

  ngOnInit() {
  }

}

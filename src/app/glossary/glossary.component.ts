import { Component, OnInit,  Output,Input, EventEmitter} from '@angular/core';
import * as _ from 'underscore';
import { DocumentSvc } from '../services/document/document.service';
import { AppRouteConfig} from '../app.router-config';
import { GlossaryTermVM } from '../models/glossaryterm.model';
import { ClientState } from '../providers/clientstate.provider';
import { UtilsService } from '../services/utils.service';


@Component({
  selector: 'glossary',
  templateUrl: './glossary.component.html',
  styleUrls: ['./glossary.component.css']
})
export class GlossaryComponent implements OnInit {

  @Input() fileId:string;
  @Input() lists:GlossaryTermVM;
  @Output() change: EventEmitter<any> = new EventEmitter<any>();
  tempList:GlossaryTermVM;
  searchText:string=null;

  constructor(private documentSvc:DocumentSvc, private utilSvc:UtilsService, private arc:AppRouteConfig, private clientState:ClientState) { }


  search(value){
    this.tempList = _.filter(this.lists, function(obj)
    { 
      if(obj.name.toLowerCase().indexOf(value) >= 0)
      return obj;
    });
  }

  toVectorDetails(vocab,e,opt){
    // this.utilSvc.selectedVocab = vocab;
    if(vocab.isGlobalIdReady){
      if(opt=='details'){
        this.clientState.selectedVocab = vocab;
        this.arc.gotoVectorDetails(this.fileId,vocab);
      }
      else{    
        // $(e.currentTarget).parent().addClass('SelectedVectorColour');
        // this.change.emit({docId : this.fileId,vocab});
        this.utilSvc.selectedVocab = vocab;
        e.stopPropagation();
      }
    }
  }

  syncToDB(){
    this.documentSvc.getAllVocabsOfADoc(this.fileId).then(data=>{
      this.lists=data.glossaryList;
      this.tempList=Object.assign([], this.lists);
    })
  }

  clear(){
    this.searchText=null;
    this.tempList=Object.assign([], this.lists);
  }

  ngOnInit() {
    if(this.lists)
      this.tempList=Object.assign([], this.lists);
    console.log(this.lists);
  }

}

import { Component, OnInit } from '@angular/core';
import { AppRouteConfig} from '../app.router-config';
import { ActivatedRoute } from '@angular/router';
import { DocumentSvc } from '../services/document/document.service';
import { Constants , HttpVerbs} from '../models/constants.model';

import { DomSanitizer } from '@angular/platform-browser';
import { ClientState } from '../providers/clientstate.provider';

@Component({
  selector: 'app-vocab-details',
  templateUrl: './vocab-details.component.html',
  styleUrls: ['./vocab-details.component.css']
})
export class VocabDetailsComponent implements OnInit {

  currUser:any;
  sub:any;
  vocab:any;
  vocabSource:any;
  allVocabData:any=[];
  optionList:any = [
    {id:'qualitytracking', name:'Quality Tracking'},
    {id:'datalineage', name:'Data Lineage'},
    {id:'dataquality', name:'Data Quality'}
  ]

  listData:any=[
    {schema:'LOAN', database:'Connection@LOAN', table:'Bond Table', column:'AggregateLossTableID'},
    {schema:'LOAN', database:'Connection@LOAN', table:'Bond Table', column:'FinancialAssetType'},
    {schema:'LOAN', database:'Connection@LOAN', table:'AggregateLoss Table', column:'ProductId'},
    {schema:'LOAN', database:'Connection@LOAN', table:'AggregateLoss Table', column:'ProductId'},
    {schema:'LOAN', database:'Connection@LOAN', table:'AggregateLoss Table', column:'ProductId'},
    {schema:'LOAN', database:'Connection@LOAN', table:'AggregateLoss Table', column:'ProductId'},
    {schema:'LOAN', database:'Connection@LOAN', table:'AggregateLoss Table', column:'ProductId'},
    {schema:'LOAN', database:'Connection@LOAN', table:'AggregateLoss Table', column:'ProductId'},
    {schema:'LOAN', database:'Connection@LOAN', table:'AggregateLoss Table', column:'ProductId'},
    {schema:'LOAN', database:'Connection@LOAN', table:'AggregateLoss Table', column:'ProductId'},
    {schema:'LOAN', database:'Connection@LOAN', table:'AggregateLoss Table', column:'ProductId'},
    {schema:'LOAN', database:'Connection@LOAN', table:'AggregateLoss Table', column:'ProductId'},
    {schema:'LOAN', database:'Connection@LOAN', table:'AggregateLoss Table', column:'ProductId'},
  ]

  constructor(private arc:AppRouteConfig, private route: ActivatedRoute,private clientState:ClientState, private docSvc : DocumentSvc,private domsanitizer : DomSanitizer
    ) { }


  onOptClick(id,e){
    $(e.currentTarget).addClass('active').siblings().removeClass('active');
    switch(id){
      case 'datastorage':
        break;
      case 'datalineage':
        this.vocabSource=this.domsanitizer.bypassSecurityTrustResourceUrl(this.allVocabData.dataLineage);
        break;
      case 'qualitytracking':
        this.vocabSource=this.domsanitizer.bypassSecurityTrustResourceUrl(this.allVocabData.dataTracking);
        break;
      case 'dataquality':
        this.vocabSource=this.domsanitizer.bypassSecurityTrustResourceUrl(this.allVocabData.dataQuality);
        break;
    }
  }

  back(){
    console.log('inside back');
    this.arc.toHome();
  }

  signOut(){
    localStorage.clear();
    this.arc.gotoLogin();
  }

  getVocabDetails(docId, vid){
    this.docSvc.getVocabDetailsById(docId,vid).then(data=>{
      this.allVocabData=data;
      console.log(this.allVocabData);
      this.vocabSource=this.domsanitizer.bypassSecurityTrustResourceUrl(this.allVocabData.dataTracking);
    })
  }

  onInitialise(){
    this.currUser =  JSON.parse(localStorage.getItem(Constants.LOGGED_USER));
    this.vocab = this.clientState.selectedVocab;
    this.sub = this.route.params.subscribe(params => {
      var docId = params['docid'];
      var vocabId = params['vid'];
      this.getVocabDetails(docId, vocabId);
    });
  }

  ngOnInit() {
    this.onInitialise();
  }

}

import { Component, OnInit , Input, Output, EventEmitter} from '@angular/core';
import {UtilsService} from '../services/utils.service';
import { DomSanitizer } from '@angular/platform-browser';
import * as _ from 'underscore';
import { AnalysisService } from '../services/analysis/analysis.service';
import { FeedbackService } from '../services/feedback/feedback.service';
import { DocumentSvc } from '../services/document/document.service';
import { ParaFeedback } from '../models/paraFeedback.model';
import { Constants } from '../models/constants.model';
import { ClientState } from '../providers/clientstate.provider';

import { ParaNoteVM } from '../models/paranote.model';
declare var $:any;

@Component({
  selector: 'paragraph-view',
  templateUrl: './paragraph-view.component.html',
  styleUrls: ['./paragraph-view.component.css']
})
export class ParagraphViewComponent implements OnInit {

  @Input() docType:any;
  @Input() para:any;
  @Input() relPara:any;
  @Input() notes:Array<ParaNoteVM>;
  // @Input() checklistItemParaTags:any;
  @Output() change: EventEmitter<any> = new EventEmitter<any>();
  currentPara:any;
  textSelection:any;
  selId=0;

  paraNote:ParaNoteVM = new ParaNoteVM;
  addNotePad:boolean = false;
  showNotes:boolean = false;
  // tagged:boolean = false;
  isDetailsDivClicked:boolean=false;
  isRelatedTermsDivClicked:boolean=false;
  isAliasDivClicked:boolean=false;
  isFeedBackOn:boolean =false;
  feedbackContextsList:any;
  inaccuracyReasonsList:any = [];
  paraFeedbackList :any =[] ;
  paraFeedbackTypes = {
        correct :"Correct",
        moreImportant : "More Important",
        lessImportant : "Less Important",
        notPresent : "Not Present"
  }

  wordName:string='';
  // tagged:boolean = false;
  relatedTerms:any=[
    // {name:'Hedging'},
    // {name:'Risk Management'},
    // {name:'Smart Beta'},
    // {name:'Managed Futures'},
    // {name:'Diversity store'},
    // {name:'Holdings'},
    // {name:'Risk profile'},
    // {name:'Asset Allocation'}
  ];
  aliasedTerms:any=[
    // {name:'Credit risk RWA'},
    // {name:'Default risk'}
  ];

  constructor(private utilSvc:UtilsService, private sanitizer: DomSanitizer, private analysisSvc:AnalysisService, private feedbackSvc: FeedbackService, private documentSvc:DocumentSvc, private clientState:ClientState) {
    utilSvc.notifyParaViewFlag.subscribe(flag => this.changeNotifier(flag));
    utilSvc.paragraphExplorerFeedback.subscribe(flag => this.paragraphExplorerFeedback(flag));
  }

  paragraphExplorerFeedback(flag){
    this.isFeedBackOn = flag;
    if(!this.isFeedBackOn){
      if(this.paraFeedbackList.length>0){
        var paraFeedback={paraFeedback:this.paraFeedbackList}
        this.feedbackSvc.saveParaFeedback(this.para.id,paraFeedback).then(data=>{
          console.log(data);
          this.paraFeedbackList = [];
        })
      }
    }
    if(this.para.contextDistribution.length>0){

      this.para.contextDistribution = _.map(this.para.contextDistribution, function(key) { return _.omit(key, 'inaccuracyReason', 'isAccurate'); }); // previous feedback is removed
      this.feedbackContextsList = Object.assign([], this.para.contextDistribution);
    }
  }

  feedbackCheck(li,opt,idx,e){
    var paraFeedback = new ParaFeedback();
    paraFeedback.context = li.contextURI;
    paraFeedback.feedback = this.paraFeedbackTypes.correct.replace(/\s/g, "_").toUpperCase();;
    if(opt=='yes'){
      if(li.isAccurate!='yes'){
        li.isAccurate=opt;
        this.paraFeedbackList  = _.without(this.paraFeedbackList, _.findWhere(this.paraFeedbackList, {context: paraFeedback.context})); //ensure no multiple feedback for same context..
        this.paraFeedbackList.push(paraFeedback);
        $('#'+idx+'Pane').siblings().find('.inaccurateReason').removeClass('turnVisible');
        $('#'+idx+'Pane').find('.reasonBtn').siblings().removeClass('activeReason');
        li.inaccuracyReason=null;
      }
      else{
        li.isAccurate = null;
        this.paraFeedbackList  = _.without(this.paraFeedbackList, _.findWhere(this.paraFeedbackList, {context: paraFeedback.context}));
      }
    }
    if(opt=='no'){
      this.feedbackSvc.getFeedbackInaccuracyReasonsList().then(data=>{
        if(data){
          this.inaccuracyReasonsList = _.without(data, "Correct");
          // li.isAccurate=opt;
        }
      })
      $('#'+idx+'Pane').siblings().find('.inaccurateReason').removeClass('turnVisible');
      $('#'+idx+'Pane').find('.inaccurateReason').addClass('turnVisible');
    }
    e.stopPropagation();
  }
  reasonClick(value,e,li,idx){
    var paraFeedback = new ParaFeedback();
    paraFeedback.context = li.contextURI;
    paraFeedback.feedback = value.replace(/\s/g, "_").toUpperCase();
    if(li.inaccuracyReason != value){
      $(e.currentTarget).addClass('activeReason').siblings().removeClass('activeReason');
      li.isAccurate='no';
      li.inaccuracyReason = value;

      this.paraFeedbackList  = _.without(this.paraFeedbackList, _.findWhere(this.paraFeedbackList, {context: paraFeedback.context}));//ensure no multiple feedback for same context..
      this.paraFeedbackList.push(paraFeedback);
      $('#'+idx+'Pane').find('.inaccurateReason').removeClass('turnVisible');
    }
    else{
      li.isAccurate= null;
      this.paraFeedbackList  = _.without(this.paraFeedbackList, _.findWhere(this.paraFeedbackList, {context: paraFeedback.context}));
      $(e.currentTarget).removeClass('activeReason');
      $('#'+idx+'Pane').find('.inaccurateReason').removeClass('turnVisible');
    }
    //e.stopPropagation();
  }
  closeFeedback(idx, e){
    $('#'+idx+'Pane').find('.inaccurateReason').removeClass('turnVisible');
    e.stopPropagation();
  }
  stopEventPropagation(e){
    e.stopPropagation();
  }

  
  termFeedbackCheck(wordName,opt,idx,e){
    
  }

  onClickOutside(e){
    var container = $("#showNoteId");
      if (!container.is(e.target) && container.has(e.target).length === 0) 
      {
          this.showNotes = false;
      }
  }

  changeNotifier(flag){
    if(flag){
      this.hideDetailsDiv();
      this.showNotes = false;
      this.addNotePad = false;
    }
  }

  onContextClick(ctxt){
    if(ctxt.isSelected)
      this.clientState.selectedContextColor=ctxt.color;
      this.change.emit({type:'context', data:ctxt});
  }

  addNotePadClick(){
    this.addNotePad = true;
    this.showNotes=false;
  }
  showNotesClick(e){
    this.showNotes = true;
    this.addNotePad = false;
    e.stopPropagation();
  }
  addNote(){
    this.addNotePad = false;
    this.change.emit({type:'note', data:this.paraNote});
  }

  getClickedWord(e) {
    console.log(this.para);
    console.log(this.relPara);
    var s = window.getSelection();
    var range = s.getRangeAt(0);
    var node = s.anchorNode;
    if(node.parentNode.localName=='span'){
      this.wordName=node.nodeValue;
      var name = node.nodeValue.toLowerCase()
      var term={"term":name};
      this.documentSvc.getDetailsOfVocab(term).then(data=>{
        console.log('data');
        console.log(data);
        this.relatedTerms=data.oneHopAdjacency;
        this.aliasedTerms=data.aliases;
            
      })
      this.showDetailsDiv();
      // alert(e.pageX + ' , ' + e.pageY);
      this.setPosition(e);
    }
  }​

  setPosition(e){
    var left = e.offsetX-190, top = e.offsetY, height = 550, bottom;
    var winHeight = $(window).height();
    if(top<270){
      height = height - top;
      top = top+115;
      $('.detailsDiv').css({'top':top, 'left':left, 'max-height':height, 'bottom':''});
    }
    else{
      height = height + 110;
      bottom = winHeight - e.y;
      $('.detailsDiv').css({'top':'', 'left':left, 'max-height':height, 'bottom':bottom});
    }
  }

  getRandomClickedWord(e) {
    var s = window.getSelection();
    var range = s.getRangeAt(0);
    var node = s.anchorNode;
    var nLen = node.nodeValue.length;

    while(range.toString().indexOf(' ') != 0) {                 
      range.setStart(node,(range.startOffset -1));
    }
    range.setStart(node, range.startOffset +1);

    do{
      range.setEnd(node,range.endOffset + 1);
    }while(range.toString().indexOf(' ') == -1 && range.toString().trim() != ''  && range.endOffset < nLen);
    range.setEnd(node,range.endOffset - 1);

    var str = range.toString();
    if(str.match(/[.,:!?]$/))  //matches any punctuation at the end of string
      range.setEnd(node,range.endOffset - 1); //reduce selection by 1

    if(str.match(/^[.,:!?]/))  //matches any punctuation at the beginning of string
      range.setStart(node,range.startOffset + 1); //reduce selection by 1

    var str = range.toString().trim().replace(/\b[-.,()&$#!\[\]{}"']+\B|\B[-.,()&$#!\[\]{}"']+\b/g, "");
    alert(str);
  }​

  showDetailsDiv(){
    this.isDetailsDivClicked=true;
    this.isRelatedTermsDivClicked=false;
    this.isAliasDivClicked=false;
  }
  hideDetailsDiv(){
    this.isRelatedTermsDivClicked=false;
    this.isDetailsDivClicked=false;
    this.isAliasDivClicked=false;
  }
  showRelatedDivs(term){
    if(term=='Relatedterms'){
      this.isDetailsDivClicked=false;
      this.isRelatedTermsDivClicked=true;
    }
    if(term=='aliases'){
      this.isAliasDivClicked=true;
      this.isDetailsDivClicked=false;
    }
  }

  // getSelectedText(){
  //   if (window.getSelection) {
  //       this.appendAnchor(this.selectText());
  //   } 
  // }
  // appendAnchor(sel){
  //   if (!sel){
  //     return;
  //   } 
  //   var extracted = sel.extractContents();
  //   var el = document.createElement('span');
  //   el.setAttribute("id", "a-"+this.selId);
  //   el.setAttribute("class", "highlighted");
  //   el.appendChild(extracted.textContent);
  //   sel.insertNode(el);
  // }
  // selectText() {	// onmouseup
  //   if (window.getSelection) {
  //     this.textSelection = window.getSelection();
  //     if (this.textSelection.getRangeAt && this.textSelection.rangeCount) {	// Chrome, FF
  //       return this.textSelection.getRangeAt(0);
  //     }
  //   }
  //   return null;
  // }
  // addNote() {
  //   var target = "a-"+this.selId;
  //   document.getElementById(target).classList.remove("highlighted");
  //   this.selId++;
  // }

  ngOnInit() {
    this.changeNotifier(true);
  }

}

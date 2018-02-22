import { Component, OnInit, ViewContainerRef} from '@angular/core';
import { ToastsManager } from 'ng2-toastr/ng2-toastr';
import { AppRouteConfig} from '../app.router-config';
import { ActivatedRoute } from '@angular/router';
import { UtilsService } from '../services/utils.service';
import { AnalysisService } from '../services/analysis/analysis.service';
import { DocumentSvc } from '../services/document/document.service';
import { ChecklistSvc } from '../services/checklist/checklist.service';
import { DomSanitizer } from '@angular/platform-browser';
import { Constants , HttpVerbs} from '../models/constants.model';


import { ClientState } from '../providers/clientstate.provider';
import { AnalysisRequestVM } from '../models/analysisrequest.model';
import { AnalysisUpdateRequestVM } from '../models/analysisUpdateRequestVM.model';
import { ParaNoteVM } from '../models/paranote.model';



import * as _ from 'underscore';
declare var $:any;

@Component({
  selector: 'paragraph-explorer',
  templateUrl: './paragraph-explorer.component.html',
  styleUrls: ['./paragraph-explorer.component.css']
})
export class ParagraphExplorerComponent implements OnInit {

  showOnHover:boolean=false;
  open:boolean=false;
  startChecklist:boolean = false;
  paraView:boolean=true;
  stateSaved:boolean=false;
  showHoverContext:boolean=false;
  showfilesDroplist:boolean=false;
  annotations:boolean=true;
  tag:boolean = false;
  allParaShowBtn:boolean = false;
  isFeedBackOn:boolean = false;
  // descPanel:boolean = false;

  // selections:any={};
  sub:any;
  notes:ParaNoteVM;
  allContextsByDoc:any;
  selectedContexts:any;
  checklistItemId:any;
  files:any;
  filesForAnalysis:any=[];
  unselectedContextsLength:number;
  activeContextLength:number=0;
  firstSelectedFile:any;
  previouslyselectedContexts:any;
  savedAnalysisMsg:string;
  discardedAnalysisMsg:string;
  selectedParadetails:any;
  tmp_selectedParadetails:any;
  selectedRelatedPara:any=null;
  paragraphArray:any=[];
  taggedParagraphList:any=[];
  paragraphTempArray:any = [];
  selectedParaContext:any;
  analysisModel:AnalysisRequestVM = new AnalysisRequestVM();
  analysisModel2:AnalysisRequestVM = new AnalysisRequestVM();
  analysisUpdateModel:AnalysisUpdateRequestVM = new AnalysisUpdateRequestVM();
  tempAnalysisId:any;
  finalSelections:any={};
  linkedCheckListId : string = null;
  paraList:any=[];
  currParaSearchStrLen: number =0;
  currUser:any;
  showDiscardOrSaveMsg:string;
  isPrimaryFileClicked:boolean=true;
  showPrimaryFile:string='';
  relatedPara:any=[];
  relatedParaWithPrimaryFile:any=[];
  relatedParaWithoutPrimaryFile:any=[];
//<<<<<<< HEAD
  wordsList=[{'word':'General','color':'green'},{'word':'Concepts','color':'red'},{'word':'Paragraph','color':'blue'}];
  loading : boolean = false;
  
//=======
  
//>>>>>>> c8a8296b916971c558eeec0ed81b2a3d14ccf54c

  constructor(private arc:AppRouteConfig, private route: ActivatedRoute,private clientState:ClientState, private utilSvc:UtilsService,
    private analysisSvc:AnalysisService, private docSvc : DocumentSvc,private checklistSvc:ChecklistSvc, private sanitizer: DomSanitizer, public toastr: ToastsManager, vcr: ViewContainerRef) {
      this.toastr.setRootViewContainerRef(vcr);
     }

  back(){
    console.log('inside back');
    this.arc.toHome();
  }
  onTabClick(tab){
    $('#'+tab).removeClass('inactiveTab').addClass('activeTab').siblings().removeClass('activeTab').addClass('inactiveTab');
    switch(tab){
      case "paragraph":
        this.paraView=true;
        break;
      case "document":
        this.paraView=false;
        break;
      }
  }
  onBtnClick(val){
    switch(val){
      case "annotations":
        this.annotations=!this.annotations;
        if(this.annotations){
          this.highlightContextWordsinPara(null);
        }
        else{
          this.tmp_selectedParadetails = Object.assign({},this.selectedParadetails);
        }
        break;
      case "openChecklist":
        this.paraView=false;
        if( this.linkedCheckListId != undefined)
          this.arc.gotoChecklistDetails( this.linkedCheckListId);
        break;
      }
  }
  leave(){
    this.showOnHover=false;
  }
  showBtns(){
    this.showOnHover=true;
  }
  newChecklist(){
    if(this.tempAnalysisId){
      // this.showDiscardOrSaveMsg="Save or Discard Analysis to start checklist";
      // setTimeout(()=>{ this.showDiscardOrSaveMsg = "" }, 4000); 
      this.toastr.error('Save or Discard Analysis to start checklist');
    }
    else{
      this.startChecklist = true;
      $('#checklistId').addClass('full').removeClass('close');
    }
  }

  rightbtnClick(opt){
    if(opt=='minimise'){
      $('#checklistId').addClass('close').removeClass('full');
      this.open=true;
    }
    if(opt=='full'){
      $('#checklistId').addClass('full').removeClass('close');
      this.open=false;
    }
    if(opt=='close'){
      $('#checklistId').addClass('close').removeClass('full');
      this.open=false;
      this.startChecklist = false;
    }
  }

  highlightContextWordsinPara(context){
    if(!context){
      context = this.selectedParaContext;
    }
    if(this.annotations){
      this.docSvc.getContributingContextWords(this.selectedParadetails.id).then( data => {
        var reqObj=_.findWhere(data,{contextUri : context.contextURI});
        this.wordsList=[];
        var ctxObj = this.clientState.contextsColors[context.contextURI];
        reqObj.words.forEach(word=>{
          this.wordsList.push({'word':word, 'color':this.clientState.selectedContextColor});
        })
        this.tmp_selectedParadetails = Object.assign({},this.selectedParadetails);
        var paraText = this.utilSvc.addHighlightingInfo(this.tmp_selectedParadetails.paraContent,this.wordsList);
        this.tmp_selectedParadetails.paraContent=this.sanitizer.bypassSecurityTrustHtml(paraText);
      });
    }
  }

  hoverOnPara(e){
    // console.log(e);
    var h = $(e.currentTarget).offset().top - $(e.currentTarget).offsetParent().offset().top - 90;
    $('.floatingPara').css('top',h);
  }

  clickOnPara(para,e){ 
      if(e){
        $(e.currentTarget).addClass('activePara').removeClass('inactivePara').siblings().removeClass('activePara').addClass('inactivePara');
        $(e.currentTarget).find('.progressPane').removeClass('hide').parent().siblings().find('.progressPane').addClass('hide');
      }
      else{
        var id = "para"+para.id.replace(':','');
        $('#'+id).addClass('activePara').removeClass('inactivePara').siblings().removeClass('activePara').addClass('inactivePara');
        $('#'+id).find('.progressPane').removeClass('hide').parent().siblings().find('.progressPane').addClass('hide');
      }
      para.relatedParaOpen =false;
      this.selectedRelatedPara=null;
      para.showHide = "SHOW";
      this.selectedParadetails  = para;
      this.tmp_selectedParadetails = Object.assign({},this.selectedParadetails);
      // this.highlightContextWordsinPara();
      this.onTabClick('paragraph');
    if(!para.relatedParagraphs){
      // this.loading=true;
      this.analysisSvc.getRelatedParasInAnAnalysis(this.clientState.lastAnalysisId,para.id).then(data=>{
        console.log(data);
        this.relatedParaWithPrimaryFile = data;
        this.showPrimaryFile='exclude';
        this.relatedParaWithPrimaryFile.forEach(obj=>{
          obj.contextDistribution = this.utilSvc.updateWeightAndColor(obj.contextDistribution);
            if(obj.documentName!=this.files[0].name){
              this.relatedParaWithoutPrimaryFile.push(obj);
            }
          });
        
          this.relatedParaWithoutPrimaryFile.forEach(obj=>{
          obj.contextDistribution = this.utilSvc.updateWeightAndColor(obj.contextDistribution);
        });
        para.relatedParagraphs = this.relatedParaWithPrimaryFile;
      })
    }

    this.utilSvc.notifyParaView();
    this.getParaNotes();
  }
  getParaNotes(){
    this.analysisSvc.getParaNote(this.clientState.lastAnalysisId,this.selectedParadetails.id).then(data=>{
      console.log(data);
      this.notes = data;
    })
  }

  showRelatedParas(para,e){
    $(e.currentTarget).offsetParent().children().find('.relatedParaGroupPanel').collapse("toggle");
    para.relatedParaOpen=!para.relatedParaOpen;
    if(para.showHide == "SHOW")
      para.showHide = "HIDE";
    else{
      para.showHide = "SHOW";
      this.selectedRelatedPara=null;
    }
    e.stopPropagation();
  }

  onPrimaryFileClick(para,e){
    this.isPrimaryFileClicked=!this.isPrimaryFileClicked;
    if(this.isPrimaryFileClicked){
      this.showPrimaryFile='exclude';
      para.relatedParagraphs = this.relatedParaWithPrimaryFile;
    }
    else{
      this.showPrimaryFile='include';
      para.relatedParagraphs = this.relatedParaWithoutPrimaryFile;
    }
    e.stopPropagation();
  }

  clickOnRelatedPara(relPara,e){
    $(e.currentTarget).addClass('activePara').removeClass('inactivePara').siblings().removeClass('activePara').addClass('inactivePara');
    this.selectedRelatedPara=relPara;
    this.onTabClick('paragraph');
    e.stopPropagation();
  }

  deleteReference(context,idx){
    this.selectedContexts.splice(idx,1);
    var obj=_.findWhere(this.allContextsByDoc,{ contextId:context.contextId });
    obj.isSelected=false;
    this.getRemainingContextDetails();
    console.log('inside deleteReference : ');
    console.log(this.selectedContexts);
    this.createNewAnalysisWithChangedContexts();
  }

  changeAddedContext(context){
    this.allContextsByDoc.forEach(obj => {
      if(obj.id == context.id){
        obj.isSelected=context.isSelected;
      }
    });
    this.getRemainingContextDetails();
  }

  paraContextClick(emittedData){
    if(emittedData.type == 'context'){
      this.selectedParaContext=emittedData.data;
      this.highlightContextWordsinPara(emittedData.data);
    }
    if(emittedData.type == 'note'){
      console.log(emittedData.data);
      this.analysisSvc.addParaNote(this.clientState.lastAnalysisId,this.selectedParadetails.id,emittedData.data).then(data=>{
        console.log(data);
        this.getParaNotes();
      })
    }
  }


  showAllParas(){
    this.loading = true;
    this.allParaShowBtn = false;
    this.paragraphTempArray = this.paragraphArray;
    this.loading = false;
  }
  showTaggedParas(){
    var arr = _.where(this.paragraphArray, {isTagged:true});
    this.paragraphTempArray = arr;
    this.allParaShowBtn = true;
    setTimeout(()=>{ 
        if(this.paragraphTempArray.length>0)
          this.clickOnPara(this.paragraphTempArray[0], event);
    }, 1000);
  }
  tagParagraph(){
    this.checklistSvc.tagParagraph(this.checklistItemId, this.tmp_selectedParadetails.id).then(data=>{
      if(data){
        this.selectedParadetails.isTagged = true;
        this.getAnalysisById(this.clientState.lastAnalysisId);
      }
    })
  }
  removeTag(){
    this.checklistSvc.untagParagraph(this.checklistItemId, this.tmp_selectedParadetails.id).then(data=>{
      if(data){
        this.selectedParadetails.isTagged = false;
      }
    })
  }

  contextChange(event){
    if(event.contextName){
      if(event.isSelected) {  //since the boolean status is rendered true if false
        if(_.findWhere(this.selectedContexts,{ id:event.id })){

        }
        else{
          this.selectedContexts.push(event); 
        }
      } 
      if(!event.isSelected)  {
        this.selectedContexts= _.reject(this.selectedContexts, function(obj){
          if(obj.contextName==event.contextName){
            return obj;
          }
        });
      } 
    }
    if(event=='all'){
      this.selectedContexts=this.allContextsByDoc;
    }
    if(event=='none'){
      this.selectedContexts=[];
    }
    this.getRemainingContextDetails();
  }

  collapse(){
    $('#droplist').collapse("toggle");
  }
  
  cancelContextChanges()
  {
    this.showHoverContext=false;
  }
  onFinishAdding(){
    this.showHoverContext=false;
    // console.log('selected contexts length: ' + this.selectedContexts.length);
    // var c=0;
    // var count=0;
    // this.selectedContexts.forEach(obj => {
    //   this.clientState.selectedDocumentsWithContexts.selectedContexts.forEach(obj1 =>{
    //       if(obj.name===obj1.name){
    //           c+=1;
    //       }
    //   });
      
    // });

    // if(this.selectedContexts.length !== this.clientState.selectedDocumentsWithContexts.selectedContexts.length){
    //   count+=1;   
    // }
    // else if(c!=this.selectedContexts.length){
    //   count+=1;  
    // }
    // if(count>0){
      
    // }
    this.createNewAnalysisWithChangedContexts();
  }
    
  createNewAnalysisWithChangedContexts()
      {
        if(this.tempAnalysisId){
          this.analysisSvc.deleteAnalysisById(this.tempAnalysisId).then(data=>{
            console.log(data); 
            this.tempAnalysisId='';       
          })  
        }
        
        this.finalSelections= { filesForAnalysis: this.clientState.selectedFilesForAnalysis, selectedContexts : this.selectedContexts, allContextsByDoc : this.allContextsByDoc };
        this.clientState.selectedDocumentsWithContexts=this.finalSelections;
        var documentRids=[];
        var contextRids=[];
        this.files.forEach(obj=>{
          documentRids.push(obj.id);
        })
        this.selectedContexts.forEach(obj=>{
          contextRids.push(obj.contextId);
        })
        this.analysisModel.docRids = documentRids;
        this.analysisModel.contextRids = contextRids;
        this.analysisModel.name = this.files[0].name;
        console.log(this.analysisModel);
        this.analysisSvc.createNewAnalysis(this.analysisModel).then(data=>{
          // this.clientState.lastAnalysisId=this.tempAnalysisId=data.id;
          this.tempAnalysisId=data.id;
          this.filesForAnalysis=[];
          this.getAnalysisById(this.tempAnalysisId);
        })
      }
  

  getRemainingContextDetails(){
    this.activeContextLength=0;
    if(this.selectedContexts)
      this.unselectedContextsLength=this.allContextsByDoc.length - this.selectedContexts.length;
    else
      this.unselectedContextsLength=this.allContextsByDoc.length;
    this.allContextsByDoc.forEach(obj => {
      if(obj.isSelected){
        this.activeContextLength++;
      }
    });
  }

  changeFileSelection(idx, file){
    if(this.filesForAnalysis.length==0){
      this.files[idx].isSelected=true;
      this.filesForAnalysis.push(this.files[idx]);
    }
    else if(file){
      file.isSelected=!file.isSelected;
      if(file.isSelected && !(_.findWhere(this.filesForAnalysis,{ id:file.id }))){
        this.filesForAnalysis.push(file);
      }
      if(!file.isSelected){
        this.filesForAnalysis=_.reject(this.filesForAnalysis, function(obj){
          if(obj.id==file.id){
            return obj;
          }
        });
      }
    }
    console.log(this.filesForAnalysis);
    this.extractParagraphs();
  }

  extractParagraphs(){
    this.firstSelectedFile=this.filesForAnalysis[0];
    this.paragraphArray = this.firstSelectedFile.paragraphs;
    this.paragraphArray.forEach(para=>{
        para.contextDistribution = this.utilSvc.updateWeightAndColor(para.contextDistribution);
        para.contextDistribution = _.reject(para.contextDistribution,function(obj){
          if(!obj.name)
            return obj;
        })
        this.allContextsByDoc.forEach(obj=>{
            var childObj=_.findWhere(para.contextDistribution,{ contextName : obj.contextName });
            if(childObj){
                childObj.isSelected=obj.isSelected;
            }
        })
        if(this.clientState.CURRENT_STATE.includes('checklist-details')){
          if(_.findWhere(this.taggedParagraphList,{id:para.id}))
            para.isTagged = true;
          else
            para.isTagged = false;
        }
    })
    Object.assign(this.paragraphTempArray,this.paragraphArray);
    console.log(this.paragraphTempArray);
    if(this.clientState.CURRENT_STATE.includes('checklist-details')){
      this.showTaggedParas();
    }
  }

  searchFocusIn(){
    $('.searchBtn').addClass('searchFocus').removeClass('searchBtn');
  }
  searchFocusOut(){
    $('.searchFocus').addClass('searchBtn').removeClass('searchFocus');
  }

  search(searchStr, resultArray){
    
    if(searchStr.length>2){
      if(this.currParaSearchStrLen>=searchStr.length)
        resultArray = this.paragraphArray;
      this.paragraphTempArray = _.filter(resultArray, function(obj)
      {
        if(obj.paraContent.toLowerCase().indexOf(searchStr) >= 0){
          return obj;
        }
      });
      // this.paragraphTempArray.forEach(obj=>{
      //   var innerHTML = "<span style='color:#21294d'>" + searchStr + "</span>";
      //   obj.paraContent = obj.paraContent.replace(new RegExp(searchStr, 'ig'), innerHTML);
      // })
    }
    else{
      this.paragraphTempArray = this.paragraphArray
    }
    this.currParaSearchStrLen = searchStr.length;
  }

  saveUpdatedAnalysis(){    
    if(this.tempAnalysisId && this.tempAnalysisId!=this.clientState.lastAnalysisId){
      this.loading = true;
      var c=0;
      var tobeDeletedContextRids=[];
      var tobeAddedContextRids=[];
      this.analysisSvc.getAnalysisById(this.clientState.lastAnalysisId).then(data=>{
        this.previouslyselectedContexts = data.contributingContexts;
        console.log('previously selected contexts ');
        console.log(this.previouslyselectedContexts);

        this.previouslyselectedContexts.forEach(obj => {
          var id1=obj.id.replace(new RegExp('#', 'g'), '');
          this.selectedContexts.forEach(obj1 =>{  
            if(id1==obj1.id){
                c+=1;  
              }
          })
          if(c==0){
            tobeDeletedContextRids.push(id1); 
          }
          c=0;
        });
        console.log('to be deleted contexts ');
        console.log(tobeDeletedContextRids);
        this.selectedContexts.forEach(obj => {
          this.previouslyselectedContexts.forEach(obj1 =>{
            var id1=obj1.id.replace(new RegExp('#', 'g'), '');
                if(obj.id==id1){
                  c+=1;  
                }
            })
            if(c==0){
              tobeAddedContextRids.push(obj.id); 
            }
            c=0;
          });
      console.log('to be Added contexts ');
      console.log(tobeAddedContextRids);
      var documentRids=[];
      this.files.forEach(obj=>{
          documentRids.push(obj.id);
      })
      // this.analysisModel2.docRids =  documentRids;
      this.analysisModel.docRids = this.analysisModel2.docRids = [];
      this.analysisModel.contextRids=tobeDeletedContextRids;
      this.analysisModel2.contextRids=tobeAddedContextRids;
      // this.analysisModel2.name = this.files[0].name;
      this.analysisModel.name = this.analysisModel2.name = '';
      this.analysisUpdateModel.analysisToDelete=this.analysisModel;
      this.analysisUpdateModel.analysisToUpdate=this.analysisModel2;
      console.log(this.analysisUpdateModel);
  
      this.analysisSvc.updateAnalysis(this.clientState.lastAnalysisId,this.analysisUpdateModel).then(data=>{
          this.clientState.lastAnalysisId=data.id;
          this.filesForAnalysis=[];
          this.loading = false;
          this.getAnalysisById(this.clientState.lastAnalysisId);
          // this.savedAnalysisMsg="Analysis updated";  
          // setTimeout(()=>{ this.savedAnalysisMsg = "" }, 4000); 
          this.toastr.success('Analysis updated');
        })
      this.analysisSvc.deleteAnalysisById(this.tempAnalysisId).then(data=>{
        this.tempAnalysisId='';       
        })
    })      
    
    }
    
  }

  discardUpdatedAnalysis(){
    this.analysisSvc.deleteAnalysisById(this.tempAnalysisId).then(data=>{ 
      this.tempAnalysisId='';
      this.filesForAnalysis=[];
      this.getAnalysisById(this.clientState.lastAnalysisId);
      // this.discardedAnalysisMsg="Analysis discarded";
      // setTimeout(()=>{ this.discardedAnalysisMsg = "" }, 4000);
      this.toastr.success('Analysis discarded');
    })
  }

  getStructuredData(data){
      this.linkedCheckListId = data.checkListId;
      this.files = data.contributingDocuments;
      this.selectedContexts = data.contributingContexts;
      this.allContextsByDoc = data.contributingDocuments[0].contextDistribution;
      this.allContextsByDoc = this.utilSvc.updateWeightAndColor(this.allContextsByDoc);
      this.selectedContexts = this.utilSvc.updateWeightAndColor(this.selectedContexts);
      this.selectedContexts.forEach(obj=>{
        // var ctxtName=obj.name.replace(new RegExp(' ', 'g'), '');
        var parentObj=_.findWhere(this.allContextsByDoc,{contextURI:obj.contextURI});
        if(parentObj){
          obj.value=parentObj.value;
          obj.isSelected=parentObj.isSelected = true;
          obj.color=parentObj.color;
        }
      })

      this.sortContexts();

      this.files.forEach(obj => {
        obj.isSelected=false;
      });
      
      this.changeFileSelection(0,null);
      this.getRemainingContextDetails();
      this.loading = false;
  }

  sortContexts(){
    let swapped, i, j;
    for (i = 0; i < this.selectedContexts.length-1; i++)
    {
      swapped = false;
      for (j = 0; j < this.selectedContexts.length-i-1; j++)
      {
          var first = parseInt(this.selectedContexts[j].value.replace('%',''));
          var second = parseInt(this.selectedContexts[j+1].value.replace('%',''));
          if (first < second)
          {
            var s = this.selectedContexts[j];
            this.selectedContexts[j] = this.selectedContexts[j+1];
            this.selectedContexts[j+1] = s;
            swapped = true;
          }
      }
      if (swapped == false)
      {};
    }
  }

  signOut(){
    localStorage.clear();
    this.arc.gotoLogin();
  }

  getAnalysisById(analysisId){
    this.loading = true;
    this.selectedParadetails=null;
    if(this.clientState.CURRENT_STATE.includes('checklist-details')){
      this.tag = true;
      this.analysisSvc.getAnalysisBychecklistItemId(analysisId,this.checklistItemId).then(data=>{
        // this.taggedParagraphList = data.taggedParagraphList;
        this.taggedParagraphList = _.uniq(data.taggedParagraphList, 'id');
        this.getStructuredData(data);
      });
    }
    else{
      this.analysisSvc.getAnalysisById(analysisId).then(data=>{
        this.getStructuredData(data);
      });
    }
    
  }

    feedbackClick(){
      this.isFeedBackOn = !this.isFeedBackOn;
      this.utilSvc.notifyparagraphExplorerFeedback(this.isFeedBackOn);
    
  }
  
  onInitialise(){
    this.currUser =  JSON.parse(localStorage.getItem(Constants.LOGGED_USER));
    this.sub = this.route.params.subscribe(params => {
      this.clientState.lastAnalysisId = params['id'];
      this.checklistItemId = params['ciId'];
      this.getAnalysisById(this.clientState.lastAnalysisId);
    });
  }

  ngOnInit() {
    this.onInitialise();
  }

}

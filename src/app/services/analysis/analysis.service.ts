import { Injectable }     from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import {RegulationVM} from "../../models/regulation.model";
import {ContextVM} from "../../models/context.model";
import {FolderVM} from "../../models/folder.model";
import {DocumentVM} from "../../models/document.model";
import {AnalysisRequestVM} from "../../models/analysisrequest.model";
import {AnalysisResponseVM} from "../../models/analysisresponse.model";
import {ParagraphVM} from "../../models/paragraph.model";
import {HttpVerbs} from "../../models/constants.model";
import {BaseHttpService} from "../../services/base-http.service";
import { ClientState} from '../../providers/clientstate.provider';
import { DocumentUploadRquestVM} from '../../models/documentuploadrequest.model';
import { NewChecklistRequestVM} from '../../models/newchecklistrequest.model';
import { ParaNoteVM } from '../../models/paranote.model';
import { AnalysisUpdateRequestVM } from '../../models/analysisUpdateRequestVM.model';

declare var stringformat : any;

@Injectable()
export class AnalysisService extends BaseHttpService {
    myURL : string = this.baseUrl + 'analysis';

    constructor( appState: ClientState, http: Http ){
        super(appState, http);
    }

        //Creates a New Analysis with a set of selected documents, contexts
    createNewAnalysis(analysisReqVM: AnalysisRequestVM) :Promise<any>{
        
        return this
                .invokeService(this.myURL+AnalysisURLS.CREATE_ANALYSIS.url,analysisReqVM,HttpVerbs.POST)
                .then(data => data.data[AnalysisURLS.CREATE_ANALYSIS.propname]);
    }
    getAllAnalysisForCCO():Promise<any>{
      return this
                .invokeService(this.myURL+AnalysisURLS.GET_CCO_ANALYSIS.url,"",HttpVerbs.GET)
                .then(data => data.data[AnalysisURLS.GET_CCO_ANALYSIS.propname]);
    }
    getAllAnalysis():Promise<any>{
      return this
                .invokeService(this.myURL+AnalysisURLS.GET_ALL_ANALYSIS.url,"",HttpVerbs.GET)
                .then(data => data.data[AnalysisURLS.GET_ALL_ANALYSIS.propname]);
    }
        //Updates the Analysis by updating document and context set
    updateAnalysis(analysisId:string, analysisReqVM: AnalysisUpdateRequestVM) :Promise<any>{
      var remUrl = stringformat(AnalysisURLS.UPDATE_ANALYSIS.url,this.filterIdString(analysisId));
        return this
                .invokeService(this.myURL+remUrl,analysisReqVM,HttpVerbs.PUT)
                // .then(data => data.data);
                .then(data => data.data[AnalysisURLS.UPDATE_ANALYSIS.propname]);
    }
        //Retrieves a previously saved analysis by analysisId
    getAnalysisById(analysisId:string) :Promise<AnalysisResponseVM>{
      var remUrl = stringformat(AnalysisURLS.GET_ANALYSIS_BY_ID.url,this.filterIdString(analysisId));
        return this
                .invokeService(this.myURL+remUrl,null,HttpVerbs.GET)
                .then(data => data.data[AnalysisURLS.GET_ANALYSIS_BY_ID.propname]);
    }
    getAnalysisBychecklistItemId(analysisId:string, checklistItemId:string) :Promise<AnalysisResponseVM>{
      var remUrl = stringformat(AnalysisURLS.GET_ANALYSIS_BY_CKID.url,this.filterIdString(analysisId), this.filterIdString(checklistItemId));
        return this
                .invokeService(this.myURL+remUrl,null,HttpVerbs.GET)
                .then(data => data.data[AnalysisURLS.GET_ANALYSIS_BY_CKID.propname]);
    }
        //Deletes an analysis
    deleteAnalysisById(analysisId:string) :Promise<any>{
      var remUrl = stringformat(AnalysisURLS.DELETE_ANALYSIS_BY_ID.url,this.filterIdString(analysisId));
        return this
                .invokeService(this.myURL+remUrl,null,HttpVerbs.DELETE)
                // .then(data => data.data[AnalysisURLS.DELETE_ANALYSIS_BY_ID.propname]);
                .then(data => data.data);
    }
        //Bookmarks An Analysis
    bookmarkAnAnalysis(analysisId:string) : Promise<any>{
      var remUrl = stringformat(AnalysisURLS.BOOKMARK_AN_ANALYSIS.url,this.filterIdString(analysisId));
      return this
              .invokeService(this.myURL+remUrl,null,HttpVerbs.POST)
              .then(data => data.data[AnalysisURLS.BOOKMARK_AN_ANALYSIS.propname]);
    }
        //Remove bookmark of an analysis
    removeBookmarkOfAnAnalysis(analysisId:string) : Promise<any>{
      var remUrl = stringformat(AnalysisURLS.REMOVE_BOOKMARK_OF_ANALYSIS.url,this.filterIdString(analysisId));
      return this
              .invokeService(this.myURL+remUrl,null,HttpVerbs.DELETE)
              .then(data => data.data[AnalysisURLS.REMOVE_BOOKMARK_OF_ANALYSIS.propname]);
    }
        //Returns All the Related Paras based on the selected contexts and documents in an analysis.
    getRelatedParasInAnAnalysis(analysisId:string, paraId: string) : Promise<any>{
      var remUrl = stringformat(AnalysisURLS.GET_RELATED_PARAS_OF_ANALYSIS.url, this.filterIdString(analysisId), this.filterIdString(paraId));
      return this
              .invokeService(this.myURL+remUrl,null,HttpVerbs.GET)
              //.then(data => data.data);
               .then(data => data.data[AnalysisURLS.GET_RELATED_PARAS_OF_ANALYSIS.propname]);
    }

    createChecklist(analysisId:string,checklistObj: NewChecklistRequestVM) :Promise<any>{
        var remUrl = stringformat(AnalysisURLS.CREATE_CHECKLIST.url,this.filterIdString(analysisId));
        return this
                .invokeService(this.myURL+remUrl,checklistObj,HttpVerbs.POST)
                .then(data => data.data[AnalysisURLS.CREATE_CHECKLIST.propname]);
    }

    addParaNote(analysisId:string,paraId:string, paraNote: ParaNoteVM) :Promise<any>{
        var remUrl = stringformat(AnalysisURLS.ADD_NOTE.url,this.filterIdString(analysisId),this.filterIdString(paraId));
        return this
                .invokeService(this.myURL+remUrl,paraNote,HttpVerbs.POST)
                .then(data => data.data[AnalysisURLS.ADD_NOTE.propname]);
    }

    getParaNote(analysisId:string,paraId:string) :Promise<any>{
        var remUrl = stringformat(AnalysisURLS.GET_NOTE.url,this.filterIdString(analysisId),this.filterIdString(paraId));
        return this
                .invokeService(this.myURL+remUrl,null,HttpVerbs.GET)
                .then(data => data.data[AnalysisURLS.GET_NOTE.propname]);
    }
    // getFeedbackInaccuracyReasonsList(){
    //   var remUrl = AnalysisURLS.GET_FEEDBACK_TYPES.url;
    //     return this
    //             .invokeService(this.baseUrl+"feedback/"+remUrl,null,HttpVerbs.GET)
    //             .then(data => data.data[AnalysisURLS.GET_FEEDBACK_TYPES.propname]);
    // }

    // saveParaFeedback(paraId,paraFeedbackList){
    //    var remUrl = stringformat(AnalysisURLS.SAVE_PARA_FEEDBACK.url,this.filterIdString(paraId));
    //     return this
    //             .invokeService(this.baseUrl+remUrl,paraFeedbackList,HttpVerbs.POST)
    //             .then(data => data.data[AnalysisURLS.SAVE_PARA_FEEDBACK.propname]);
    // }
}
var  AnalysisURLS = {
      CREATE_ANALYSIS : {
          url : "",
          propname : "analysis" //needs to check 
      },
      CREATE_CHECKLIST : {
          url : "/{0}/checklist",
          propname : "checklist" //needs to check 
      },
      GET_ALL_ANALYSIS:{
          url : "",
          propname : "analysis"
      },
      GET_CCO_ANALYSIS:{
          url : "/all",
          propname : "analysis"
      },
      UPDATE_ANALYSIS : {
        url : "/{0}",
        propname : "analysis" //needs to check 
      },
      GET_ANALYSIS_BY_ID : {
        url : "/{0}",
        propname : "analysis" //needs to check 
      },
      GET_ANALYSIS_BY_CKID : {
        url : "/{0}/checklistItem/{1}",
        propname : "analysis" //needs to check 
      },
      DELETE_ANALYSIS_BY_ID : {
        url : "/{0}",
        propname : "" //needs to check 
      },
      BOOKMARK_AN_ANALYSIS : {
        url: "/{0}/bookmark",
        propname : "" //needs to check 
      },
      REMOVE_BOOKMARK_OF_ANALYSIS : {
        url: "/{0}/bookmark",
        propname : "" //needs to check 
      },
      GET_RELATED_PARAS_OF_ANALYSIS : {
        url: "/{0}/relatedParas/{1}",
        propname : "relatedParagraphs" //needs to check 
      },
      ADD_NOTE : {
        url: "/{0}/note/{1}",
        propname : "" 
      },
      GET_NOTE : {
        url: "/{0}/note/{1}",
        propname : "" 
      },

      // GET_FEEDBACK_TYPES:{
      //   url: "getFeedbackTypes",
      //   propname : "feedbackTypes"
      // },
      // SAVE_PARA_FEEDBACK:{
      //   url: "para/{0}/feedback",
      //   propname : ""
      // }
  }
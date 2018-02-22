import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import {BaseHttpService} from "../../services/base-http.service";
import { ClientState} from '../../providers/clientstate.provider';
import {HttpVerbs} from "../../models/constants.model";

declare var stringformat : any;

@Injectable()
export class FeedbackService extends BaseHttpService {
  // myURL : string = this.baseUrl + 'analysis';

    constructor( appState: ClientState, http: Http ){
        super(appState, http);
    }

    getFeedbackInaccuracyReasonsList(){
      var remUrl = this.baseUrl+"feedback/"+FeedbackURLS.GET_FEEDBACK_TYPES.url;
        return this
                .invokeService(remUrl,null,HttpVerbs.GET)
                .then(data => data.data[FeedbackURLS.GET_FEEDBACK_TYPES.propname]);
    }

    saveParaFeedback(paraId,paraFeedbackList){
       var remUrl = stringformat(FeedbackURLS.SAVE_PARA_FEEDBACK.url,this.filterIdString(paraId));
        return this
                .invokeService(this.baseUrl+remUrl,paraFeedbackList,HttpVerbs.POST)
                .then(data => data.data[FeedbackURLS.SAVE_PARA_FEEDBACK.propname]);
    }
    // saveDocumentFeedback(docId){
    //     var remUrl = stringformat(FeedbackURLS.SAVE_PARA_FEEDBACK.url,this.filterIdString(paraId));
    //     return this
    //             .invokeService(this.baseUrl+remUrl,null,HttpVerbs.POST)
    //             .then(data => data.data[FeedbackURLS.SAVE_PARA_FEEDBACK.propname]);
    // }

}

var  FeedbackURLS = {
      GET_FEEDBACK_TYPES:{
        url: "getFeedbackTypes",
        propname : "feedbackTypes"
      },
      SAVE_PARA_FEEDBACK:{
        url: "para/{0}/feedback",
        propname : ""
      }
  }

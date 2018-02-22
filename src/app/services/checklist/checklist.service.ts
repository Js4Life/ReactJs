import { Injectable, EventEmitter }     from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import {RegulationVM} from "../../models/regulation.model"
import {ContextVM} from "../../models/context.model"
import {HttpVerbs} from "../../models/constants.model"
import {BaseHttpService} from "../../services/base-http.service"
import { ClientState} from '../../providers/clientstate.provider';
import { checklistItemVM} from '../../models/checklistItem.model';


declare var stringformat : any;

@Injectable()
export class ChecklistSvc extends BaseHttpService{

    myURL : string = this.baseUrl + 'checklist';

    public emitFromEditor: EventEmitter<boolean>;

    constructor( appState: ClientState, http: Http ){
        super(appState, http);
        this.emitFromEditor= new EventEmitter();
    }

    getEditorChanges(flag){
        this.emitFromEditor.emit(flag);
    }
    getChecklistReportById(id){
        var remUrl = stringformat(this.filterIdString(id));
        return this
                .invokeService(this.myURL+'/'+remUrl+'/report',null,HttpVerbs.GET)
                .then(data => data.data);
            // .then(data => data.data[checklistURLS.CHECKLIST_BY_ID.propname]);
    }
    deleteChecklistById(id){
        var remUrl = stringformat(this.filterIdString(id));
        return this
                .invokeService(this.myURL+'/'+remUrl,null,HttpVerbs.DELETE)
                .then(data => data.data);
    }
    getContextCompliance(){
        return this
                .invokeService(this.myURL+checklistURLS.CONTEXT_COMPLIANCE.url,null,HttpVerbs.GET)
            // .then(data => data.data);
            .then(data => data.data[checklistURLS.CONTEXT_COMPLIANCE.propname]);
    }
    getComponentCompliance(){
        return this
                .invokeService(this.myURL+checklistURLS.COMPONENT_COMPLIANCE.url,null,HttpVerbs.GET)
            // .then(data => data.data);
            .then(data => data.data[checklistURLS.COMPONENT_COMPLIANCE.propname]);
    }
    getBusinessSegmentCompliance(){
        return this
                .invokeService(this.myURL+checklistURLS.BUSINESSSEGMENT_COMPLIANCE.url,null,HttpVerbs.GET)
            // .then(data => data.data);
            .then(data => data.data[checklistURLS.BUSINESSSEGMENT_COMPLIANCE.propname]);
    }
    getProductCompliance(){
        return this
                .invokeService(this.myURL+checklistURLS.PRODUCT_COMPLIANCE.url,null,HttpVerbs.GET)
            // .then(data => data.data);
            .then(data => data.data[checklistURLS.PRODUCT_COMPLIANCE.propname]);
    }
    tagParagraph(checklistItemId, paraId){
        var remUrl = stringformat(checklistURLS.TAG_PARAGRAPH.url,this.filterIdString(checklistItemId), this.filterIdString(paraId));
        return this
                .invokeService(this.myURL+remUrl,JSON.stringify(""),HttpVerbs.PUT)
            // .then(data => data.data);
            .then(data => data.data[checklistURLS.TAG_PARAGRAPH.propname]);
    }
    untagParagraph(checklistItemId, paraId){
        var remUrl = stringformat(checklistURLS.TAG_PARAGRAPH.url,this.filterIdString(checklistItemId), this.filterIdString(paraId));
        return this
                .invokeService(this.myURL+remUrl,null,HttpVerbs.DELETE)
            // .then(data => data.data);
            .then(data => data.data[checklistURLS.TAG_PARAGRAPH.propname]);
    }



    getAllChecklist(){
        return this
                .invokeService(this.myURL+checklistURLS.ALL_CHECKLIST.url,null,HttpVerbs.GET)
            // .then(data => data.data);
            .then(data => data.data[checklistURLS.ALL_CHECKLIST.propname]);
    }
    getChecklistById(id){
        var remUrl = stringformat(checklistURLS.CHECKLIST_BY_ID.url,this.filterIdString(id));
        return this
                .invokeService(this.myURL+remUrl,null,HttpVerbs.GET)
            // .then(data => data.data);
            .then(data => data.data[checklistURLS.CHECKLIST_BY_ID.propname]);
    }
    getDashboard(){
        var remUrl = stringformat(checklistURLS.DASHBOARD.url);
        return this
                .invokeService(this.myURL+remUrl,null,HttpVerbs.GET)
            // .then(data => data.data);
            .then(data => data.data[checklistURLS.DASHBOARD.propname]);
    }
     saveChecklistItem(checklistId:string,checklistItemObj:checklistItemVM){
        var remUrl = stringformat(checklistURLS.CREATE_CHECKLIST_ITEM.url,this.filterIdString(checklistId));
        return this
                .invokeService(this.myURL+remUrl,checklistItemObj,HttpVerbs.POST)
            // .then(data => data.data);
            .then(data => data.data[checklistURLS.CREATE_CHECKLIST_ITEM.propname]);
    }
    updateChecklistItem(checklistId:string,checklistItemObj:checklistItemVM){
        var remUrl = stringformat(checklistURLS.CREATE_CHECKLIST_ITEM.url,this.filterIdString(checklistId));
        return this
            .invokeService(this.myURL+remUrl,checklistItemObj,HttpVerbs.PUT)
            // .then(data => data.data);
            .then(data => data.data[checklistURLS.CREATE_CHECKLIST_ITEM.propname]);
    }
}

var checklistURLS = {
    CONTEXT_COMPLIANCE : {
        url : "/CONTEXT/compliance",
        propname : "checklistItemCompliance"
    },
    COMPONENT_COMPLIANCE : {
        url : "/COMPONENTTYPE/compliance",
        propname : "checklistItemCompliance"
    },
    BUSINESSSEGMENT_COMPLIANCE: {
        url : "/BUSINESSSEGMENT/compliance",
        propname : "checklistItemCompliance"
    },    
    PRODUCT_COMPLIANCE: {
        url : "/PRODUCT/compliance",
        propname : "checklistItemCompliance"
    },    
    ALL_CHECKLIST : {
        url : "",
        propname : "checkLists"
    },
    CHECKLIST_BY_ID : {
        url : "/{0}",
        propname : "checklist"
    },
    DASHBOARD : {
        url : "/CHECKLISTS/compliance",
        propname : "checklistItemCompliance"
    },
    CREATE_CHECKLIST_ITEM : {
        url : "/{0}/checklistItem",
        propname : "checklistItem"
    },
    TAG_PARAGRAPH :{
        url : "/{0}/tag/{1}",
        propname : ""
    }
}

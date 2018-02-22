import { Injectable }     from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import {RegulationVM} from "../../models/regulation.model"
import {ContextVM} from "../../models/context.model"
import {HttpVerbs} from "../../models/constants.model"
import {BaseHttpService} from "../../services/base-http.service"
import { ClientState} from '../../providers/clientstate.provider';

declare var stringformat : any;

@Injectable()
export class MasterDataSvc extends BaseHttpService{

    myURL : string = this.baseUrl + 'masterdata/';

    constructor( appState: ClientState, http: Http ){
        super(appState, http);
    }

    // getDocumentAccessModifierTypes(){
    //     return this.invokeService(this.env.BASE_URL5, '', HttpVerbs.GET);
    // }

    getAllRegulations() /*: Promise<RegulationVM>*/{
        return this
                .invokeService(this.myURL+MasterDataURLS.ALLREGULATIONS.url,null,HttpVerbs.GET)
            .then(data => data.data[MasterDataURLS.ALLREGULATIONS.propname]);
    }
    getAllContexts() : Promise<ContextVM>{
        return this
                .invokeService(this.myURL+MasterDataURLS.ALLCONTEXTS.url,null,HttpVerbs.GET)
                .then(data => data.data[MasterDataURLS.ALLCONTEXTS.propname]);
    }
    getComponentTypeByContextId(id){
        var remUrl = stringformat(this.filterIdString(id));
        return this
        .invokeService(this.myURL+MasterDataURLS.ALLCONTEXTS.url+'/'+remUrl+'/'+MasterDataURLS.COMPONENTTYPE.url,null,HttpVerbs.GET)
        .then(data => data.data[MasterDataURLS.COMPONENTTYPE.propname]);
    }
}

var MasterDataURLS = {
    ALLREGULATIONS : {
        url : "regulations",
        propname : "regulations" 
    },
    ALLCONTEXTS : {
        url : "contexts",
        propname : "contexts" 
    },
    COMPONENTTYPE : {
        url: "componentType",
        propname: "componentType"
    }
}

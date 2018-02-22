import { Injectable }     from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import {RegulationVM} from "../../models/regulation.model"
import {ContextVM} from "../../models/context.model"
import {HttpVerbs} from "../../models/constants.model"
import {BaseHttpService} from "../../services/base-http.service"
import { ClientState} from '../../providers/clientstate.provider';
import {Constants} from '../../models/constants.model';
import {UserVM} from "../../models/user.model";


declare var stringformat : any;

@Injectable()
export class UserSvc extends BaseHttpService{

    myURL : string = this.baseUrl + 'users';
    userId :string = "10:1545";

    constructor( appState: ClientState, http: Http ){
        super(appState, http);
    }

    register(userModel: UserVM){
      return this.invokeService(this.myURL+UserURLS.REGISTER.url,userModel ,HttpVerbs.POST)
        .then(data => data.data);
        // .then(data => data.data[UserURLS.REGISTER.propname]);
    }
    getAllRoles(){
        return this
                .invokeService(this.myURL+UserURLS.ROLES.url,null,HttpVerbs.GET)
            // .then(data => data.data);
            .then(data => data.data[UserURLS.ROLES.propname]);
    }
    getAllUsers() /*: Promise<RegulationVM>*/{
        return this
                .invokeService(this.myURL+UserURLS.ALLUSERS.url,null,HttpVerbs.GET)
            .then(data => data.data[UserURLS.ALLUSERS.propname]);
    }
    getChecklistByUserId(id){
        var remUrl = stringformat(UserURLS.CHECKLIST_BY_USER.url,this.filterIdString(id));
        return this
                .invokeService(this.myURL+remUrl,null,HttpVerbs.GET)
            // .then(data => data.data);
            .then(data => data.data[UserURLS.CHECKLIST_BY_USER.propname]);
    }
}

var UserURLS = {
    REGISTER : {
        url : "/signUp",
        propname : "" 
    },
    ROLES : {
        url : "/roles",
        propname : "roles" 
    },
    ALLUSERS : {
        url : "",
        propname : "users" 
    },
    CHECKLIST_BY_USER : {
        url : "/{0}/checklist",
        propname : "checkLists"
    }
}

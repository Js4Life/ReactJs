import { Injectable }     from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import {Observable} from 'rxjs/Rx';
import {Jsonp} from '@angular/http';
import {ResponseModel} from '../../models/response.model';
import { environment } from '../../../environments/environment';
import { BaseHttpService } from './../base-http.service';
import {Constants} from '../../models/constants.model';
import { Router } from '@angular/router';
import { ClientState} from '../../providers/clientstate.provider';
import {HttpVerbs} from "../../models/constants.model"

import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';

@Injectable()
export class AuthenticationService extends BaseHttpService {
    // private authUrl : string = environment.BASE_URL + "authenticate";
    private authUrl1 : string = environment.BASE_URL + "users/signIn";
    private authUrl : String;

    constructor(public appState : ClientState , http: Http , public router : Router) {
        super(appState,http);
    }

   login(obj) : Promise<boolean> {
      return this.invokeService(this.authUrl1,obj ,HttpVerbs.POST)
        .then(data => {
            if(data.status){
                console.log(data);
                localStorage.setItem(Constants.LOGIN_RESPONSE,JSON.stringify(data.data));
                localStorage.setItem(Constants.LOGGED_USER,JSON.stringify(data.data.user));
                return true;
            }
            else{
                return false;
            }
        });
   }


}

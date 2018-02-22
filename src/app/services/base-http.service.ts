import { Injectable, Output,EventEmitter }     from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable} from 'rxjs/Rx';
import { Subject } from 'rxjs/Subject';
import { Jsonp} from '@angular/http';
import { ResponseModel} from '../models/response.model';

import { environment } from '../../environments/environment';
import { Constants , HttpVerbs} from '../models/constants.model';
import { ClientState} from '../providers/clientstate.provider';


// Import RxJs required methods
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';

@Injectable()
export  class BaseHttpService {

    protected baseUrl:string =  environment.BASE_URL;
    protected env = environment;
    headers:any;
    public loading: EventEmitter<boolean>;
    public gotoLogin: EventEmitter<boolean>;
    loadingCount:number=0;

    constructor( public appState : ClientState, private http: Http) { 
        this.loading= new EventEmitter();
        this.gotoLogin= new EventEmitter();
    }

    invokeService(url,data,httpVerb : HttpVerbs,headers?) : Promise<ResponseModel> {

        this.loading.emit(true);
        this.loadingCount++;
        
        var resObservable : Observable<ResponseModel>;
        var httpResponseErr : ResponseModel ;

        this.headers = headers;

        switch( httpVerb ){
            case HttpVerbs.POST:
            resObservable = this.postData(url,data);
            break;
            case HttpVerbs.GET:
            resObservable = this.getData(url);
            break;
            case HttpVerbs.DELETE:
            resObservable = this.deleteData(url);
            break;
            case HttpVerbs.PUT:
            resObservable = this.putData(url,data);
            break;
        }

        return resObservable
            .map(data => data)
            .toPromise()
            .then(data => {
                if(data.code==401)
                    this.gotoLogin.emit(true);
                    
                this.loadingCount--;
                if(this.loadingCount!=0)
                    this.loading.emit(true);
                if(this.loadingCount==0)
                    this.loading.emit(false);
                
                return data;
            })
            .catch((error: any) => {
                httpResponseErr = new ResponseModel();
                httpResponseErr.status = false;
                httpResponseErr.code = error.status;
                if(error.status==401)
                    this.gotoLogin.emit(true);
                
                if(this.loadingCount!=0)
                    this.loading.emit(true);
                if(this.loadingCount==1){
                    this.loadingCount--;
                    this.loading.emit(false);
                }

                return httpResponseErr;
            });

            
    } 
    
    postData(url, obj): Observable<ResponseModel> {

        if(this.headers != null)
            return this.http.post(url, obj).map(data => data.json())
                        .catch((error: any) => Observable.throw(error || 'Server error'));
        else
            return this.http.post(url, obj, { headers: this.tokenAuthorizer() }).map(data => data.json())
                        .catch((error: any) => Observable.throw(error || 'Server error'));

    }

    getData(url): Observable<ResponseModel> {
        return this.http.get(url, { headers: this.tokenAuthorizer() })
            .map(data => data.json())
            .catch((error: any) => Observable.throw(error || 'Server error'));
    }

    putData(url, obj): Observable<ResponseModel> {
        return this.http.put(url, obj, { headers: this.tokenAuthorizer() })
            .map(data => data.json())
            .catch((error: any) => Observable.throw(error || 'Server error'));

    }

    deleteData(url): Observable<ResponseModel> {
        return this.http.delete(url, { headers: this.tokenAuthorizer() })
            .map(data => data.json())
            .catch((error: any) => Observable.throw(error || 'Server error'));
    }

    tokenAuthorizer(){
        if(this.headers == null){
            let headers = new Headers({'Content-Type': 'application/json'}); 
            if(this.headers)
                headers=new Headers(this.headers); 
            
            //headers.
            var currUser = localStorage.getItem(Constants.LOGGED_USER);
            if(!currUser || currUser == "undefined" || currUser == "null"){
                localStorage.clear();
                headers.append('Authorization' , "Bearer " +  '');
                return headers;
            }
            else{
                var token = JSON.parse(currUser).token;
                headers.append('Authorization' , "Bearer " +  token);
                return headers;
            }
        }
         
    }
    filterIdString(id: string): string {
        return id.replace("#","");
    }


}

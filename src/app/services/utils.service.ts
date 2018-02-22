import { Injectable , EventEmitter}     from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import {RegulationVM} from "../models/regulation.model";
import {ContextVM} from "../models/context.model";
import {ContextWeightVM} from "../models/contextweight.model";
import * as _ from 'underscore';
import { Observable } from 'rxjs/Observable';


import { ClientState } from '../providers/clientstate.provider';
import { ContextWord } from '../models/contextword.model';



@Injectable()
export class UtilsService {

    r_color=165;
    g_color=0;
    b_color=0;
    color:string;
    public notifyParaViewFlag: EventEmitter<boolean>;
    public fileExplorerFeedback: EventEmitter<boolean>;
    public paragraphExplorerFeedback: EventEmitter<boolean>;
    public selectedVocab : any;
    

    constructor(private clientState:ClientState){
        this.notifyParaViewFlag= new EventEmitter();
        this.fileExplorerFeedback= new EventEmitter();
        this.paragraphExplorerFeedback = new EventEmitter();
        // this.selectedVocab = new EventEmitter();
    }

    // notifySelectedVocabData() : Observable<any>{
    // //   this.selectedVocab=vocab;
    // //   this.selectedVocab.emit(vocab);
    // return this.selectedVocab;
    // // .map((res: Response) => res.json());  
    // }

    notifyfileExplorerFeedback(flag){
        this.fileExplorerFeedback.emit(flag);
    }

    notifyparagraphExplorerFeedback(flag){
        this.paragraphExplorerFeedback.emit(flag);
    }

    notifyParaView(){
        this.notifyParaViewFlag.emit(true);
    }
    
    updateWeightAndColor(contextsList : any[]){
        this.r_color=165;
        this.g_color=0;
        this.b_color=0;

        var context_len = 0;
        contextsList.forEach(obj=>{
            context_len++;
        })
                
        contextsList.forEach(obj=>{
            obj.isSelected=false;
            obj.value=(obj.weight*100).toFixed(2)+"%";
            var ctxObj = this.clientState.contextsColors[obj.contextURI];
            if(ctxObj != undefined){
                obj.contextId = obj.id = ctxObj.id;
                obj.contextName = obj.name = ctxObj.name;
                var hashcodeval = this.hashfunction(obj.name);
                obj.color = this.colorGenerator(hashcodeval, context_len);
            }
        })
        return contextsList;
    }

    hashfunction(contextst) {
        var hash = 0, i, chr;
        if (contextst.length === 0) return hash;
        for (i = 0; i < contextst.length; i++) {
          chr   = contextst.charCodeAt(i);
          hash  = ((hash << 5) - hash) + chr;
          hash |= 0; // Convert to 32bit integer
        }
        return Math.abs(hash);
      }

    // mapContextColor(contextData){
    //     contextData.forEach(obj=>{            
    //         var ctxName = obj.name;
    //         ctxName = ctxName.replace(" ","");
    //         var ctxObj = this.clientState.contextsColors[ctxName];
    //         if(ctxObj != undefined){
    //             ctxObj.id = obj.id;
    //             ctxObj.contextURI = obj.contextURI;
    //             obj.color = ctxObj.color;
    //         }
    //     })
    //     console.log(contextData);
    //     return contextData;
    // }

    colorGenerator(hashcodevalue, total_context){
        //if(this.g_color<=250)
        //    this.g_color+=10;
        //else this.g_color-=9;
        //if(this.b_color<=250)
        //    this.b_color+=10;
        //else this.b_color-=9;
        var cc_mult = hashcodevalue % total_context;
        var g_cc = this.g_color + (cc_mult * Math.floor(250/total_context));
        var b_cc = this.b_color + (cc_mult * Math.floor(250/total_context));
        var c = 'rgb('+this.r_color+','+g_cc+','+b_cc+')';
        // var color='hsl('+c+',100%,50%)';
        return c;
    }

    createContextColor(contextData){
        this.r_color=165;
        this.g_color=0;
        this.b_color=0;

        var context_len = 0;
        contextData.forEach(obj=>{
            context_len++;
        })

        contextData = _.sortBy(contextData, 'name');
        contextData.forEach(obj=>{ 
            var hashcodeval = this.hashfunction(obj.name);
            var color = this.colorGenerator(hashcodeval, context_len);   
            this.clientState.contextsColors[obj.contextURI]={
                name:obj.name,
                id:obj.id,
                color:color
            };
            obj.color = color;
        })
        console.log(this.clientState.contextsColors);
        return contextData;
    }
    addHighlightingInfo(paraText,list)
    {   
        list.forEach(obj=>{
            var str = obj.word.toLowerCase()
            var splitted = str.split(" ");
            var splitted_joined = splitted.join("[ \t-_]?")
           
            var splitted_joined_reg = new RegExp(splitted_joined)
            if (paraText.search(splitted_joined_reg) != -1 ) {
                var innerHTML = "<span style='background-color:"+obj.color+";cursor:pointer'>" + obj.word + "</span>";
                paraText = paraText.replace(new RegExp(obj.word, 'ig'), innerHTML);
            }
        })
        return paraText;
    }

    // flattenContextWords(ctxWords : ContextWord[]){

    //     var flattenedList : any[] = [];
    //     ctxWords.forEach( ctxWord => {
    //         var ctxName = ctxWord.contextName;
    //         var color = '#000055';
    //         ctxName = ctxName.replace(" ","");
    //         var ctxObj = this.clientState.contextsColors[ctxName];
    //         if(ctxObj != undefined)
    //             color = ctxObj.color;

    //         ctxWord.words.forEach( a => {
    //             var obj : any = {};
    //             obj.word = a;
    //             obj.color = color;
    //             flattenedList.push(obj);
    //         })
    //     });
    //     return flattenedList;
    // }
}

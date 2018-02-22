import { Component, OnInit } from '@angular/core';
import { Router }            from '@angular/router';

import { Observable }        from 'rxjs/Observable';
import { Subject }           from 'rxjs/Subject';
// Observable class extensions
import 'rxjs/add/observable/of';

// Observable operators
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/distinctUntilChanged';
import * as _ from 'underscore';

import { BaseHttpService} from '../services/base-http.service';
import { ChecklistFiles } from './checklist-files';
import { environment } from "../../environments/environment";

@Component({
  selector: 'app-checklists',
  templateUrl: './checklists.component.html',
  styleUrls: ['./checklists.component.css']
})
export class ChecklistsComponent implements OnInit {
  url:string;
  files: any;
  filedata: any;
  activefiles: any;
  size: any;
  

    constructor(private router: Router,
  private baseService: BaseHttpService) {
   }

  
   onMeShow(opt,e):void {
     if(e)
     $(e.currentTarget).removeClass('inActive').addClass('active').siblings().removeClass('active').addClass('inActive');
     if(opt=='pending'){
      this.filedata=this.files;
     }
     else{
      this.filedata=this.activefiles;
     }
    
    
   }

   getAllCheckedFiles(): void {
    this.baseService.invokeService(this.url,null,1)
        .then(data => {
          this.files=data;
          this.size=this.getActiveCheckLength(data);
          this.allActiveFiles();
          this.onMeShow('pending',null);
        });
    }

    getActiveCheckLength(data:any): any{
      var length = 0;     
      _.filter(data,function(obj){
        if(obj.status!=='close'){
          length += 1;
        }
      });
      return length;
    }

    ngOnInit(): void {
    this.getAllCheckedFiles();
  }

  allActiveFiles() {
    this.activefiles = _.filter(this.files,function(obj){
      if(obj.status!=='close'){
        return obj;
      }
    })
  }
}

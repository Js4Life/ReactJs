import { Component, OnInit, Input , Output, EventEmitter} from '@angular/core';
import {CKEditorModule,CKEditorComponent} from 'ng2-ckeditor';
import { ChecklistSvc } from '../services/checklist/checklist.service';


@Component({
  selector: 'editor',
  templateUrl: './editor.component.html',
  styleUrls: ['./editor.component.css']
})
export class EditorComponent implements OnInit {

  @Input() data:any;
  @Input() type:any;
  @Output() change: EventEmitter<any> = new EventEmitter<any>();
  ckeditorContent = '';
  config:any=[];
  constructor(private checklistSvc:ChecklistSvc) {
    checklistSvc.emitFromEditor.subscribe(flag => this.save(flag));
    // setInterval(() => { this.save(); }, 500);
  }

  setConfig(){
    this.ckeditorContent = '<p>'+this.data+'</p>';
    if(!this.data)
      this.ckeditorContent = '<p>Start typing here..</p>';
    this.config = {uiColor: '#e2e4f5'};
    this.config.toolbarGroups= [
				{"name":"basicstyles","groups":["basicstyles"]},
        { name: 'clipboard',   groups: [ 'clipboard', 'undo' ] },
				// {"name":"links","groups":["links"]},
				// {"name":"paragraph","groups":['list', 'indent', 'blocks', 'align', 'bidi']},
				{"name":"paragraph","groups":['list', 'indent', 'align']},
				// {"name":"document","groups":["mode"]},
				// {"name":"insert","groups":["insert"]},
				{"name":"styles","groups":["styles"]},
        { name: 'colors' },
        // { name: 'tools' },
				// {"name":"about","groups":["about"]}
			];
    this.config.removeButtons= 'Strike,Anchor,about,Specialchar,links,table,document,tools';
    this.config.resize_enabled = false;
    this.config.removePlugins = 'elementspath';
    this.config.height = '25em';
    this.config.width = '100%'; 
    this.config.autoGrow_maxHeight = '25em';
  }

  save(flag){
    if(flag)
      this.change.emit({data:this.ckeditorContent, type:this.type});
  }


  ngOnInit() {
    this.setConfig();
    
  }

}

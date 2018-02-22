import { Component, OnInit ,Input, Directive } from '@angular/core';

@Component({
  selector: 'document-para-view',
  templateUrl: './document-para-view.component.html',
  styleUrls: ['./document-para-view.component.css']
})

export class DocumentParaViewComponent implements OnInit {

  @Input() documents:any; 
  @Input() selectedPara:any;
  currentDoc:string;
  constructor() { }

  // paraCreatedCallback(e){
  //   var h = $(e.currentTarget).height();
  //   $(e.currentTarget).find('.contextBar').height($(e.currentTarget).height());
  // }

  scrollToSelectedPara(){
    $('#para'+this.selectedPara.name).addClass('selectedPara').removeClass('unselectedPara').siblings().addClass('unselectedPara').removeClass('selectedPara');
    $('.paraContainer').animate({
        scrollTop: $('#para'+this.selectedPara.name).offset().top - 300
    }, 2000);
  }

  ngOnInit() {
    this.currentDoc=this.documents[0];
  }
  ngAfterViewInit() {
    this.scrollToSelectedPara();
  }

}

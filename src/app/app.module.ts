import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { routes } from './app.routes';
import { AppRouteConfig } from './app.router-config';
// import { ModalModule } from 'angular2-modal';
// import { BootstrapModalModule } from 'angular2-modal/plugins/bootstrap';
import { FormsModule,FormControl, ReactiveFormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { JsonpModule} from '@angular/http';
import {  FileUploadModule } from 'ng2-file-upload';
import { LoadingModule , ANIMATION_TYPES } from 'ngx-loading';
import {CKEditorModule} from 'ng2-ckeditor';
import { ChartsModule } from 'ng2-charts/ng2-charts';
import {ToastModule} from 'ng2-toastr/ng2-toastr';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

// import { TreeModule } from 'ng2-tree';
// import { ToastrModule } from 'toastr-ng2';

import { AuthenticationService} from './services/security/authentication.service';
import { BaseHttpService} from './services/base-http.service';
import { DocumentSvc } from './services/document/document.service';
import { MasterDataSvc } from './services/masterdata/masterdata.service';
import {AnalysisService}from './services/analysis/analysis.service';
import {FeedbackService}from './services/feedback/feedback.service';
import {UserSvc} from './services/user/user.service';
import { UtilsService } from './services/utils.service';
import { ChecklistSvc } from './services/checklist/checklist.service';

import { AppComponent } from './app.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { LoginComponent } from './login/login.component';
import * as $ from 'jquery';
import { SidenavComponent } from './sidenav/sidenav.component';
import { HomeComponent } from './home/home.component';
import { FileExplorerComponent } from './file-explorer/file-explorer.component';

import { ListComponent } from './list/list.component';
import { CollapsibleListComponent } from './collapsible-list/collapsible-list.component';
import { ContextsComponent } from './contexts/contexts.component';
import { NewChecklistComponent } from './new-checklist/new-checklist.component';
import { ParagraphViewComponent } from './paragraph-view/paragraph-view.component';
import { DocumentViewComponent } from './document-view/document-view.component';
import { ParagraphExplorerComponent } from './paragraph-explorer/paragraph-explorer.component';
import { ClientState} from './providers/clientstate.provider';


import { AnalysisComponent } from './analysis/analysis.component';
import { ChecklistsComponent } from "./checklists/checklists.component";
import { ApiTestComponent } from './api-test/api-test.component';
import { DocumentParaViewComponent } from './document-para-view/document-para-view.component';
import { GlossaryComponent } from './glossary/glossary.component';
import { ChecklistViewComponent } from './checklist-view/checklist-view.component';
import { ChecklistDetailsViewComponent } from './checklist-details-view/checklist-details-view.component';
import { EditorComponent } from './editor/editor.component';
import { GraphComponent } from './graph/graph.component';
import { VocabDetailsComponent } from './vocab-details/vocab-details.component';


@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    LoginComponent,
    SidenavComponent,
    HomeComponent,
    FileExplorerComponent,
    ListComponent,
    CollapsibleListComponent,
    ContextsComponent,
    NewChecklistComponent,
    ParagraphViewComponent,
    DocumentViewComponent,
    ParagraphExplorerComponent,
    AnalysisComponent,
    ChecklistsComponent,
    ApiTestComponent,
    DocumentParaViewComponent,
    GlossaryComponent,
    ChecklistViewComponent,
    ChecklistDetailsViewComponent,
    EditorComponent,
    GraphComponent,
    VocabDetailsComponent,
  ],
  imports: [
    BrowserModule,
    routes,
    FormsModule,
    LoadingModule,
    HttpModule,
    JsonpModule,
    FileUploadModule,
    CKEditorModule,
    ChartsModule,
    BrowserAnimationsModule,
    ToastModule.forRoot(),
    LoadingModule.forRoot({
      animationType: ANIMATION_TYPES.threeBounce,
      backdropBackgroundColour: 'rgba(0,0,0,0.1)', 
      backdropBorderRadius: '4px',
      primaryColour: '#00fa01', 
      secondaryColour: '#aeaea2', 
      tertiaryColour: '#ffffff'
  })
  ],
  providers: [
    AuthenticationService,
    BaseHttpService,
    MasterDataSvc,
    AnalysisService,
    FeedbackService,
    DocumentSvc,AppRouteConfig,ClientState,UtilsService,UserSvc,ChecklistSvc],
  bootstrap: [AppComponent]
})
export class AppModule { }

import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule} from '@angular/router';

import {AppComponent} from './app.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { FileExplorerComponent } from './file-explorer/file-explorer.component';
import {ChecklistViewComponent} from './checklist-view/checklist-view.component';
import { AnalysisComponent } from './analysis/analysis.component';
import { ChecklistDetailsViewComponent} from './checklist-details-view/checklist-details-view.component';
import { VocabDetailsComponent } from './vocab-details/vocab-details.component';


import { ParagraphExplorerComponent } from './paragraph-explorer/paragraph-explorer.component';
import { ApiTestComponent} from './api-test/api-test.component';


import { EditorComponent } from './editor/editor.component';


export const router : Routes = [
    {path:'', component: LoginComponent},
    {path:'login', component: LoginComponent},
    {path:'dashboard', component: DashboardComponent,
    children: [
        {path : 'home' , component : HomeComponent},
        { path:'checklist', component: ChecklistViewComponent},
        {path : 'file-explorer/:id' , component : FileExplorerComponent},
        ]
    }
    ,
    // { path:'state-selections/:dataString', component: StateSelectionsComponent},
    { path:'paragraph-explorer/:id', component: ParagraphExplorerComponent},
    { path:'paragraph-explorer/:id/:ciId', component: ParagraphExplorerComponent},
    { path:'apitest', component: ApiTestComponent},
    { path:'checklist-details/:id', component:ChecklistDetailsViewComponent},
    { path:'vector-details/:docid/:vid', component:VocabDetailsComponent}
];

export const routes : ModuleWithProviders = RouterModule.forRoot(router);



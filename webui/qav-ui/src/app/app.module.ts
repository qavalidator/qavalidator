import 'reflect-metadata';
import 'zone.js/dist/zone';

import { NgModule }      from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule }   from '@angular/forms';
import { HttpModule }    from '@angular/http';
import { LocationStrategy, HashLocationStrategy } from '@angular/common';

import {NgbModule} from '@ng-bootstrap/ng-bootstrap';

import { AppComponent }  from './app.component';
import { EdgeDetailComponent} from './graph/edge-detail.component';
import { NodeListComponent} from './graph/node-list.component';
import { NodeDetailComponent} from './graph/node-detail.component';

import { Config} from './config/config';
import { GraphService }  from './graph/graph.service';
import { routing } from './app.routing';

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        HttpModule,
        NgbModule.forRoot(),
        routing
    ],
    declarations: [
        AppComponent,
        EdgeDetailComponent,
        NodeListComponent,
        NodeDetailComponent
    ],
    providers: [
        Config,
        GraphService,
        {provide: LocationStrategy, useClass: HashLocationStrategy}
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}

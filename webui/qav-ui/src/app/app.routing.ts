import {ModuleWithProviders}  from '@angular/core';
import {Routes, RouterModule} from '@angular/router';

import {EdgeDetailComponent}  from './graph/edge-detail.component';
import {NodeListComponent}    from './graph/node-list.component';
import {NodeDetailComponent}  from './graph/node-detail.component';

const appRoutes: Routes = [
    {
        path: '',
        redirectTo: '/nodes',
        pathMatch: 'full'
    },
    {
        path: 'nodes',
        component: NodeListComponent
    },
    {
        path: 'node/:nodeId',
        component: NodeDetailComponent
    },
    {
        path: 'edge/:from/:to',
        component: EdgeDetailComponent
    }
];

export const routing: ModuleWithProviders = RouterModule.forRoot(appRoutes);

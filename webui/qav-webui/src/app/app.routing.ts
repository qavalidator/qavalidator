import {ModuleWithProviders} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {NodeListComponent} from './node-list/node-list.component';
import {NodeDetailComponent} from './node-detail/node-detail.component';
import {EdgeDetailComponent} from './edge-detail/edge-detail.component';

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

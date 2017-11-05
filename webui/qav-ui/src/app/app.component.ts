import {NgModule}  from '@angular/core';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';

import {Component} from '@angular/core';
import {GraphService} from './graph/graph.service';

@NgModule(
    {
        imports: [NgbModule]
    }
)
@Component({
    selector: 'qav',
    template: `
        <div class="container">
            <div class="header">
                <h1>QAvalidator</h1>
            </div>
            We have this graph: {{graphInfo}}
            <nav>
                <a routerLink="/nodes">Nodes</a>
            </nav>
            <router-outlet></router-outlet>
            <div class="container footer navbar-fixed-bottom">
                <p>QAvalidator &mdash; &copy; QAware GmbH 2016</p>
            </div>
        </div>
        `,
    providers: [GraphService]
})
export class AppComponent {

    title = 'QAvalidator';
    graphInfo: String;

    constructor(private graphService: GraphService) {
    }

    ngOnInit(): void {
        this.getGraphInfo();
    }

    private getGraphInfo(): void {
        this.graphService.getGraphInfo().then(graphInfo => this.graphInfo = graphInfo);
    }
}

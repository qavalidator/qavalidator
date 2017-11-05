import {Input, Component} from '@angular/core';
import {ActivatedRoute, Params} from '@angular/router';
import {GraphService} from './graph.service';
import {Dependency} from './graph.types';

/**
 * Display an Edge of the graph, with its source and target names, and its properties and base dependencies.
 */
@Component({
    selector: 'qav-edge-detail',
    template: `
        <div *ngIf="edge">
            <h2>{{sourceShortName}} &rarr; {{targetShortName}}</h2>

            <div>
                <div class="col-sm-2">Source</div>
                <div class="col-sm-10"><a [routerLink]="['/node', edge.sourceName]">{{edge.sourceName}}</a></div>
                <div class="col-sm-2">Target</div>
                <div class="col-sm-10"><a [routerLink]="['/node', edge.targetName]">{{edge.targetName}}</a></div>
                <div class="col-sm-2">Dependency Type</div>
                <div class="col-sm-10"><b>{{edge.typeName}}</b></div>
            </div>

            <div class="col-xs-12"><br/></div>
                        
            <div *ngIf="hasProperties()">
                <h3>Properties</h3>
                <table class="table table-striped table-condensed">
                    <thead>
                    <tr>
                        <td><b>Key</b></td><td><b>Value</b></td>
                    </tr>
                    </thead>
                    <tbody>
                    <tr *ngFor="let key of propertyKeys">
                        <td>{{key}}</td><td>{{edge.properties[key]}}</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        
            <div *ngIf="hasBaseDependencies()">
                <h3>Base Dependencies</h3>

                <table class="table table-striped table-condensed">
                    <thead>
                        <tr>
                            <td><b>Source</b></td>
                            <td><b>Target</b></td>
                            <td><b>Dependency Type</b></td>
                        </tr>
                    </thead>
                    <tbody>
                        <tr *ngFor="let dep of edge.baseDependencies">
                            <td><a [routerLink]="['/node', dep.sourceName]">{{dep.sourceName}}</a></td>
                            <td><a [routerLink]="['/node', dep.targetName]">{{dep.targetName}}</a></td>
                            <td><a [routerLink]="['/edge', dep.sourceName, dep.targetName]">{{dep.typeName}}</a></td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    `,
    providers: [GraphService]
})
export class EdgeDetailComponent {

    @Input()
    from: string;

    @Input()
    to: string;

    edge: Dependency;
    sourceShortName: string;
    targetShortName: string;
    propertyKeys: String[];

    constructor(private graphService: GraphService,
                private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        this.route.params.forEach((params: Params) => {
            this.from = params['from'];
            this.to = params['to'];
            this.initEdge();
        });
    }

    hasBaseDependencies(): boolean {
        return this.edge.baseDependencies && this.edge.baseDependencies.length > 0;
    }

    hasProperties(): boolean {
        return this.propertyKeys && this.propertyKeys.length > 0;
    }

    private initEdge() {
        this.graphService.getEdge(this.from, this.to).then(edge => this.setEdge(edge));
    }

    private setEdge(dependency: Dependency) {
        this.edge = dependency;
        this.sourceShortName = this.getLastPart(this.edge.sourceName);
        this.targetShortName = this.getLastPart(this.edge.targetName);
        this.propertyKeys = this.getKeys(this.edge.properties);
    }

    private getLastPart(s: string): string {
        let parts = s.split('.');
        return parts[parts.length - 1];
    }

    private getKeys(map: Map<String, any>): String[] {
        let result = [];
        for (let key in map) {
            result.push(key);
        }
        return result;
    }

}
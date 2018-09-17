import {Component, Input, OnInit} from '@angular/core';
import {Dependency} from '../graph/graph.types';
import {GraphService} from '../graph/graph.service';
import {ActivatedRoute, Params} from '@angular/router';

/**
 * Display an Edge of the graph, with its source and target names, and its properties and base dependencies.
 */
@Component({
  selector: 'app-edge-detail',
  templateUrl: './edge-detail.component.html'
})
export class EdgeDetailComponent implements OnInit {

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

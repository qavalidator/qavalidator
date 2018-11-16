import { Component, OnInit } from '@angular/core';
import {GraphService} from '../graph/graph.service';

@Component({
  selector: 'app-info',
  templateUrl: './info.component.html'
})
export class InfoComponent implements OnInit {

  graphInfo: String;

  constructor(private graphService: GraphService) { }

  ngOnInit() {
    this.readGraphInfo();
  }

  private readGraphInfo(): void {
    this.graphService.getGraphInfo().then(graphInfo => this.graphInfo = graphInfo);
  }

}

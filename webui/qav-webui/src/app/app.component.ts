import {Component} from '@angular/core';
import {GraphService} from './graph/graph.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {

  graphInfo: String;

  constructor(private graphService: GraphService) {
  }

  ngOnInit(): void {
    this.readGraphInfo();
  }

  private readGraphInfo(): void {
    this.graphService.getGraphInfo().then(graphInfo => this.graphInfo = graphInfo);
  }
}

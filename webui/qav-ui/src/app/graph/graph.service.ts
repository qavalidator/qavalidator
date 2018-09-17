import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {ResultPage} from '../util/resultpage';
import {Dependency, GraphInfo, Node} from './graph.types';


/**
 * Access to the graph REST API.
 *
 * Provides general graph info, node information on list and detail level, and edge detail information.
 */
@Injectable({
  providedIn: 'root'
})
export class GraphService {

  private graphUrl: string;

  constructor(private http: HttpClient) {
    this.graphUrl = environment.apiUrl;
  }

  getGraphInfo(): Promise<string> {
    return this.http.get<GraphInfo>(`${this.graphUrl}/info`)
      .toPromise()
      .then(response => response.info)
      .catch(this.handleError);
  }

  getNode(name: string): Promise<Node> {
    return this.http.get<Node>(`${this.graphUrl}/node?name=${name}`)
      .toPromise()
      .then(response => response)
      .catch(this.handleError);
  }

  getNodes(query: string, page: number, size: number): Promise<ResultPage<Node>> {
    let queryParam = '';
    if (query !== '') {
      queryParam = `q=${query}&`;
    }
    return this.http.get<ResultPage<Node>>(`${this.graphUrl}/nodes?${queryParam}page=${page}&size=${size}`)
      .toPromise()
      .then(response => response)
      .catch(this.handleError);
  }

  getEdge(from: string, to: string): Promise<Dependency> {
    return this.http.get<Dependency>(`${this.graphUrl}/edge?from=${from}&to=${to}`)
      .toPromise()
      .then(response => response)
      .catch(this.handleError);
  }

  private handleError(error: any): Promise<any> {
    console.error('An error occurred', error); // for demo purposes only
    return Promise.reject(error.message || error);
  }

}

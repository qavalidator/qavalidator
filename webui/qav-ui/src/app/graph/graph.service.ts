import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import 'rxjs/add/operator/toPromise';
import { ResultPage } from '../util/resultpage';
import {Node, Dependency} from './graph.types';
import { Config} from '../config/config';

/**
 * Accesses the graph REST API.
 *
 * Provides general graph info, node information on list and detail level, and edge detail information.
 */
@Injectable()
export class GraphService {

    private graphUrl: string;

    constructor(private http: Http, private config: Config) {
        this.graphUrl = config.apiUrl;
    }

    getGraphInfo(): Promise<String> {
        return this.http.get(`${this.graphUrl}/info`)
            .toPromise()
            .then(response => response.text())
            .catch(this.handleError);
    }

    getNode(name: string): Promise<Node> {
        return this.http.get(`${this.graphUrl}/node?name=${name}`)
            .toPromise()
            .then(response => response.json())
            .catch(this.handleError);
    }

    getNodes(query: string, page: number, size: number): Promise<ResultPage<Node>> {
        let queryParam = '';
        if (query !== '') {
            queryParam = `q=${query}&`;
        }
        return this.http.get(`${this.graphUrl}/nodes?${queryParam}page=${page}&size=${size}`)
            .toPromise()
            .then(response => response.json())
            .catch(this.handleError);
    }

    getEdge(from: string, to: string): Promise<Dependency> {
        return this.http.get(`${this.graphUrl}/edge?from=${from}&to=${to}`)
            .toPromise()
            .then(response => response.json())
            .catch(this.handleError);
    }

    private handleError(error: any): Promise<any> {
        console.error('An error occurred', error); // for demo purposes only
        return Promise.reject(error.message || error);
    }

}

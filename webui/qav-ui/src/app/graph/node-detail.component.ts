import {Component, Input} from '@angular/core';
import {ActivatedRoute, Params} from '@angular/router';

import {GraphService}           from './graph.service';
import {Node}                   from './graph.types';

/**
 * Shows the details of a node, with all its attributes and relations.
 *
 * All relations are navigable.
 */
@Component({
    selector: 'qav-node-detail',
    templateUrl: '/app/graph/node-detail.html',
    providers: [GraphService]
})
export class NodeDetailComponent {

    @Input()
    nodeId: string;

    node: Node;

    constructor(private graphService: GraphService,
                private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        this.route.params.forEach((params: Params) => {
            this.nodeId = params['nodeId'];
            this.initNode();
        });
    }

    hasParents(): boolean {
        return this.node.parents && this.node.parents.length > 0;
    }

    hasChildren(): boolean {
        return this.node.containedDeps && this.node.containedDeps.length > 0;
    }

    hasIncomingDependencies(): boolean {
        return this.node.incomingDeps && this.node.incomingDeps.length > 0;
    }

    hasOutgoingDependencies(): boolean {
        return this.node.outgoingDeps && this.node.outgoingDeps.length > 0;
    }

    keys(): String[] {
        let result = [];
        for (let key in this.node.properties) {
            result.push(key);
        }
        return result;
    }

    private initNode() {
        this.graphService.getNode(this.nodeId).then(node => this.node = node);
    }
}


/**
 * represents a Node:
 * with a name, properties, hierarchy relations (parents, containedDeps), and incoming/outgoing relations.
 */
export class Node {

    name: string;
    properties: Map<String, any>;

    parents: Dependency[];
    incomingDeps: Dependency[];
    outgoingDeps: Dependency[];
    containedDeps: Dependency[];
}

/**
 * represents a Dependency, i.e. an edge in the graph.
 *
 * sourceName and targetName are the names of Nodes.
 */
export class Dependency {

    sourceName: string;
    targetName: string;
    typeName: string;

    baseDependencies: Dependency[];
    properties: Map<String, any>;
}

/**
 * represent the Graph info.
 */
export class GraphInfo {
  info: string;
}

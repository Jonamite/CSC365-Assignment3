public class TestGraph {

    private Graph testGraph;

    public TestGraph(){
        this.testGraph = new Graph(false,true);
        Vertex startNode = testGraph.addVertex("v0.0.0");
        Vertex v1 = this.testGraph.addVertex("v1.0.0");
        Vertex v2 = this.testGraph.addVertex("v2.0.0");

        Vertex v11 = this.testGraph.addVertex("v1.1.0");
        Vertex v12 = this.testGraph.addVertex("v1.2.0");
        Vertex v21 = this.testGraph.addVertex("v2.1.0");

        Vertex v111 = this.testGraph.addVertex("v1.1.1");
        Vertex v112 = this.testGraph.addVertex("v1.1.2");
        Vertex v121 = this.testGraph.addVertex("v1.2.1");
        Vertex v211 = this.testGraph.addVertex("v2.1.1");

        this.testGraph.addEdge(startNode, v1, null);
        this.testGraph.addEdge(startNode, v2, null);

        this.testGraph.addEdge(startNode, v11, null);
        this.testGraph.addEdge(startNode, v12, null);
        this.testGraph.addEdge(startNode, v21, null);

        this.testGraph.addEdge(startNode, v111, null);
        this.testGraph.addEdge(startNode, v112, null);
        this.testGraph.addEdge(startNode, v121, null);
        this.testGraph.addEdge(startNode, v211, null);

        //create a cycle
        this.testGraph.addEdge(v211, v2, null);
    }

    public Vertex getStartingVertex() {
        return this.testGraph.getVertices().get(0);
    }
}

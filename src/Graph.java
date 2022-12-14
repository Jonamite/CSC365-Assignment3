import java.io.*;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Graph implements Serializable {

    private ArrayList<Vertex> vertices;
    private int numEdges = 0; //number of edges

    private boolean isWeighted;
    private boolean isDirected;

    public Graph(boolean inputIsWeighted, boolean inputIsDirected) {
        this.vertices = new ArrayList<>();
        this.isWeighted = inputIsWeighted;
        this.isDirected = inputIsDirected;
    }

    public Vertex addVertex(String data) {
        Vertex newVertex = new Vertex(data);
        this.vertices.add(newVertex);
        return newVertex;
    }

    public void addEdge(Vertex vertex1, Vertex vertex2, Integer weight) {
        if(!this.isWeighted) {
            weight = null;
        }

        vertex1.addEdge(vertex2, weight);

        if(!this.isDirected) {
            vertex2.addEdge(vertex1, weight);
        }

        numEdges++;
    }

    //get edge v1->v2
    public Edge getEdge(Vertex vertex1, Vertex vertex2){
        ArrayList<Edge> edges = vertex1.getEdges();
        for (Edge e: edges){
            if (e.getEnd() == vertex2){
                return e;
            }
        }
        return null;
    }

    public void addOrUpdateEdge(Vertex vertex1, Vertex vertex2, Integer weight) {
        if(!this.isWeighted) {
            weight = null;
        }

        Edge firstEdge = null;


        ArrayList<Edge> edges = vertex1.getEdges();
        for (Edge e: edges){
            if (e.getEnd() == vertex2){
                firstEdge = e;
                break;
            }
        }

        if (firstEdge == null){
            vertex1.addEdge(vertex2, weight);

            numEdges++;

        }else{
            if(this.isWeighted) {
                firstEdge.setWeight(firstEdge.getWeight() + weight);
            }
        }

        if(!this.isDirected) {

            Edge secondEdge = null;
            edges = vertex2.getEdges();
            for (Edge e: edges){
                if (e.getEnd() == vertex1){
                    secondEdge = e;
                    break;
                }
            }

            if (secondEdge == null){
                vertex2.addEdge(vertex1, weight);
            }else{
                if(this.isWeighted) {
                    secondEdge.setWeight(secondEdge.getWeight() + weight);
                }
            }
        }
    }

    public void removeEdge(Vertex vertex1, Vertex vertex2) {
        vertex1.removeEdge(vertex2);
        if(!this.isDirected) {
            vertex2.removeEdge(vertex1);
        }

        numEdges--;
    }

    public void removeVertex(Vertex vertex) {
        this.vertices.remove(vertex);
    }

    public ArrayList<Vertex> getVertices() {
        return this.vertices;
    }

    public boolean isWeighted() {
        return this.isWeighted;
    }

    public boolean isDirected() {
        return this.isDirected;
    }

    public Vertex getVertexByValue(String value) {
        for(Vertex v: this.vertices) {
            if(v.getData().equals(value)) {
                return v;
            }
        }
        return null;
    }

    public void print() {
        for(Vertex v: this.vertices) {
            v.print(isWeighted);
        }
    }

    public int getNumEdges() {
        return numEdges;
    }

    public static void main(String[] args) {
        Graph websites = new Graph(false, false);
        Vertex startWebsite = websites.addVertex("Wikipedia");
        Vertex nextWebsite = websites.addVertex("Wikipedia 1");

        websites.addEdge(startWebsite, nextWebsite, 0);
        websites.print();
    }

    public void saveToFile(){
        try {

            FileOutputStream fileOut = new FileOutputStream("graph.dat");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(this);
            objectOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Graph loadGraph() {

        Graph g = null;

        try {
            File f = new File("graph.dat");
            FileInputStream fi = new FileInputStream(f);
            ObjectInputStream oi = new ObjectInputStream(fi);

            g = (Graph) oi.readObject();

            oi.close();
            fi.close();
        }catch (Exception ex){
            ex.printStackTrace();
            System.out.println("Warn: cannot load graph.dat");
        }

        return g;
    }
}

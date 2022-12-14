import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Controller {

    private static int MAX_SIZE = 1000; //1000 Wikipedia pages and links
    private static int MAX_LEVEL = 30; //max level to crawl
    private static final String WIKI_URL = "https://www.wikipedia.org";

    //weight, no directed
    private Graph graph;

    @FXML
    private TextArea txtOutput ;

    @FXML
    private TextField txtFirstPage ;

    @FXML
    private TextField txtSecondPage ;

    /**
     * build graph
     * @param actionEvent
     */
    public void buildGraph(ActionEvent actionEvent) {

        File f = new File("graph.dat");
        if(f.isFile()) {
            graph = Graph.loadGraph();

        }else {
            //weight, no directed
            graph = new Graph(true, false);

            //crawl, create graph
            crawl(0, null, WIKI_URL, graph);

            graph.saveToFile();
        }

        graph.print();

        System.out.println("Number of vertices: " + graph.getVertices().size());
        System.out.println("Number of edges: " + graph.getNumEdges());

        txtFirstPage.setText("Wikipedia");
        txtSecondPage.setText("Wikipedia, the free encyclopedia");
    }

    //create adjacency matrix
    private double[][] createAdjacencyMatrix(Graph graph){
        int N = graph.getVertices().size();

        double[][] matrix = new double[N][N];
        ArrayList<Vertex> vertices = graph.getVertices();

        for (int i = 0; i < N; i++){

            for (int j = 0; j < N; j++){
                Edge e = graph.getEdge(vertices.get(i), vertices.get(j));
                if (e != null){
                    matrix[i][j] = e.getWeight();
                }
            }
        }

        return matrix;
    }

    //create Tf- term frequency
    private double[][] createTFMatrix(double[][] adjMatrix){
        int N = graph.getVertices().size();

        double[][] tfMatrix = new double[N][N];

        for (int i = 0; i < N; i++){
            double sumLine = 0;
            for (int j = 0; j < N; j++){
                sumLine += adjMatrix[i][j];
            }
            if (sumLine != 0){
                for (int j = 0; j < N; j++){
                    tfMatrix[i][j] = adjMatrix[i][j] / sumLine;
                }
            }
        }

        return tfMatrix;
    }

    //create IDF matrix
    private double[] createIDFMatrix(double[][] adjMatrix){
        int N = graph.getVertices().size();

        double[] idfMatrix = new double[N];

        for (int i = 0; i < N; i++){

            double countLine = 0;
            for (int j = 0; j < N; j++){
                if (adjMatrix[i][j] > 0){
                    countLine += 1;
                }
            }
            if (countLine != 0){
                idfMatrix[i] = N / countLine;
            }
        }

        return idfMatrix;
    }

    //create TFIDF matrix
    private double[] createTFIDFFMatrix(double[][] tfMatrix, double[] idfMatrix){
        int N = graph.getVertices().size();

        double[] tfidfMatrix = new double[N];

        for (int i = 0; i < N; i++){
            for (int j = 0; j < N; j++){
                tfidfMatrix[i] += tfMatrix[i][j] * idfMatrix[j];
            }
        }

        return tfidfMatrix;
    }

    private void crawl(int level, Vertex prev, String url, Graph graph) {

        if (level <= MAX_LEVEL){

            if (graph.getVertices().size() >= MAX_SIZE && graph.getNumEdges() >= MAX_SIZE){
                return;
            }

            Document doc = request(url);
            if (doc != null) {

                Vertex u = graph.getVertexByValue(doc.title());

                if (u == null) {
                    graph.addVertex(doc.title());
                    u = graph.getVertexByValue(doc.title());

                    for (Element link : doc.select("a[href]")) {

                        String nextLink = link.absUrl("href");

                        crawl(level++, u, nextLink, graph);
                    }
                }else {
                    if (prev != null) {
                        graph.addOrUpdateEdge(prev, u, 1);
                    }
                }

            }
        }
    }

    private Document request(String url) {
        try {
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            if (connection.response().statusCode() == 200) {

                System.out.println("Link: " + url);
                System.out.println(document.title());

                return document;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public void computeDisjointSets(ActionEvent actionEvent) {
        String msg = "";
        int idx = 1;

        if (graph != null) {

            ArrayList<Vertex> visitedVertices = new ArrayList<>();
            for (Vertex start : graph.getVertices()) {

                if (!visitedVertices.contains(start)) {

                    ArrayList<Vertex> disjointSet = new ArrayList<>();
                    disjointSet.add(start);
                    GraphTraverser.depthFirstTraversal(start, disjointSet);

                    msg += "Disjoint Set " + idx + " (" + disjointSet.size() + "): ";

                    for (Vertex u: disjointSet){
                        msg += u.getData() + " ";
                    }
                    msg += "\n";

                    visitedVertices.addAll(disjointSet);
                }
            }
        }else{
            msg = "Please build the graph";
        }

        txtOutput.setText(msg);
    }

    //find path
    public void findPath(ActionEvent actionEvent) {
        String msg = "";
        if (graph != null) {

            Vertex from = graph.getVertexByValue(txtFirstPage.getText());
            if (from == null){
                msg = "First page not found!";
            }else{
                Vertex to = graph.getVertexByValue(txtSecondPage.getText());

                if (to == null){
                    msg = "Second page not found!";
                }else{

                    //find the path
                    StringBuffer sb = new StringBuffer();
                    Dijkstra.shortestPathBetween(graph, from, to, sb);
                    msg = sb.toString();
                }
            }

        }else{
            msg = "Please build the graph";
        }

        txtOutput.setText(msg);
    }

    public void saveTFIDF(double[] tfidfMatrix){
        try {

            FileOutputStream fileOut = new FileOutputStream("tfidf.dat");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(tfidfMatrix);
            objectOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double[] loadTFIDF() {

        double[] tfidf = null;

        try {
            File f = new File("tfidf.dat");
            FileInputStream fi = new FileInputStream(f);
            ObjectInputStream oi = new ObjectInputStream(fi);

            tfidf = (double[]) oi.readObject();

            oi.close();
            fi.close();
        }catch (Exception ex){
            ex.printStackTrace();
            System.out.println("Warn: cannot load graph.dat");
        }

        return tfidf;
    }

    //compute TF-IDF
    public void computeTFIDF(ActionEvent actionEvent) {

        String msg = "";
        if (graph != null) {

            double[] tfidfMatrix = null;

            File f = new File("tfidf.dat");
            if(f.isFile()) {
                tfidfMatrix = loadTFIDF();
            }else {
                double[][] adjMatrix = createAdjacencyMatrix(graph);
                double[][] tfMatrix = createTFMatrix(adjMatrix);
                double[] idfMatrix = createIDFMatrix(adjMatrix);
                tfidfMatrix = createTFIDFFMatrix(tfMatrix, idfMatrix);

                saveTFIDF(tfidfMatrix);
            }

            ArrayList<Vertex> vertices = graph.getVertices();
            int i = 0;
            for (Vertex v: vertices){
                msg += v.getData() + ": " + tfidfMatrix[i] + "\n";
                i++;
            }

        }else{
            msg = "Please build the graph";
        }

        txtOutput.setText(msg);
    }
}

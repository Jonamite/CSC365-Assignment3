import java.io.Serializable;

@SuppressWarnings("serial")
public class Edge implements Serializable {

    private Vertex start;
    private Vertex end;
    private Integer weight;

    public Edge(Vertex startV, Vertex endV, Integer inputWeight) {
        this.start = startV;
        this.end = endV;
        this.weight = inputWeight;
    }

    public Vertex getStart() {
        return this.start;
    }

    public Vertex getEnd() {
        return this.end;
    }

    public Integer getWeight() {
        return  this.weight;
    }

    public void setWeight(Integer newWeight) {
        this.weight = newWeight;
    }
}

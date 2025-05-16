public class Edge {
    private final Station target;
    private final int distance;

    public Edge(Station target, int distance) {
        this.target = target;
        this.distance = distance;
    }

    public Station getTarget() {
        return target;
    }

    public int getDistance() {
        return distance;
    }
}
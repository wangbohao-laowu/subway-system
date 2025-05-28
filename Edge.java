/**
 * 表示两个站点之间的连接边，包含目标站点、距离和所属线路
 */
public class Edge {
    private final String to;        // 目标站点名称
    private final double distance;  // 到目标站点的距离（单位：公里）
    private final String line;      // 所属线路名称

    public Edge(String to, double distance, String line) {
        this.to = to;
        this.distance = distance;
        this.line = line;
    }

    // ------------ Getters ------------
    public String getTo() {
        return to;
    }

    public double getDistance() {
        return distance;
    }

    public String getLine() {
        return line;
    }

    // ------------ 辅助方法 ------------
    @Override
    public String toString() {
        return String.format("[%s] %s (%.3f km)", line, to, distance);
    }
}
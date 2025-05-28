import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

public class SubwaySystem {
    private final Map<String, Station> stations = new HashMap<>();
    private final Map<String, Line> lines = new HashMap<>();
    private final Map<String, List<Edge>> graph = new HashMap<>();

    // 解析地铁文件，构建线路和站点关系
    public void parseSubwayFile(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String currentLineName = null;
            Line currentLine = null;
            String prevStation = null;

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // 检测线路标题（增强健壮性）
                if (line.endsWith("线站点间距")) {
                    // 安全提取线路名称
                    int index = line.indexOf("号");
                    if (index != -1) {
                        currentLineName = line.substring(0, index) + "号线";
                    } else {
                        // 尝试其他方式提取线路名称
                        currentLineName = line.replace("站点间距", "").trim();
                    }
                    
                    // 创建线路对象
                    if (currentLineName != null && !currentLineName.isEmpty()) {
                        currentLine = new Line(currentLineName);
                        lines.put(currentLineName, currentLine);
                    } else {
                        System.err.println("警告：跳过无效线路标题: " + line);
                        currentLine = null;
                    }
                    
                    // 跳过表头行（增强健壮性）
                    skipHeaderLines(br);
                    prevStation = null;
                    continue;
                }

                // 解析站点间距数据
                if (currentLine != null) {
                    parseStationData(line, currentLine, prevStation);
                    // 更新上一站点
                    String[] parts = line.split("\t| {2,}");
                    if (parts.length >= 1) {
                        String stationPair = parts[0].trim();
                        String[] stations = stationPair.split("---");
                        if (stations.length == 2) {
                            prevStation = stations[1].trim();
                        }
                    }
                } else {
                    System.err.println("警告：跳过无效数据行（无有效线路）: " + line);
                }
            }
        }
    }

    // 辅助方法：跳过表头行（安全版）
    private void skipHeaderLines(BufferedReader br) throws IOException {
        // 尝试读取表头行
        String header = br.readLine();
        if (header != null && !header.trim().isEmpty()) {
            // 如果是表头行，继续读下一行（可能是空行）
            String possibleEmpty = br.readLine();
            if (possibleEmpty != null && !possibleEmpty.trim().isEmpty()) {
                // 如果不是空行，说明格式可能不标准
                System.err.println("警告：非标准文件格式，预期空行");
            }
        }
    }

    // 辅助方法：解析站点数据（增强健壮性）
    private void parseStationData(String dataLine, Line currentLine, String prevStation) {
        String[] parts = dataLine.split("\t| {2,}");
        if (parts.length < 2) {
            System.err.println("警告：跳过无效数据行（列数不足）: " + dataLine);
            return;
        }

        String stationPair = parts[0].trim();
        double distance;
        try {
            distance = Double.parseDouble(parts[1].trim());
        } catch (NumberFormatException e) {
            System.err.println("警告：跳过无效距离数据: " + dataLine);
            return;
        }

        String[] stations = stationPair.split("---");
        if (stations.length != 2) {
            // 尝试从上一站点推断
            if (prevStation != null) {
                String stationA = prevStation;
                String stationB = stationPair.split("---")[0].trim(); // 可能只有一端
                if (!stationB.isEmpty()) {
                    addStationData(stationA, stationB, distance, currentLine.getName());
                    prevStation = stationB;
                } else {
                    System.err.println("警告：无法解析站点对: " + stationPair);
                }
            } else {
                System.err.println("警告：跳过无效站点对格式: " + stationPair);
            }
            return;
        }

        String stationA = stations[0].trim();
        String stationB = stations[1].trim();
        
        // 添加到线路
        addStationData(stationA, stationB, distance, currentLine.getName());
    }

    // 辅助方法：添加站点数据
    private void addStationData(String stationA, String stationB, double distance, String lineName) {
        // 添加到线路
        if (lineName != null) {
            Line line = lines.get(lineName);
            if (line != null) {
                line.addStationPair(stationA, stationB, distance);
            }
        }
        
        // 更新全局站点信息
        addStationToMap(stationA, lineName);
        addStationToMap(stationB, lineName);
        
        // 构建图结构
        addEdgeToGraph(stationA, stationB, distance, lineName);
        addEdgeToGraph(stationB, stationA, distance, lineName);
    }
    // 辅助方法：将站点添加到全局映射
    private void addStationToMap(String stationName, String lineName) {
        Station station = stations.computeIfAbsent(stationName, Station::new);
        station.addLine(lineName);
    }

    // 辅助方法：添加边到图结构
    private void addEdgeToGraph(String from, String to, double distance, String line) {
        graph.computeIfAbsent(from, k -> new ArrayList<>())
             .add(new Edge(to, distance, line));
    }

    // 功能1：获取所有换乘站
    public List<TransferStation> getTransferStations() {
        return stations.values().stream()
            .filter(s -> s.getLines().size() >= 2)
            .map(s -> new TransferStation(s.getName(), s.getLines()))
            .collect(Collectors.toList());
    }

    // 功能2：查找邻近站点
    public List<NearbyStation> findNearbyStations(String stationName, double maxDistance) {
        if (!stations.containsKey(stationName)) 
            throw new IllegalArgumentException("站点不存在: " + stationName);

        List<NearbyStation> results = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        Map<String, Double> distances = new HashMap<>();

        queue.add(stationName);
        distances.put(stationName, 0.0);

        // BFS遍历，累计距离
        while (!queue.isEmpty()) {
            String current = queue.poll();
            double currentDist = distances.get(current);

            for (Edge edge : graph.getOrDefault(current, Collections.emptyList())) {
                String neighbor = edge.getTo();
                double newDist = currentDist + edge.getDistance();

                if (!visited.contains(neighbor) && newDist <= maxDistance) {
                    visited.add(neighbor);
                    distances.put(neighbor, newDist);
                    queue.add(neighbor);
                 // 记录结果
                    results.add(new NearbyStation(
                        neighbor, 
                        stations.get(neighbor).getLines().iterator().next(), // 取第一条线路
                        newDist
                    ));
                }
            }
        }

        return results.stream()
            .filter(n -> n.getDistance() > 0) // 排除自身
            .collect(Collectors.toList());
    }

    // 功能3：查找所有路径（DFS实现）
    public List<Path> findAllPaths(String start, String end) {
        if (!stations.containsKey(start) || !stations.containsKey(end)) {
            throw new IllegalArgumentException("起点或终点不存在");
        }
        
        List<Path> paths = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        dfsFindPaths(start, end, visited, new ArrayList<>(), 0.0, paths);
        return paths;
    }
    
    private void dfsFindPaths(String current, String end, Set<String> visited, 
                             List<String> currentPath, double currentDistance, 
                             List<Path> result) {
        // 到达终点
        if (current.equals(end)) {
            List<String> finalPath = new ArrayList<>(currentPath);
            finalPath.add(current);
            result.add(new Path(finalPath, currentDistance));
            return;
        }
        
        // 标记访问
        visited.add(current);
        currentPath.add(current);
        
        // 遍历邻居
        for (Edge edge : graph.getOrDefault(current, Collections.emptyList())) {
            String neighbor = edge.getTo();
            
            if (!visited.contains(neighbor)) {
                double newDistance = currentDistance + edge.getDistance();
                dfsFindPaths(neighbor, end, visited, currentPath, newDistance, result);
            }
        }
        
        // 回溯
        visited.remove(current);
        currentPath.remove(currentPath.size() - 1);
    }
    
    // 功能4：Dijkstra算法找最短路径
    public Path findShortestPath(String start, String end) {
        if (!stations.containsKey(start) || !stations.containsKey(end)) 
            {throw new IllegalArgumentException("起点或终点不存在");}

        PriorityQueue<Node> pq = new PriorityQueue<>();
        Map<String, Double> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        Map<String, String> lineMap = new HashMap<>(); // 记录到达该站点的线路

        // 初始化距离
        stations.keySet().forEach(s -> dist.put(s, Double.MAX_VALUE));
        dist.put(start, 0.0);
        pq.add(new Node(start, 0.0));
        
        //Dijkstra主循环
        while (!pq.isEmpty()) {
            Node node = pq.poll();
            String current = node.getStation();

            if (current.equals(end)) break;
            if (node.getDistance() > dist.get(current)) continue;

            for (Edge edge : graph.getOrDefault(current, Collections.emptyList())) {
                String neighbor = edge.getTo();
                double newDist = dist.get(current) + edge.getDistance();

                if (newDist < dist.get(neighbor)) {
                    dist.put(neighbor, newDist);
                    prev.put(neighbor, current);
                    lineMap.put(neighbor, edge.getLine());
                    pq.add(new Node(neighbor, newDist));
                }
            }
        }

        // 回溯路径
        LinkedList<String> path = new LinkedList<>();
        String current = end;
        while (current != null) {
            path.addFirst(current);
            current = prev.get(current);
        }

        return new Path(path, dist.get(end));
    }

    // 其他辅助类

    private static class Node implements Comparable<Node> {
        private String station;
        private double distance;

        public Node(String station, double distance) {
            this.station = station;
            this.distance = distance;
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.distance, other.distance);
        }

        // Getters
        public String getStation() { return station; }
        public double getDistance() { return distance; }
    }

    // 中转站封装类
    public static class TransferStation {
        private String name;
        private Set<String> lines;

        public TransferStation(String name, Set<String> lines) {
            this.name = name;
            this.lines = lines;
        }

        // Getters
        public String getName() { return name; }
        public Set<String> getLines() { return lines; }
    }

    // 邻近站点封装类
    public static class NearbyStation {
        private String name;
        private String line;
        private double distance;

        public NearbyStation(String name, String line, double distance) {
            this.name = name;
            this.line = line;
            this.distance = distance;
        }

        // Getters
        public String getName() { return name; }
        public String getLine() { return line; }
        public double getDistance() { return distance; }
    }
    
    //站点名字
    public Station getStation(String name) {
        Station station = stations.get(name);
        if (station == null) {
            throw new IllegalArgumentException("站点不存在: " + name);
        }
        return station;
    }
}
public class SubwaySystem {
    private final Map<String, Station> stations = new HashMap<>();
    private final Map<String, Line> lines = new HashMap<>();
    private final Map<Station, List<Edge>> graph = new HashMap<>();

    public void addLineSegment(String lineName, String station1Name, String station2Name, int distance) {
        Station station1 = stations.computeIfAbsent(station1Name, Station::new);
        Station station2 = stations.computeIfAbsent(station2Name, Station::new);

        station1.addLine(lineName);
        station2.addLine(lineName);

        Line line = lines.computeIfAbsent(lineName, Line::new);
        line.addStation(station1);
        line.addStation(station2);

        addEdge(station1, station2, distance);
        addEdge(station2, station1, distance);
    }

    private void addEdge(Station from, Station to, int distance) {
        graph.computeIfAbsent(from, k -> new ArrayList<>()).add(new Edge(to, distance));
    }

    public Set<Station> getTransferStations() {
        Set<Station> transferStations = new HashSet<>();
        for (Station station : stations.values()) {
            if (station.getLines().size() >= 2) {
                transferStations.add(station);
            }
        }
        return transferStations;
    }

    public Map<Station, Integer> findNearbyStations(Station start, int maxDistance) {
        Map<Station, Integer> distances = new HashMap<>();
        if (!stations.containsValue(start)) return distances;

        PriorityQueue<Station> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        distances.put(start, 0);
        queue.add(start);

        while (!queue.isEmpty()) {
            Station current = queue.poll();
            int currentDist = distances.get(current);

            for (Edge edge : graph.getOrDefault(current, Collections.emptyList())) {
                Station neighbor = edge.getTarget();
                int newDist = currentDist + edge.getDistance();
                if (newDist > maxDistance) continue;

                if (!distances.containsKey(neighbor) || newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    queue.add(neighbor);
                }
            }
        }

        distances.entrySet().removeIf(entry -> entry.getValue() > maxDistance);
        return distances;
    }

    public List<Path> findAllPaths(Station start, Station end) {
        List<Path> paths = new ArrayList<>();
        if (!stations.containsValue(start) || !stations.containsValue(end)) return paths;

        dfs(start, end, new HashSet<>(), new ArrayList<>(), paths);
        return paths;
    }
    
    private void dfs(Station current, Station end, Set<Station> visited, List<Station> path, List<Path> paths) {
        visited.add(current);
        path.add(current);

        if (current.equals(end)) {
            paths.add(new Path(new ArrayList<>(path), calculateDistance(path)));
        } else {
            for (Edge edge : graph.getOrDefault(current, Collections.emptyList())) {
                Station neighbor = edge.getTarget();
                if (!visited.contains(neighbor)) {
                    dfs(neighbor, end, new HashSet<>(visited), new ArrayList<>(path), paths);
                }
            }
        }
    }

    private int calculateDistance(List<Station> path) {
        int distance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Station from = path.get(i);
            Station to = path.get(i + 1);
            for (Edge edge : graph.get(from)) {
                if (edge.getTarget().equals(to)) {
                    distance += edge.getDistance();
                    break;
                }
            }
        }
        return distance;
    }

    public Path findShortestPath(Station start, Station end) {
        Map<Station, Integer> dist = new HashMap<>();
        Map<Station, Station> prev = new HashMap<>();
        PriorityQueue<Station> pq = new PriorityQueue<>(Comparator.comparingInt(dist::get));

        dist.put(start, 0);
        pq.add(start);

        while (!pq.isEmpty()) {
            Station u = pq.poll();
            if (u.equals(end)) break;

            for (Edge edge : graph.getOrDefault(u, Collections.emptyList())) {
                Station v = edge.getTarget();
                int alt = dist.get(u) + edge.getDistance();
                if (alt < dist.getOrDefault(v, Integer.MAX_VALUE)) {
                    dist.put(v, alt);
                    prev.put(v, u);
                    pq.add(v);
                }
            }
        }

        if (!prev.containsKey(end)) return null;

        List<Station> path = new ArrayList<>();
        Station current = end;
        while (current != null) {
            path.add(current);
            current = prev.get(current);
        }
        Collections.reverse(path);
        return new Path(path, dist.get(end));
    }

    public Station getStationByName(String name) {
        return stations.get(name);
    }
}
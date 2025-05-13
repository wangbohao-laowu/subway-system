class Line {
    private String name;
    private List<String> stations = new ArrayList<>();
    private List<Double> distances = new ArrayList<>();

    public Line(String name) { this.name = name; }

    public void addSegment(String from, String to, double distance) {
        if (stations.isEmpty()) {
            stations.add(from);
            stations.add(to);
            distances.add(distance);
        } else if (stations.get(stations.size() - 1).equals(from)) {
            stations.add(to);
            distances.add(distance);
        } else {
            throw new IllegalArgumentException("线路数据不连续");
        }
    }

    public List<String> getStations() { return stations; }
    public List<Double> getDistances() { return distances; }
    public String getName() { return name; }
}
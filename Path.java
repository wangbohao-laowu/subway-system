import java.util.List;

public class Path {
    private final List<Station> stations;
    private final int totalDistance;

    public Path(List<Station> stations, int totalDistance) {
        this.stations = stations;
        this.totalDistance = totalDistance;
    }

    public List<Station> getStations() {
        return stations;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    @Override
    public String toString() {
        return "Path{" + stations + ", distance=" + totalDistance + '}';
    }
}
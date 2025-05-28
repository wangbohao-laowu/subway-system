import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Line {
    private String name = "";
    private final List<String> stations = new ArrayList<>();
    private final List<Double> distances = new ArrayList<>();


    public Line(String name) {
		this.name=name;
	}

	public void addStationPair(String stationA,String stationB,double distance) {
        if(stations.isEmpty()) {
    	stations.add(stationA);
    	stations.add(stationB);
        }else {
        	stations.add(stationB);
        }
        distances.add(distance);
    }
    
    public String getName() {
        return this.name;
    }

    public List<String> getStations() {
        return Collections.unmodifiableList(this.stations);
    }
    public List<Double> getDistances() {
        return Collections.unmodifiableList(this.distances);
    }
    public double getTotalDistance() {
    	return this.distances.stream().mapToDouble(Double::doubleValue).sum();
    }
}
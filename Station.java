import java.util.*;

public class Station {
    private String name;
    private Set<String> lines = new HashSet<>();

    public Station(String name) {
        this.name = name;
    }

    public void addLine(String line) {
        lines.add(line);
    }

    public String getName() { return name; }
    public Set<String> getLines() { return lines; }
}
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TMS {
    public static void main(String[] args) {
        SubwaySystem subway = new SubwaySystem();
        
        try (BufferedReader br = new BufferedReader(new FileReader("subway.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length != 4) continue;
                subway.addLineSegment(
                    parts[0], 
                    parts[1], 
                    parts[2], 
                    Integer.parseInt(parts[3])
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 功能测试示例
        testTransferStations(subway);
        testNearbyStations(s

// 功能测试示例
        testTransferStations(subway);
        testNearbyStations(subway, "华中科技大学站", 2);
        testPathFinding(subway, "站点A", "站点C");
    }

    private static void testTransferStations(SubwaySystem subway) {
        System.out.println("=== 中转站 ===");
        subway.getTransferStations().forEach(s -> 
            System.out.println(s.getName() + " <" + s.getLines() + ">")
        );
    }

    private static void testNearbyStations(SubwaySystem subway, String stationName, int n) {
        System.out.println("\n=== 附近站点 ===");
        Station station = subway.getStationByName(stationName);
        if (station == null) return;
        subway.findNearbyStations(station, n).forEach((k, v) -> 
            System.out.println(k.getName() + " " + v + "km")
        );
    }

    private static void testPathFinding(SubwaySystem subway, String start, String end) {
        System.out.println("\n=== 路径查找 ===");
        Station s = subway.getStationByName(start);
        Station e = subway.getStationByName(end);
        if (s == null || e == null) return;

        Path path = subway.findShortestPath(s, e);
        System.out.println("最短路径: " + path);
        System.out.println("格式化路径: " + formatPath(path));
        System.out.println("票价: " + Pricing.calculateFare(path.getTotalDistance(), Pricing.TicketType.NORMAL));
    }

    private static String formatPath(Path path) {
        StringBuilder sb = new StringBuilder();
        String currentLine = null;
        Station prev = null;

        for (Station station : path.getStations()) {
            if (prev == null) {
                prev = station;
                continue;
            }

            Set<String> common = new HashSet<>(prev.getLines());
            common.retainAll(station.getLines());
            String line = common.iterator().next();

            if (currentLine == null) {
                currentLine = line;
                sb.append(line).append("从").append(prev.getName()).append("到");
            } else if (!line.equals(currentLine)) {
                sb.append(prev.getName()).append("换乘").append(line).append("到");
                currentLine = line;
            }
            prev = station;
        }
        sb.append(prev.getName());
        return sb.toString();
    }
}
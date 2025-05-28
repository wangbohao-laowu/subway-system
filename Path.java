import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 封装地铁路径信息，包含站点序列和总距离
 */
public class Path {
    private final List<String> stations;  // 路径站点序列（起点->...->终点）
    private final double totalDistance;   // 路径总距离（公里）
    
    public Path(List<String> stations, double totalDistance) {
        this.stations = Collections.unmodifiableList(stations);
        this.totalDistance = totalDistance;
    }
    
    // ============== 核心访问方法 ==============
    
    /**
     * 获取路径总距离（公里）
     */
    public double getTotalDistance() {
        return totalDistance;
    }
    
    /**
     * 获取路径站点序列（不可修改）
     */
    public List<String> getStations() {
        return stations;
    }
    
    /**
     * 获取路径中的站点数量
     */
    public int getStationCount() {
        return stations.size();
    }
    
    // ============== 路径展示功能 ==============
    
    /**
     * 格式化输出路径信息
     * 示例：A站 → B站 → C站 (总距离: 5.2km)
     */
    @Override
    public String toString() {
        if (stations.isEmpty()) return "空路径";
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stations.size(); i++) {
            if (i > 0) sb.append(" → ");
            sb.append(stations.get(i));
        }
        return sb.append(String.format(" (总距离: %.2fkm)", totalDistance)).toString();
    }
    
    /**
     * 生成换乘提示信息
     * 示例： 
     *  乘坐1号线：从A站到B站（共3站）
     *  在B站换乘2号线
     *  乘坐2号线：从B站到D站（共5站）
     */
    public String getTransferInstructions(SubwaySystem subway) {
        if (stations.size() < 2) return "无需乘车";
        
        StringBuilder instructions = new StringBuilder();
        String currentLine = getLineBetween(stations.get(0), stations.get(1), subway);
        int segmentStart = 0;
        
        for (int i = 1; i < stations.size(); i++) {
            String from = stations.get(i-1);
            String to = stations.get(i);
            String line = getLineBetween(from, to, subway);
            
            // 检测换乘点
            if (!line.equals(currentLine)) {
                // 输出当前区段
                addSegmentInstruction(instructions, currentLine, segmentStart, i-1);
                
                // 添加换乘提示
                instructions.append("在")
                          .append(stations.get(i-1))
                          .append("站换乘")
                          .append(line)
                          .append("\n");
                
                // 开始新区段
                currentLine = line;
                segmentStart = i-1;
            }
        }
        
        // 添加最后一段
        addSegmentInstruction(instructions, currentLine, segmentStart, stations.size()-1);
        
        return instructions.toString();
    }
    
    // ============== 辅助方法 ==============
    
    // 添加区段信息
    private void addSegmentInstruction(StringBuilder sb, String line, int start, int end) {
        int stationCount = end - start;
        sb.append("乘坐").append(line).append("：")
          .append("从").append(stations.get(start)).append("站")
          .append("到").append(stations.get(end)).append("站");
        
        if (stationCount > 1) {
            sb.append("（共").append(stationCount).append("站）");
        }
        sb.append("\n");
    }
    
    // 获取两站之间的线路
    private String getLineBetween(String stationA, String stationB, SubwaySystem subway) {
        Station a = subway.getStation(stationA);
        Station b = subway.getStation(stationB);
        
        if (a == null || b == null) return "未知线路";
        
        // 取两站共有的第一条线路
        Set<String> commonLines = new HashSet<>(a.getLines());
        commonLines.retainAll(b.getLines());
        
        return commonLines.isEmpty() ? "未知线路" : commonLines.iterator().next();
    }
    
    /**
     * 获取换乘次数
     */
    public int getTransferCount(SubwaySystem subway) {
        if (stations.size() < 3) return 0;
        
        int transfers = 0;
        String currentLine = getLineBetween(stations.get(0), stations.get(1), subway);
        
        for (int i = 2; i < stations.size(); i++) {
            String line = getLineBetween(stations.get(i-1), stations.get(i), subway);
            if (!line.equals(currentLine)) {
                transfers++;
                currentLine = line;
            }
        }
        
        return transfers;
    }
}
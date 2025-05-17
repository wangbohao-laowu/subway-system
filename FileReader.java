import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SubwayFileReader {
    
    /**
     * 从文件加载地铁线路数据到系统
     * @param system 地铁系统实例
     * @param filePath 数据文件路径
     * @throws IOException 文件读取失败时抛出
     * @throws DataFormatException 数据格式错误时抛出
     */
    public static void loadSubwayData(SubwaySystem system, String filePath) 
        throws IOException, DataFormatException {
        
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(filePath))) {
            String line;
            int lineNumber = 0;
            
            while ((line = br.readLine()) != null) {
                lineNumber++;
                processLine(system, line, lineNumber);
            }
        }
    }

    private static void processLine(SubwaySystem system, String line, int lineNumber) 
        throws DataFormatException {
        
        // 跳过空行和注释行
        if (line.trim().isEmpty() || line.startsWith("#")) return;
        
        String[] parts = line.split("\\s+");
        if (parts.length != 4) {
            throw new DataFormatException("格式错误 (行 " + lineNumber + "): " + line);
        }
        
        try {
            String lineName = parts[0];
            String stationA = parts[1];
            String stationB = parts[2];
            double distance = Double.parseDouble(parts[3]);
            
            system.addLineSegment(lineName, stationA, stationB, distance);
        } catch (NumberFormatException e) {
            throw new DataFormatException("距离值非法 (行 " + lineNumber + "): " + line);
        }
    }

    /**
     * 自定义数据格式异常类
     */
    public static class DataFormatException extends Exception {
        public DataFormatException(String message) {
            super(message);
        }
    }
}
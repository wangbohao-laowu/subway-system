import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * 武汉地铁系统测试类
 * 用于验证系统各项功能
 */
public class Test {
    private static SubwaySystem subway;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("===== 武汉地铁模拟系统 =====");
        
        try {
            // 初始化地铁系统
            initSubwaySystem();
            
            // 主菜单
            while (true) {
                printMainMenu();
                int choice = readIntInput("请选择功能: ");
                
                switch (choice) {
                    case 1 -> testTransferStations();
                    case 2 -> testNearbyStations();
                    case 3 -> testFindAllPaths();
                    case 4 -> testShortestPath();
                    case 5 -> testPathPresentation();
                    case 6 -> testFareCalculation();
                    case 7 -> testPeriodicTicket();
                    case 0 -> {
                        System.out.println("感谢使用武汉地铁模拟系统，再见！");
                        return;
                    }
                    default -> System.out.println("无效选项，请重新选择！");
                }
            }
        } catch (IOException e) {
            System.err.println("系统初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== 初始化方法 ====================
    
    private static void initSubwaySystem() throws IOException {
        System.out.println("正在加载地铁数据...");
        subway = new SubwaySystem();
        subway.parseSubwayFile("subway.txt");
        System.out.println("地铁数据加载完成！");
        System.out.println("共加载 " + subway.getTransferStations().size() + " 个换乘站");
    }

    // ==================== 测试方法 ====================
    
    /**
     * 测试功能1：识别所有中转站
     */
    private static void testTransferStations() {
        System.out.println("\n===== 测试：识别所有中转站 =====");
        
        List<SubwaySystem.TransferStation> transfers = subway.getTransferStations();
        System.out.println("发现 " + transfers.size() + " 个中转站:");
        
        // 分页显示结果
        int pageSize = 10;
        int pageCount = (transfers.size() + pageSize - 1) / pageSize;
        
        for (int page = 0; page < pageCount; page++) {
            System.out.println("\n--- 第 " + (page+1) + " 页 / 共 " + pageCount + " 页 ---");
            int start = page * pageSize;
            int end = Math.min(start + pageSize, transfers.size());
            
            for (int i = start; i < end; i++) {
                SubwaySystem.TransferStation ts = transfers.get(i);
                System.out.printf("%2d. %s: %s%n", 
                                 i+1, ts.getName(), ts.getLines());
            }
            
            if (page < pageCount - 1) {
                System.out.print("按回车键继续...");
                scanner.nextLine();
            }
        }
        
        System.out.println("\n中转站查询完成！");
    }
    
    /**
     * 测试功能2：查找邻近站点
     */
    private static void testNearbyStations() {
        System.out.println("\n===== 测试：查找邻近站点 =====");
        
        String station = readStationInput("请输入站点名称: ");
        double distance = readDoubleInput("请输入最大距离(km): ");
        
        try {
            List<SubwaySystem.NearbyStation> nearby = 
                subway.findNearbyStations(station, distance);
            
            if (nearby.isEmpty()) {
                System.out.println("在 " + distance + "km 范围内未找到邻近站点");
                return;
            }
            
            System.out.println("找到 " + nearby.size() + " 个邻近站点:");
            System.out.println("站点名称\t\t线路\t\t距离");
            System.out.println("------------------------------------");
            
            for (SubwaySystem.NearbyStation ns : nearby) {
                System.out.printf("%-12s\t%-10s\t%.3fkm%n", 
                                 ns.getName(), ns.getLine(), ns.getDistance());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("错误: " + e.getMessage());
        }
    }
    
    /**
     * 测试功能3：查找所有路径
     */
    private static void testFindAllPaths() {
        System.out.println("\n===== 测试：查找所有路径（无重复站点） =====");
        System.out.println("注意：此功能可能耗时较长，建议测试短路径");
        
        String start = readStationInput("请输入起点站: ");
        String end = readStationInput("请输入终点站: ");
        
        try {
            List<Path> paths = subway.findAllPaths(start, end);
System.out.println("找到 " + paths.size() + " 条路径:");
            
            // 显示前3条路径详情
            int maxShow = Math.min(3, paths.size());
            for (int i = 0; i < maxShow; i++) {
                System.out.printf("\n路径 %d (%.2fkm):%n", i+1, paths.get(i).getTotalDistance());
                System.out.println(paths.get(i));
            }
            
            if (paths.size() > maxShow) {
                System.out.printf("\n... 省略 %d 条路径%n", paths.size() - maxShow);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("错误: " + e.getMessage());
        }
    }
    
    /**
     * 测试功能4：查找最短路径
     */
    private static void testShortestPath() {
        System.out.println("\n===== 测试：查找最短路径 =====");
        
        String start = readStationInput("请输入起点站: ");
        String end = readStationInput("请输入终点站: ");
        
        try {
            Path path = subway.findShortestPath(start, end);
            System.out.println("最短路径 (总距离: " + path.getTotalDistance() + "km):");
            System.out.println(path);
            
            // 显示换乘次数
            int transfers = path.getTransferCount(subway);
            System.out.println("换乘次数: " + transfers);
        } catch (IllegalArgumentException e) {
            System.out.println("错误: " + e.getMessage());
        }
    }
    
    /**
     * 测试功能5：路径展示
     */
    private static void testPathPresentation() {
        System.out.println("\n===== 测试：路径展示 =====");
        
        String start = readStationInput("请输入起点站: ");
        String end = readStationInput("请输入终点站: ");
        
        try {
            Path path = subway.findShortestPath(start, end);
            System.out.println("\n=== 简洁路径展示 ===");
            System.out.println(path.getTransferInstructions(subway));
            
            System.out.println("\n=== 详细路径信息 ===");
            System.out.println("起点: " + start);
            System.out.println("终点: " + end);
            System.out.println("总距离: " + path.getTotalDistance() + "km");
            System.out.println("站点数: " + path.getStationCount());
            System.out.println("换乘次数: " + path.getTransferCount(subway));
            System.out.println("路径详情: " + path);
        } catch (IllegalArgumentException e) {
            System.out.println("错误: " + e.getMessage());
        }
    }
    
    /**
     * 测试功能6：票价计算
     */
    private static void testFareCalculation() {
        System.out.println("\n===== 测试：票价计算 =====");
        
        String start = readStationInput("请输入起点站: ");
        String end = readStationInput("请输入终点站: ");
        
        try {
            Path path = subway.findShortestPath(start, end);
            double distance = path.getTotalDistance();
            
            // 计算各种票价
            double normalFare = Pricing.calculateNormalFare(distance);
            double wuhanPassFare = Pricing.calculateWuhanPassFare(distance);
            
            System.out.println("\n路线信息:");
            System.out.println(path.getTransferInstructions(subway));
            System.out.printf("总距离: %.2fkm\n", distance);
            
            System.out.println("\n票价信息:");
            System.out.println("普通单程票: " + Pricing.formatFare(normalFare));
            System.out.println("武汉通(9折): " + Pricing.formatFare(wuhanPassFare));
        } catch (IllegalArgumentException e) {
            System.out.println("错误: " + e.getMessage());
        }
    }
    
    /**
     * 测试功能7：定期票计算
     */
    private static void testPeriodicTicket() {
        System.out.println("\n===== 测试：定期票推荐 =====");
        
        int days = readIntInput("请输入出行天数: ");
        int dailyRides = readIntInput("请输入每日乘车次数: ");
        
        // 计算总乘车次数
        int totalRides = days * dailyRides;
        if (totalRides <= 0) {
            System.out.println("无效的出行计划");
            return;
        }
        
        // 获取推荐票种
        Pricing.PeriodicTicket recommended = 
            Pricing.recommendPeriodicTicket(dailyRides, days);
        
        System.out.println("\n出行计划分析:");
        System.out.println("出行天数: " + days);
        System.out.println("每日乘车次数: " + dailyRides);
        System.out.println("总乘车次数: " + totalRides);
        
        if (recommended == null) {
            System.out.println("\n推荐方案: 购买普通单程票更划算");
            return;
        }
        
        // 计算各种票的费用
        double normalTotalFare = totalRides * 4.0; // 假设平均每次4元
        double periodicTicketCost = recommended.getPrice();
        double saving = normalTotalFare - periodicTicketCost;
        
        System.out.println("\n推荐方案: 购买" + getTicketName(recommended) + "定期票");
        System.out.println("定期票价格: " + Pricing.formatFare(periodicTicketCost));
        System.out.println("预计普通票费用: " + Pricing.formatFare(normalTotalFare));
        System.out.printf("预计节省: %s (%.1f%%)%n", 
                         Pricing.formatFare(saving), 
                         (saving / normalTotalFare) * 100);
        
        // 显示所有定期票选项
        System.out.println("\n定期票选项对比:");
        System.out.println("票种\t\t价格\t\t单次费用(按" + totalRides + "次计算)");
        System.out.println("------------------------------------------------");
        
        for (Pricing.PeriodicTicket ticket : Pricing.PeriodicTicket.values()) {
            double perRideCost = Pricing.calculatePeriodicTicketFare(ticket, totalRides);
            System.out.printf("%-10s\t%-10s\t%s%n", 
                             getTicketName(ticket),
                             Pricing.formatFare(ticket.getPrice()),
                             Pricing.formatFare(perRideCost));
        }
    }
    
    // ==================== 辅助方法 ====================
    
    private static void printMainMenu() {
        System.out.println("\n========== 主菜单 ==========");
        System.out.println("1. 查询所有中转站");
        System.out.println("2. 查找邻近站点");
        System.out.println("3. 查找所有路径");
        System.out.println("4. 查找最短路径");
        System.out.println("5. 路径展示");
        System.out.println("6. 票价计算");
        System.out.println("7. 定期票推荐");
        System.out.println("0. 退出系统");
        System.out.println("============================");
    }
    
    private static String readStationInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    private static int readIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("输入无效，请输入整数！");
            }
        }
    }
    
    private static double readDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("输入无效，请输入数字！");
            }
        }
    }
    
    private static String getTicketName(Pricing.PeriodicTicket ticket) {
        switch (ticket) {
            case ONE_DAY: return "1日";
            case THREE_DAYS: return "3日";
            case SEVEN_DAYS: return "7日";
            default: return "未知";
        }
    }
}










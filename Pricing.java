/**
 * 地铁票价计算器
 * 实现武汉地铁的计价规则和优惠方案
 */
public class Pricing {
    // 武汉地铁计价规则
    private static final double BASE_FARE = 2.0;           // 起步价
    
    private static final int FIRST_STAGE_LIMIT = 4;        // 第一阶段下限（公里）
    private static final int SECOND_STAGE_LIMIT = 12;      // 第二阶段下限（公里）
    private static final int THIRD_STAGE_LIMIT = 24;       // 第三阶段下限（公里）
    private static final int FOURTH_STAGE_LIMIT = 40;       // 第四阶段下限（公里）
    private static final int FIFTH_STAGE_LIMIT = 50;       // 第五阶段下限（公里）
    
    private static final double FIRST_STAGE_RATE = 1.0;    // 第一阶段每4公里加1元
    private static final double SECOND_STAGE_RATE = 1.0;   // 第二阶段每6公里加1元
    private static final double THIRD_STAGE_RATE = 1.0;    // 第三阶段每8公里加1元
    private static final double FOURTH_STAGE_RATE = 1.0;    // 第四阶段每10公里加1元
    private static final double FIFTH_STAGE_RATE = 1.0;    // 第五阶段每20公里加1元
    
    
    private static final double WUHAN_PASS_DISCOUNT = 0.9; // 武汉通折扣（9折）
    
    // 定期票价格
    public enum PeriodicTicket {
        ONE_DAY(18.0),
        THREE_DAYS(45.0),
        SEVEN_DAYS(90.0);
        
        private final double price;
        
        PeriodicTicket(double price) {
            this.price = price;
        }
        
        public double getPrice() {
            return price;
        }
    }

    /**
     * 计算单程票价（普通票）
     * @param distance 乘车距离（公里）
     * @return 票价（元）
     */
    public static double calculateNormalFare(double distance) {
        if (distance <= 0) return 0;
        
        double fare = BASE_FARE;
        
        // 第一阶段：4-12公里部分
        if (distance > FIRST_STAGE_LIMIT) {
            double section = Math.min(distance, SECOND_STAGE_LIMIT) - FIRST_STAGE_LIMIT;
            fare += Math.ceil(section / 4) * FIRST_STAGE_RATE;
        }
        
        // 第二阶段：12-24公里部分
        if (distance > SECOND_STAGE_LIMIT) {
            double section = Math.min(distance, THIRD_STAGE_LIMIT) - SECOND_STAGE_LIMIT;
            fare += Math.ceil(section / 6) * SECOND_STAGE_RATE;
        }
        
        // 第三阶段：24公里以上部分
        if (distance > THIRD_STAGE_LIMIT) {
            double section = distance - THIRD_STAGE_LIMIT;
            fare += Math.ceil(section / 8) * THIRD_STAGE_RATE;
        }
        
        // 第四阶段：40公里以上部分
        if (distance > FOURTH_STAGE_LIMIT) {
            double section = distance - FOURTH_STAGE_LIMIT;
            fare += Math.ceil(section / 10) * FOURTH_STAGE_RATE;
        }
        
       // 第五阶段：50公里以上部分
        if (distance > FIFTH_STAGE_LIMIT) {
            double section = distance - FIFTH_STAGE_LIMIT;
            fare += Math.ceil(section / 20) * FIFTH_STAGE_RATE;
        }
        
        return fare;
    }

    /**
     * 计算武汉通优惠票价
     * @param distance 乘车距离（公里）
     * @return 优惠后票价（元）
     */
    public static double calculateWuhanPassFare(double distance) {
        return calculateNormalFare(distance) * WUHAN_PASS_DISCOUNT;
    }

    /**
     * 计算定期票单次费用
     * @param ticketType 定期票类型
     * @param rideCount 乘车次数
     * @return 单次平均费用（元）
     */
    public static double calculatePeriodicTicketFare(PeriodicTicket ticketType, int rideCount) {
        if (rideCount <= 0) return ticketType.getPrice(); // 至少按一次计算
        return ticketType.getPrice() / rideCount;
    }

    /**
     * 判断定期票是否划算
     * @param dailyRides 每日乘车次数
     * @param days 出行天数
     * @return 推荐的定期票类型（null表示普通票更划算）
     */
    public static PeriodicTicket recommendPeriodicTicket(int dailyRides, int days) {
        int totalRides = dailyRides * days;
        if (totalRides == 0) return null;
        
        // 计算普通票总费用（假设平均每次4元）
        double normalTotalFare = totalRides * 4;
        
        // 计算各种定期票的日均成本
        double oneDayCost = PeriodicTicket.ONE_DAY.getPrice() / days;
        double threeDaysCost = PeriodicTicket.THREE_DAYS.getPrice() / days;
        double sevenDaysCost = PeriodicTicket.SEVEN_DAYS.getPrice() / days;
        
        // 找出最经济的定期票
        PeriodicTicket bestTicket = null;
        double minCost = Double.MAX_VALUE;
        
        if (oneDayCost < minCost && oneDayCost < normalTotalFare) {
            minCost = oneDayCost;
            bestTicket = PeriodicTicket.ONE_DAY;
        }
        
        if (threeDaysCost < minCost && threeDaysCost < normalTotalFare) {
            minCost = threeDaysCost;
            bestTicket = PeriodicTicket.THREE_DAYS;
        }
        
        if (sevenDaysCost < minCost && sevenDaysCost < normalTotalFare) {
            bestTicket = PeriodicTicket.SEVEN_DAYS;
        }
        
        return bestTicket;
    }

    /**
     * 格式化票价输出
     * @param fare 票价
     * @return 格式化的价格字符串（如：¥5.00）
     */
    public static String formatFare(double fare) {
        return String.format("¥%.2f", fare);
    }
}
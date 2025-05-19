public class Pricing {
    public enum TicketType { NORMAL, WUHAN_TONG, DAY_1, DAY_3, DAY_7 }

    public static double calculateFare(int distance, TicketType type) {
        double base = calculateBaseFare(distance);
        switch (type) {
            case WUHAN_TONG: return base * 0.9;
            case DAY_1: return 18;
            case DAY_3: return 45;
            case DAY_7: return 90;
            default: return base;
        }
    }

    private static double calculateBaseFare(int distance) {
        if (distance <= 6) return 3;
        else if (distance <= 12) return 4;
        else if (distance <= 22) return 5;
        else return 5 + Math.ceil((distance - 22) / 10.0);
    }
}
package web.usc.edu.searchnearbyapp.util;

public class DrawnableUtil {
    public static String repeatSymbol(int number, String symbol) {
        String dollar = "";
        for (int i =  0; i < number; i++) {
            dollar += symbol;
        }

        return dollar;
    }
}

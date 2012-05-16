package models;

import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;

public class KTLCUtils {
    public static Pattern FAST_LOG_PATTERN_RACE = Pattern.compile("KTLC .* on \\[(.+)\\] \\((.+),(.+),(.+)\\)");
    public static Pattern FAST_LOG_PATTERN_PLACE = Pattern.compile("([0-9]*),([0-9]*),.*,.*,.*,(.*),(.*)");
    
    private static int[] rankPoints = { 30, 25, 21, 18, 16, 14, 12, 10, 8, 7,
            6, 5, 4, 3, 2, 1 };

    public static int getRankPoints(int rank) {
        assert rank > 0;
        if (rank > rankPoints.length) {
            return 0;
        } else {
            return rankPoints[rank - 1];
        }
    }
}

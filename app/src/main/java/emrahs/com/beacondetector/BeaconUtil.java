package emrahs.com.beacondetector;

/**
 * Created by emrahsoytekin on 9.12.2017.
 */

public final class BeaconUtil {

    public static String UNKNOWN = "Unknown";
    public static String IMMEDIATE = "Immediate";
    public static String CLOSE = "Very Close";
    public static String NEAR = "Near";
    public static String FAR = "Far";


    public static String getDistance(double accuracy) {
        if (accuracy == -1.0) {
            return UNKNOWN;
        } else if(accuracy < .3) {
            return IMMEDIATE;
        }else if (accuracy < 1) {
            return CLOSE;
        } else if (accuracy < 3) {
            return NEAR;
        } else {
            return FAR;
        }
    }
}

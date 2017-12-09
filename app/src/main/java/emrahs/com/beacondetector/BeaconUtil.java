package emrahs.com.beacondetector;

/**
 * Created by emrahsoytekin on 9.12.2017.
 */

public final class BeaconUtil {



    public static Status getDistance(double accuracy) {
        if (accuracy == -1.0) {
            return Status.UNKNOWN;
        } else if(accuracy < .3) {
            return Status.IMMEDIATE;
        }else if (accuracy < 1) {
            return Status.CLOSE;
        } else if (accuracy < 3) {
            return Status.NEAR;
        } else {
            return Status.FAR;
        }
    }
}

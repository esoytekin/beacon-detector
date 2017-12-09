package emrahs.com.beacondetector;

/**
 * Created by emrahsoytekin on 9.12.2017.
 */

public enum Status {
    STARTED("Started"), STOPPED("Stopped"), UNKNOWN("Unknown"), IMMEDIATE("Immediate"), CLOSE("Close"), NEAR("Near"), FAR("Far");

    private String label;

    Status(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

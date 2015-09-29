package onyxmotion.basketballcoach;

/**
 * Constants and Flags used by the mobile app
 * Created by Vivek on 2014-07-02.
 */
public class Const {
    public static final String
        MOBILE = "mobile",
        WEAR = "watch",
        f = "/",
        t = ",",
        n = "\n",
        DEV = "device",
        NUM = "index",
        TIM = "timestamp_nano",
        ACC = "accelerometer",
        ROT = "rotation",
        GYR = "gyroscope",
        REC = "records",
        PATH_RECORD = "/sensor/record/",
        PATH_RECORDS = "/sensor/records/",
        PATH_ACTIVITY = "/activity",
	    PATH_CONNECTED = "/connected",
        PATH_ON = "/sensor/on";

    public static final int
        RATE = 5000,
        NUMD = 100;

    public static final String DEVICE = MOBILE;

    private Const() {

    }
}

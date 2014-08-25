package onyxmotion.onyx;

import com.google.android.gms.wearable.DataMap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A point in recorded motion data
 * Created by Vivek on 2014-07-04.
 */
public class RecordData {
    private static ArrayList<RecordData> wearRec = new ArrayList<RecordData>(),
        mobileRec = new ArrayList<RecordData>();

    public interface RecordCallable {
        public void call(RecordData record);
    }


    private DataMap dm;

    public static boolean delete(RecordData record) {
        ArrayList<RecordData> records
            = record.device().equals(Const.WEAR) ? wearRec : mobileRec;
        for(RecordData rec : records)
            if (rec.device().equals(record.device())
                && rec.index() == record.index())
                return records.remove(rec);
        return false;
    }

    public static void add(RecordData record) {
        ArrayList<RecordData> records
            = record.device().equals(Const.WEAR) ? wearRec : mobileRec;
        for(RecordData rec : records)
            if (rec.device().equals(record.device())
                && rec.index() == record.index())
                records.remove(rec);
        records.add(record);
    }

    public static ArrayList<RecordData> all(String device) {
        return device.equals(Const.WEAR) ? wearRec : mobileRec;
    }

    public RecordData(DataMap dataMap, boolean toAdd) {
        dm = dataMap;
        if (toAdd) add(this);
        else delete(this);
    }

    public RecordData(String device, int index, long time,
        float[] acc, float[] rot, float[]gyr) {
        dm = new DataMap();
        dm.putString(Const.DEV, device);
        dm.putInt(Const.NUM, index);
        dm.putLong(Const.TIM, time);
        dm.putFloatArray(Const.ACC, acc);
        dm.putFloatArray(Const.ROT, rot);
        dm.putFloatArray(Const.GYR, gyr);
        add(this);
    }

    public DataMap data() {
        return dm;
    }

    public String device() {
        return dm.getString(Const.DEV);
    }

    public int index() {
        return dm.getInt(Const.NUM);
    }

    public long time() {
        return dm.getLong(Const.TIM);
    }

    public String ftime() {
        return SimpleDateFormat.getDateTimeInstance()
            .format(new Date(time() / 1000000L));
    }

    public float[] acc() {
        return dm.getFloatArray(Const.ACC);
    }

    public float[] rot() {
        return dm.getFloatArray(Const.ROT);
    }

    public float[] gyr() {
        return dm.getFloatArray(Const.GYR);
    }

}
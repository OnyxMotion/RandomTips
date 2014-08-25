package onyxmotion.onyx;

import android.os.Environment;
import android.util.Log;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;

/**
 * Handles writing to file
 * Created by Vivek on 2014-07-02.
 */
public class FileHandler {

    private static final String TAG = "FileHandler", f = Const.f, t = Const.t;

    private static FileHandler[] fileHandler = {null, null};

    private PrintWriter writer;

    private String file, device;

    private boolean isInitialized = false;

    public static FileHandler getHandler(String d) {
        int i = d.equals(Const.MOBILE) ? 0 : 1;
        if (fileHandler[i] == null) fileHandler[i] = new FileHandler(d);
        return fileHandler[i];
    }

    public static String filenameDefault(String device) {
        return "OnyxData" + device + new SimpleDateFormat("yyMMddHHmmss")
            .format(System.currentTimeMillis()) + ".txt";
    }

    private FileHandler(String d) { device = d; }

    public void initialize(String filename) {
	    if (isInitialized) return;
        file = filename;
	    Log.d(TAG, "Initializing: " + file);
        try {
            writer = new PrintWriter(
                Environment.getExternalStorageDirectory().getAbsolutePath()
                + f + filename, "UTF-8");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing file " + file);
        }
        isInitialized = true;
    }

    public void close() {
        if (!isInitialized) return;
        writer.flush();
        writer.close();
        if (writer.checkError())
            Log.e(TAG, "Error in closing writer " + file);
        isInitialized = false;
    }

    public void printData(RecordData rec) {
        float[] aData = rec.acc(), rData = rec.rot(), gData = rec.gyr();
        println("#N-=" + rec.device() + t + rec.index() + t + rec.time());
        println("#A-=" + aData[0] + t + aData[1] + t + aData[2]);
        println("#R-=" + rData[0] + t + rData[1] + t + rData[2]
            + t + rData[3] + t + rData[4]);
        println("#G-=" + gData[0] + t + gData[1] + t + gData[2]);
	    Log.d(TAG, "Printing record " + rec.device() + t + rec.index());
    }

    public void print(String text) {
        Log.d(TAG,"print: " + text);
        if (isInitialized)
            writer.print(text);
    }

    public void println(String text) {
        if (!isInitialized) return;
    //    Log.d(TAG,"println: " + text);
        writer.println(text);

    }
}

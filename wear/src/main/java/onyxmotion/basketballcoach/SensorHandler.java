package onyxmotion.basketballcoach;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Handles all sensor initialization and events
 * Created by Vivek on 2014-06-30.
 *
 */
public class SensorHandler implements SensorEventListener {

    private static SensorHandler sensorHandler;

    private SensorManager sensorManager;
    private Sensor aSensor, rSensor, gSensor;
    private boolean isInitialized = false;
    private RecordData.RecordCallable toCall;
    private float[] currA, currR, currG;
    private int numData = 0;
    public boolean isRecordData = false;

    public static SensorHandler getHandler() {
        if (sensorHandler == null) sensorHandler = new SensorHandler();
        return sensorHandler;
    }

    private SensorHandler() {

    }

    public boolean initialize(Context c, RecordData.RecordCallable onSensor) {
        if (isInitialized) return true;
        toCall = onSensor;

        sensorManager
            = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager == null) return false;
        aSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        rSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        gSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (aSensor == null || rSensor == null || gSensor == null) return false;

        isInitialized = true;
        return true;
    }

    public boolean register() {
        return isInitialized
            && sensorManager.registerListener(this, aSensor, Const.RATE)
            && sensorManager.registerListener(this, rSensor, Const.RATE)
            && sensorManager.registerListener(this, gSensor, Const.RATE);
    }

    public void unregister() {
        if (isInitialized) sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!isRecordData) return;

        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                currA = event.values; break;
            case Sensor.TYPE_ROTATION_VECTOR:
                currR = event.values; break;
            case Sensor.TYPE_GYROSCOPE:
                currG = event.values; break;
            default: break;
        }

        if (currA == null || currR == null || currG == null) return;

        RecordData record = new RecordData(
            Const.DEVICE, numData, event.timestamp, currA, currR, currG);
        onDataComplete(record);
        toCall.call(record);

        currA = currR = currG = null;
        numData++;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do Nothing
    }

    private void onDataComplete(RecordData record) {
        if (RecordData.all(Const.DEVICE).size() % Const.NUMD == 0)
            new Thread(new Runnable() {
	            @Override
	            public void run() {
		            DataHandler.getHandler().addNUMData(Const.DEVICE);
	            }
            }).run();
    }

    public boolean toggleRecord() {
        isRecordData = !isRecordData;
        return isRecordData;
    }
}

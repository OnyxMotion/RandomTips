package onyxmotion.basketballcoach;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Holder extends Activity implements SensorEventListener,
        DataApi.DataListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "WatchActivity";
    private TextView mTextView;
    String t = "\t", n = "\n";
    SensorManager sensorManager;
    Sensor aSensor, gSensor;
    float aData[] = {0,0,0}, gData[] = {0,0,0};
    int numData;
    GoogleApiClient googleApiClient;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);
        final WatchViewStub stub
            = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                updateText();
                Log.d(TAG, "TextView: " + mTextView.getText());
            }
        });

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        aSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        gSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

    }


    @Override
    protected void onResume() {
        super.onResume();
        googleApiClient.connect();
//        sensorManager.registerListener(this, aSensor, 5000);
//        sensorManager.registerListener(this, gSensor, 5000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        googleApiClient.disconnect();
//        sensorManager.unregisterListener(this);
    }

    public void onClick(View button) {
        if (!googleApiClient.isConnected()) return;

        float[] aData = {17.98924f,12.234f,9.43123f},
                rData = {4.12341f,5.1234214f,0.223f,1.234f,0.1005f},
                gData = {17.98924f,12.234f,9.43123f};

        PutDataMapRequest putRequest = PutDataMapRequest
                .create("/watch/sensor/record/" + index);
        DataMap dm = putRequest.getDataMap();
        dm.putString(Const.DEV,Const.WEAR);
        dm.putInt(Const.NUM, index);
        dm.putLong(Const.TIM, 9802314242L);
        dm.putFloatArray(Const.ACC, aData);
        dm.putFloatArray(Const.ROT, rData);
        dm.putFloatArray(Const.GYR, gData);

        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
                .putDataItem(googleApiClient, putRequest.asPutDataRequest());
        Log.d(TAG, "Data sent!");

    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // Many sensors return 3 values, one for each axis.
        float data[] = new float[3];
        System.arraycopy(event.values,0,data,0,3);
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                aData = data; break;
            case Sensor.TYPE_GYROSCOPE:
                gData = data; break;
            default: break;
        }

        updateText();
    }

    public final void updateText() {
        String text = "Watch Data" + t + numData + n
                + "Accel:" + t + aData[0] + t + aData[1] + t + aData[2] + n
                + "Gyros:" + t + gData[0] + t + gData[1] + t + gData[2] + n;
        mTextView.setText(text);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged: " + dataEvents);
        if (!dataEvents.getStatus().isSuccess()) return;
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();

        if(!googleApiClient.isConnected()) {
            ConnectionResult connectionResult = googleApiClient
                    .blockingConnect(30, TimeUnit.SECONDS);
            if (!connectionResult.isSuccess()) {
                Log.e(TAG, "googleApiClient failed to connect to GoogleApiClient.");
                return;
            }
        }
        Log.d(TAG,"Number of events: " + events.size());
        for (DataEvent event : events)
            if (event.getDataItem().getUri().getPath().contains("/sensor/"))
                if (event.getType() == DataEvent.TYPE_CHANGED)
                    handleDataChanged(event);
                else handleDataDeleted(event);
    }

    private void handleDataDeleted(DataEvent event) {

    }

    private void handleDataChanged(DataEvent event) {
        mTextView.setText(event.toString());
        Log.d(TAG, "Handling changed data");
        DataMap dm = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
        if (dm == null) return;
        float aData[] = dm.getFloatArray(Const.ACC),
                gData[] = dm.getFloatArray(Const.GYR);
        String text = "Mobile Data" + t + dm.getInt(Const.NUM) + t +
                SimpleDateFormat.getDateTimeInstance()
                        .format(new Date(dm.getLong(Const.TIM) / 1000000L)) + n
                + "Accel:" + t + aData[0] + t + aData[1] + t + aData[2] + n
                + "Gyros:" + t + gData[0] + t + gData[1] + t + gData[2] + n;
        mTextView.setText(text);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "gapicl connected");
        Wearable.DataApi.addListener(googleApiClient,this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
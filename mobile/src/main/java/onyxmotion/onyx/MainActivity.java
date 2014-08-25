package onyxmotion.onyx;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    String TAG = "MainActivity";
	TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		textView = (TextView) findViewById(R.id.main_text);
        initializeHandlers();

        Log.d(TAG, "MainActivity initialized");
    }

    @Override
    protected void onResume() {
        super.onResume();
        SensorHandler.getHandler().register();
	    DataHandler.getHandler().connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SensorHandler.getHandler().unregister();
	    DataHandler.getHandler().disconnect();
    }                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return item.getItemId() == R.id.action_settings
            || super.onOptionsItemSelected(item);
    }

    private void initializeHandlers() {
        DataHandler.getHandler().initialize(this,
            new RecordData.RecordCallable() {
                @Override
                public void call(RecordData record) {
                    onData(record);
                }
            }
        );
        SensorHandler.getHandler().initialize(this,
            new RecordData.RecordCallable() {
                @Override
                public void call(RecordData record) {
                    onSensor(record);
                }
            }
        );

        FileHandler.getHandler(Const.MOBILE)
            .initialize(FileHandler.filenameDefault(Const.MOBILE));
        FileHandler.getHandler(Const.WEAR)
            .initialize(FileHandler.filenameDefault(Const.WEAR));

    }

    public void onSensor(RecordData record) {

    }

    public void onData(RecordData rec) {
	    String t = Const.t, n = Const.n;
	    float[] aData = rec.acc(), rData = rec.rot(), gData = rec.gyr();
	    textView.setText("#N-=" + rec.device() + t + rec.index() + t + rec.time()
	     + n + "#A-=" + aData[0] + t + aData[1] + t + aData[2]
	     + n + "#R-=" + rData[0] + t + rData[1] + t + rData[2] + t + rData[3] + t + rData[4]
	     + n + "#G-=" + gData[0] + t + gData[1] + t + gData[2]);
/*        DataMap dm = DataHandler.getHandler().currDataMap;
        if (dm == null) return;
        float aData[] = dm.getFloatArray(DataHandler.ACC),
              gData[] = dm.getFloatArray(DataHandler.GYR);
        String text = "Mobile Data" + t + dm.getInt(DataHandler.NUM) + t +
            SimpleDateFormat.getDateTimeInstance()
            .format(new Date(dm.getLong(DataHandler.TSN) / 1000000L)) + n
            + "Accel:" + t + aData[0] + t + aData[1] + t + aData[2] + n
            + "Gyros:" + t + gData[0] + t + gData[1] + t + gData[2] + n;
*/
//        mTextView.setText(DataHandler.getHandler().text);
    }

    public void onClick(View button) {
        ((Button) button).setText("Currently "
            + (SensorHandler.getHandler().toggleRecord() ? "is" : "not")
            + " recording");
    }

	public void startWatch(View button) {
		DataHandler.getHandler().sendStartWatch();
	}
}
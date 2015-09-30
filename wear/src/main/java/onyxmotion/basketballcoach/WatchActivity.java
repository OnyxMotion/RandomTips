package onyxmotion.basketballcoach;


import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Shows events and photo from the Wearable APIs.
 */
public class WatchActivity extends Activity {

    private static final String TAG = "MainActivity";

    private TextView mIntroText;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_watch);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override public void onLayoutInflated(WatchViewStub stub) {
                // Now you can access your views
        mIntroText = (TextView) findViewById(R.id.intro);
            }
        });
        // Stores data events received by the local broadcaster.

        DataHandler.getHandler().initialize(this,
            new RecordData.RecordCallable() {
                @Override
                public void call(RecordData record) {

                }
            });
        /*SensorHandler.getHandler().initialize(this,
            new RecordData.RecordCallable() {
                @Override
                public void call(RecordData record) {

                }
            });
        */
	    new Timer().scheduleAtFixedRate(new TimerTask() {
		    @Override
	        public void run() {
			    runOnUiThread(new Runnable() {
				    @Override
				    public void run() {
                        // Use a random number generator to choose the tip
                        int min = 1, max = 9;
                        Random r = new Random();
                        int randomNum = r.nextInt(max - min + 1) + min;

					    if (randomNum == 2) mIntroText.setText(TIP2);
					    else if (randomNum == 3) mIntroText.setText(TIP3);
					    else if (randomNum == 4) mIntroText.setText(TIP4);
					    else if (randomNum == 5) mIntroText.setText(TIP5);
					    else if (randomNum == 6) mIntroText.setText(TIP6);
					    else if (randomNum == 7) mIntroText.setText(TIP7);
					    else if (randomNum == 8) mIntroText.setText(TIP8);
                        else if (randomNum == 9) mIntroText.setText(TIP9);
					    else if (randomNum == 1) mIntroText.setText(TIP1);
				    }
			    });
		    }
	    }, 5000, 5000);
    }

	public static final String
		TIP1 = "Keep feet a little wider than shoulder-width apart to improve stance",
		TIP2 = "For great posture, tilt your waist, not your hips.",
		TIP3 = "Start your down swing with your lower body then the upper body.",
		TIP4 = "Power in golf comes from your legs, coil and release.",
		TIP5 = "Snap your wrist for the follow through and hold for 3 seconds.",
		TIP6 = "Take a deep breath. Do three dribbles.  Shoot.",
		TIP7 = "You're doing great.",
		TIP8 = "Make sure your shot pocket is high.",
        TIP9 = "Start small, end tall. Bend your knees and spring up on your shot.";

    @Override
    protected void onResume() {
        super.onResume();
        DataHandler.getHandler().connect();
	    SensorHandler.getHandler().register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DataHandler.getHandler().disconnect();
	    SensorHandler.getHandler().unregister();
    }

    public void onClick(View button) {

	    ((Button) button).setText("Smart Tip "
		    + (SensorHandler.getHandler().toggleRecord() ? "ON" : "OFF"));
    }
}

package onyxmotion.onyx;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

public class MessageService extends WearableListenerService {

	private static final String TAG = "MessageService";

	private GoogleApiClient googleApiClient;

	@Override
	public void onCreate() {
		super.onCreate();
		googleApiClient = new GoogleApiClient.Builder(this)
			.addApi(Wearable.API)
			.build();
		googleApiClient.connect();
	}

	@Override
	public void onDestroy() {
		googleApiClient.disconnect();
		super.onDestroy();
	}
/*
	@Override
	public void onDataChanged(DataEventBuffer dataEvents) {
		// Pass through to DataHandler?
	}
*/
	@Override
	public void onMessageReceived(MessageEvent messageEvent) {
		Log.d(TAG, "onMessageReceived: " + messageEvent);

		// Check to see if the message is to start an activity
		if (messageEvent.getPath().equals(Const.PATH_ACTIVITY)) {
			Intent startIntent = new Intent(this, WatchActivity.class);
			startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			PendingIntent pendingIntent = PendingIntent.getActivity(
				this, 0, startIntent, 0);
			Notification.Builder builder = new Notification.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Launch Coach?")
				.setLocalOnly(true)
		//		.setOngoing(true)
				.setPriority(Notification.PRIORITY_MAX)
				.extend(new Notification.WearableExtender()
					.addAction(new Notification.Action(R.drawable.alpha_1_x_1,
						"Launch Activity", pendingIntent))
					.setHintHideIcon(true));



/*			Intent notificationIntent = new Intent(this, ListActivity.class);
			PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

// Create second page notification
			Notification secondPageNotification =
				new Notification.Builder(this)
					.extend(new Notification.WearableExtender()
						.setDisplayIntent(notificationPendingIntent)
						.setCustomSizePreset(Notification.WearableExtender.SIZE_MEDIUM))
					.build();

			Notification twoPageNotification =
				new Notification.WearableExtender()
					.addPage(secondPageNotification)
					.extend(builder)
					.build();
*/

			((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
				.notify(Const.NOTIFICATION_ID, builder.build());
		}
	}

	@Override
	public void onPeerConnected(Node peer) {
		Log.d(TAG, "onPeerConnected: " + peer);
	}

	@Override
	public void onPeerDisconnected(Node peer) {
		Log.d(TAG, "onPeerDisconnected: " + peer);
	}



}

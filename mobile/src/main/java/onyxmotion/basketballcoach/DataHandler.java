package onyxmotion.basketballcoach;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Handles all Wearable API Data layer initialization and events
 * Created by Vivek on 2014-06-30.
 */
public class DataHandler implements GoogleApiClient.OnConnectionFailedListener,
	GoogleApiClient.ConnectionCallbacks, ResultCallback<DataApi.DataItemResult>,
	DataApi.DataListener {

	private static DataHandler dataHandler;

	private final static  String TAG = "DataHandler", f = Const.f, t = Const.t;


	private GoogleApiClient googleApiClient;
	private RecordData.RecordCallable toCall;

	public static DataHandler getHandler() {
		if (dataHandler == null) dataHandler = new DataHandler();
		return dataHandler;
	}

	private DataHandler() {

	}

	public void initialize(Context context, RecordData.RecordCallable onData) {

		toCall = onData;
		googleApiClient = new GoogleApiClient.Builder(context)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this)
			.addApi(Wearable.API)
			.build();
	}

	public void connect() {
		if (!googleApiClient.isConnected()) googleApiClient.connect();
	}

	public void disconnect() {
		Wearable.DataApi.removeListener(googleApiClient,this);
		if (googleApiClient.isConnected()) googleApiClient.disconnect();
	}

	@Override
	public void onConnected(Bundle bundle) {
		Log.d(TAG, "onConnected: " + bundle);
		Wearable.DataApi.addListener(googleApiClient, this);

		sendConnect();
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.d(TAG, "onConnectionSuspended: " + i);
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.d(TAG, "onConnectionFailed: " + connectionResult);
	}

	@Override
	public void onDataChanged(DataEventBuffer dataEvents) {
		Log.d(TAG, "onDataChanged: " + dataEvents);
		if (!dataEvents.getStatus().isSuccess()) return;

		List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
		dataEvents.close();

		if(!googleApiClient.isConnected()) {
			ConnectionResult connectionResult = googleApiClient
				.blockingConnect(30, TimeUnit.SECONDS);
			if (!connectionResult.isSuccess()) {
				Log.e(TAG, "Failed to connect to GoogleApiClient.");
				return;
			}
		}

		for (DataEvent event : events)
			if (event.getDataItem().getUri().getPath().contains(f
				+ (Const.DEVICE.equals(Const.WEAR) ? Const.MOBILE : Const.WEAR)
				+ Const.PATH_RECORDS))
				if (event.getType() == DataEvent.TYPE_CHANGED)
					recordsChanged(event);
				else recordsDeleted(event);
			else if (event.getDataItem().getUri().getPath().contains(
				Const.PATH_CONNECTED))
				toCall.call(null);
	}

	@Override
	public void onResult(DataApi.DataItemResult result) {
		if (result.getStatus().isSuccess())
			Log.d(TAG, "Data item set: " + result.getDataItem().getUri());
		else Log.d(TAG, "Data item failed");
	}

	public void addNUMData(String device) {
		if (!googleApiClient.isConnected()) return;

		ArrayList<DataMap> list = new ArrayList<DataMap>();
		ArrayList<RecordData> records = RecordData.all(device);
		int size = records.size();
		for (int i = size - Const.NUMD; i < size; i++)
			list.add(records.get(i).data());

		PutDataMapRequest putRequest = PutDataMapRequest.create(f + device
			+ Const.PATH_RECORDS + Math.round(size / Const.NUMD - 1.));
		putRequest.getDataMap().putDataMapArrayList(Const.REC, list);

		Wearable.DataApi.putDataItem(googleApiClient,
			putRequest.asPutDataRequest()).setResultCallback(this);
	}

	public void addData(RecordData record) {
		if (!googleApiClient.isConnected()) return;

		PutDataMapRequest putRequest = PutDataMapRequest
			.create(f + record.device() + Const.PATH_RECORD + record.index());
		putRequest.getDataMap().putAll(record.data());

		PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
			.putDataItem(googleApiClient, putRequest.asPutDataRequest());
		pendingResult.setResultCallback(this);
	}

	public void sendConnect() {
		Wearable.DataApi.putDataItem(
			googleApiClient, PutDataRequest.create(Const.PATH_CONNECTED));
	}

	private void recordsChanged(DataEvent event) {
		ArrayList<DataMap> list = DataMapItem.fromDataItem(event.getDataItem())
			.getDataMap().getDataMapArrayList(Const.REC);
		RecordData record = null;

		for (DataMap dm : list) {
			record = new RecordData(dm, true);
            if (Const.DEVICE.equals(Const.MOBILE))
	            FileHandler.getHandler(Const.WEAR).printData(record);
		}

		if (record != null) toCall.call(record);
	}

	private void recordsDeleted(DataEvent event) {
		ArrayList<DataMap> list = DataMapItem.fromDataItem(event.getDataItem())
			.getDataMap().getDataMapArrayList(Const.REC);
		RecordData record;

		for (DataMap dm : list) {
			record = new RecordData(dm, false);
			Log.d(TAG,"Record deleted " + record.device() + t + record.index());
		}
	}

	public void sendStartWatch() {

		Wearable.NodeApi.getConnectedNodes(googleApiClient).setResultCallback(
			new ResultCallback<NodeApi.GetConnectedNodesResult>() {
			@Override
			public void onResult(NodeApi.GetConnectedNodesResult result) {
				for (Node node : result.getNodes())
					Wearable.MessageApi.sendMessage(googleApiClient,
						node.getId(), Const.PATH_ACTIVITY, new byte[0]);
			}
		});


	}
}
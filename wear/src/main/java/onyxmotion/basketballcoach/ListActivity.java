package onyxmotion.basketballcoach;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.view.WearableListView;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class ListActivity extends Activity {

	WearableListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listview = (WearableListView) findViewById(R.id.watch_list);
	    String[] list = {"Record Data", "Golf Drive", "Golf putt"};

	    //= new ArrayAdapter<String>(
		  //  this, android.R.layout.simple_list_item_1, list);
	    listview.setAdapter(new SimpleStringAdapter(this, list));
    }

	public class SimpleStringAdapter extends WearableListView.Adapter {

		private int mBackground;

		private ArrayList<String> values;

		public class ViewHolder extends WearableListView.ViewHolder {

			public TextView textView;

			public ViewHolder(TextView v) {
				super(v);
				textView = v;
			}

			@Override
			public String toString() {
				return super.toString() + " '" + textView.getText();
			}
		}

		public SimpleStringAdapter(Context context, String[] strings) {
			TypedValue val = new TypedValue();
			if (context.getTheme() != null) {
				context.getTheme().resolveAttribute(
					android.R.attr.selectableItemBackground, val, true);
			}
			mBackground = val.resourceId;
			values = new ArrayList<String>();
			Collections.addAll(values, strings);
		}

		@Override
		public SimpleStringAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			final ViewHolder h = new ViewHolder(new TextView(parent.getContext()));
			h.textView.setMinimumHeight(128);
			h.textView.setPadding(20, 0, 20, 0);
			h.textView.setFocusable(true);
			h.textView.setBackgroundResource(mBackground);
			RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
			lp.leftMargin = 10;
			lp.rightMargin = 5;
			lp.topMargin = 20;
			lp.bottomMargin = 15;
			h.textView.setLayoutParams(lp);
			return h;
		}

		@Override
		public void onBindViewHolder(WearableListView.ViewHolder holder, int i) {
			ViewHolder h = (ViewHolder) holder;
			h.textView.setText(values.get(i));
			h.textView.setMinHeight((200 + values.get(i).length() * 10));
		}

		@Override
		public int getItemCount() {
			return values.size();
		}
	}

}

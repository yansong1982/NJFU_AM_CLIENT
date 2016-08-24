package cn.njfu.ams;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChecksViewAdapter extends BaseAdapter {
	LayoutInflater mLayoutInflater;
	private ArrayList<String> mRoomNumArray = new ArrayList<String>();
	private ArrayList<String> mRoomUpdateTimeArray = new ArrayList<String>();
	private ArrayList<String> mRoomScoreArray = new ArrayList<String>();
	private ArrayList<String> mRoomFinishedArray = new ArrayList<String>();
	private String mCheckType;
	private Context mContext;
	private int mStatus;

	public ChecksViewAdapter(Context context, String floor, String checkType,
			int status) {
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		mContext = context;
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mCheckType = checkType;
		mStatus = status;
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor cursor = null;
		String tableName = null;
		if (isCheckTypeSecurity()) {
			tableName = DatabaseHelper.DB_SECURITY_CHECK_TABLE;
		} else {
			tableName = DatabaseHelper.DB_CLEANING_CHECK_TABLE;
		}
		String[] queryKey = new String[] { DatabaseHelper.KEY_ROOM_NUM,
				DatabaseHelper.KEY_ROOM_UPDATE_TIME,
				DatabaseHelper.KEY_ROOM_SCORE, DatabaseHelper.KEY_ROOM_FINISHED };
		if (floor.equals("-1")) {
			cursor = db.query(tableName, queryKey, null, null, null, null,
					null, null);
		} else {
			cursor = db.query(tableName, queryKey,
					DatabaseHelper.KEY_ROOM_FLOOR + "=?",
					new String[] { floor }, null, null, null, null);
		}
		while (cursor != null && cursor.moveToNext()) {
			String roomNum = cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.KEY_ROOM_NUM));
			String roomUpdateTime = cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.KEY_ROOM_UPDATE_TIME));

			String roomScore = null;
			String roomFinished = null;
			roomScore = cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.KEY_ROOM_SCORE));

			roomFinished = cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.KEY_ROOM_FINISHED));

			switch (mStatus) {
			case 0:
			case 1:
				if (roomFinished.equals(Integer.toString(mStatus))) {
					mRoomNumArray.add(roomNum);
					mRoomUpdateTimeArray.add(roomUpdateTime);
					mRoomScoreArray.add(roomScore);
					mRoomFinishedArray.add(roomFinished);
				}
				break;
			default:
				mRoomNumArray.add(roomNum);
				mRoomUpdateTimeArray.add(roomUpdateTime);
				mRoomScoreArray.add(roomScore);
				mRoomFinishedArray.add(roomFinished);

			}
		}
		cursor.close();

		databaseHelper.close();
	}

	public boolean isAllFinished() {
		for (int i = 0; i < mRoomFinishedArray.size(); i++) {
			if (!mRoomFinishedArray.get(i).equals("1")) {
				return false;
			}
		}
		return true;
	}

	public ArrayList<String> getRoomNumArray() {
		return mRoomNumArray;
	}

	public String getRoomNum(int position) {
		return mRoomNumArray.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public int getCount() {
		return mRoomNumArray.size();
	}

	public Object getItem(int position) {
		return mRoomNumArray.get(position);
	}

	private boolean isCheckTypeSecurity() {
		return mCheckType.equals(Utils.CHECK_TYPE_SECURITY);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout linearLayout = (LinearLayout) mLayoutInflater.inflate(
				R.layout.checks_list, null);
		TextView roomTextView = (TextView) linearLayout
				.findViewById(R.id.room_text_view);
		TextView updateTimeTextView = (TextView) linearLayout
				.findViewById(R.id.update_time_text_view);
		TextView scoreTextView = (TextView) linearLayout
				.findViewById(R.id.score_text_view);
		TextView isFinishedTextView = (TextView) linearLayout
				.findViewById(R.id.is_finished_text_view);

		roomTextView.setText(mRoomNumArray.get(position));
		updateTimeTextView.setText(mContext.getResources().getString(
				R.string.update_time_prefix)
				+ mRoomUpdateTimeArray.get(position));
		String score;
		score = mRoomScoreArray.get(position);
		scoreTextView.setText(score
				+ mContext.getResources().getString(R.string.points));
		Log.e("zys", "score = "+score);
		if (!score.isEmpty()&&Integer.parseInt(score) >= 3) {
			scoreTextView.setTextColor(android.graphics.Color.rgb(0x22, 0x8b,
					0x22));
		} else {
			scoreTextView.setTextColor(android.graphics.Color.RED);
		}

		if (!mRoomFinishedArray.get(position).equals("1")) {
			isFinishedTextView.setText(R.string.unfinished);
			isFinishedTextView.setTextColor(android.graphics.Color.LTGRAY);
			isFinishedTextView
					.setBackgroundResource(R.drawable.gray_textview_border);
			scoreTextView.setText("");
		} else {
			isFinishedTextView.setText(R.string.finished);
			isFinishedTextView.setTextColor(android.graphics.Color.BLACK);
			isFinishedTextView
					.setBackgroundResource(R.drawable.textview_border);
		}
		return linearLayout;
	}
}

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
import android.widget.CheckBox;
import android.widget.TextView;

public class ChecksDetailViewAdapter extends BaseAdapter {
	LayoutInflater mLayoutInflater;
	public boolean[] mIsSelectedArray;
	private Context mContext;
	private String mRoomNum;
	private ArrayList<String> mItemArray = new ArrayList<String>();
	private String[] mItemStringArray;
	private String mCheckType;
	private String mRoomScore;
	private String mRoomScoreReason;
	private String mRoomNotes;

	public ChecksDetailViewAdapter(Context context, String roomNum,
			String checkType) {
		mContext = context;
		mRoomNum = roomNum;
		mCheckType = checkType;
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (isCheckTypeSecurity()) {
			mItemStringArray = context.getResources().getStringArray(
					R.array.security_checks_detail_string_array);
		} else {
			mItemStringArray = context.getResources().getStringArray(
					R.array.cleaning_checks_detail_string_array);
		}
		for (int i = 0; i < mItemStringArray.length; i++) {
			mItemArray.add(mItemStringArray[i]);
		}
		init();
	}

	public String getCurrentScore() {
		return mRoomScore;
	}

	public String getNotesContent() {
		return mRoomNotes;
	}

	public void init() {
		DatabaseHelper databaseHelper = new DatabaseHelper(mContext);
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor cursor = null;
		String[] queryKey = new String[] { DatabaseHelper.KEY_ROOM_NUM,
				DatabaseHelper.KEY_ROOM_SCORE,
				DatabaseHelper.KEY_ROOM_SCORE_REASON,
				DatabaseHelper.KEY_ROOM_SCORE_NOTES };

		String tableName = null;

		if (mCheckType.equals(Utils.CHECK_TYPE_SECURITY)) {
			tableName = DatabaseHelper.DB_SECURITY_CHECK_TABLE;
		} else {
			tableName = DatabaseHelper.DB_CLEANING_CHECK_TABLE;
		}
		cursor = db.query(tableName, queryKey, DatabaseHelper.KEY_ROOM_NUM
				+ "=?", new String[] { mRoomNum }, null, null, null, null);
		while (cursor != null && cursor.moveToNext()) {
			mRoomNum = cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.KEY_ROOM_NUM));
			mRoomScore = cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.KEY_ROOM_SCORE));
			mRoomScoreReason = cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.KEY_ROOM_SCORE_REASON));
			mRoomNotes = cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.KEY_ROOM_SCORE_NOTES));
			Log.e("zys", "mRoomNum = " + mRoomNum
					+ ", mRoomSecurityScoreReason = " + mRoomScoreReason);
		}

		cursor.close();
		databaseHelper.close();
		mIsSelectedArray = new boolean[mItemStringArray.length];
		for (int i = 0; i < getCount(); i++) {
			if (mRoomScoreReason != null
					&& mRoomScoreReason.length() == getCount()) {
				Log.e("zys", "mRoomSecurityScoreReason = " + mRoomScoreReason);
				mIsSelectedArray[i] = mRoomScoreReason.substring(i, i + 1)
						.equals("1");
				Log.e("zys", "1-----mIsSelectedArray[" + i + "] = "
						+ mIsSelectedArray[i]);

			} else {
				mIsSelectedArray[i] = false;
				Log.e("zys", "3-----mIsSelectedArray[" + i + "] = "
						+ mIsSelectedArray[i]);
			}
		}
	}

	public String getRoomNum(int position) {
		return mItemArray.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public int getCount() {
		return mItemArray.size();
	}

	public Object getItem(int position) {
		return mItemArray.get(position);
	}

	private boolean isCheckTypeSecurity() {
		return mCheckType.equals(Utils.CHECK_TYPE_SECURITY);
	}

	public View getView(int position, View view, ViewGroup parent) {

		ViewHolder holder = null;
		if (holder == null) {
			holder = new ViewHolder();
			if (view == null) {
				view = mLayoutInflater.inflate(R.layout.checks_detail_list,
						null);
			}
			holder.textView = (TextView) view.findViewById(R.id.rule_text_view);

			holder.checkBox = (CheckBox) view
					.findViewById(R.id.deducting_check_box);
			view.setTag(holder);
		}

		holder.textView.setText(mItemArray.get(position));
		holder.checkBox.setChecked(mIsSelectedArray[position]);

		return view;
	}

}

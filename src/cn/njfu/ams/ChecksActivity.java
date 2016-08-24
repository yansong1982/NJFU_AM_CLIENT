package cn.njfu.ams;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChecksActivity extends Activity {
	private static final int PROGRESS_DISMISS_MSG = 0;
	private static final int PERFORM_BACK_BTN = 1;
	private static final int PERFORM_FLOOR_BTN = 2;
	private static final int PERFORM_STATUS_BTN = 3;
	private static final int PERFORM_DOWNLOAD_BTN = 4;
	private static final int PERFORM_UPLOAD_BTN = 5;
	private static final int ENABLE_BACK_BTN = 6;
	private static final int ENABLE_FLOOR_BTN = 7;
	private static final int ENABLE_STATUS_BTN = 8;
	private static final int ENABLE_DOWNLOAD_BTN = 9;
	private static final int ENABLE_UPLOAD_BTN = 10;
	private static final int PERFORM_BUTTON_DELAY = 250;

	private ChecksViewAdapter mChecksViewAdapter;
	private TextView mTitleTextView;
	private ImageButton mBackButton;
	private TextView mDetailTextView;
	private LinearLayout mFloorFilterLineayLayout;
	private TextView mFloorFilterTextView;
	private LinearLayout mStatusFilterLineayLayout;
	private TextView mStatusFilterTextView;
	private ListView mListView;
	private Button mDownloadButton;
	private Button mUploadButton;
	private String mSelectedFloor = "-1";
	private String mCheckType;
	private ArrayList<String> mFloorList;
	private ProgressDialog mProgressDialog;
	private int mStatus;
	private String mCommunityNum;
	private String mBuildingNum;
	static int mLastFloorSelection;
	static int mLastStatusSelection;
	static CharSequence[] mFloorStringArray;
	static CharSequence[] mStatusArray;
	private DownloadAsyncTask mDownloadAsyncTask;
	private UploadAsyncTask mUploadAsyncTask;

	private Animation mAnimation;

	private ArrayList<RoomInfo> mRoomInfoArray = new ArrayList<RoomInfo>();

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case PROGRESS_DISMISS_MSG:
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
				}
				break;
			case PERFORM_BACK_BTN:
				onBackPressed();
				mHandler.sendEmptyMessageDelayed(ENABLE_BACK_BTN,
						PERFORM_BUTTON_DELAY);
				break;
			case PERFORM_FLOOR_BTN:
				performFloorButton();
				mHandler.sendEmptyMessageDelayed(ENABLE_FLOOR_BTN,
						PERFORM_BUTTON_DELAY);
				break;
			case PERFORM_STATUS_BTN:
				performStatusButton();
				mHandler.sendEmptyMessageDelayed(ENABLE_STATUS_BTN,
						PERFORM_BUTTON_DELAY);
				break;
			case PERFORM_DOWNLOAD_BTN:
				performDownloadButton();
				mHandler.sendEmptyMessageDelayed(ENABLE_DOWNLOAD_BTN,
						PERFORM_BUTTON_DELAY);
				break;
			case PERFORM_UPLOAD_BTN:
				performUploadButton();
				mHandler.sendEmptyMessageDelayed(ENABLE_UPLOAD_BTN,
						PERFORM_BUTTON_DELAY);
				break;
			case ENABLE_BACK_BTN:
				mBackButton.setEnabled(true);
				break;
			case ENABLE_FLOOR_BTN:
				mFloorFilterLineayLayout.setEnabled(true);
				break;
			case ENABLE_STATUS_BTN:
				mStatusFilterLineayLayout.setEnabled(true);
				break;
			case ENABLE_DOWNLOAD_BTN:
				mDownloadButton.setEnabled(true);
				break;
			case ENABLE_UPLOAD_BTN:
				mUploadButton.setEnabled(true);
				break;

			}

		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		mCommunityNum = intent.getStringExtra(Utils.EXTRA_COMMUNITY_NUM);
		mBuildingNum = intent.getStringExtra(Utils.EXTRA_BUILDING_NUM);
		mCheckType = intent.getStringExtra(Utils.EXTRA_CHECK_TYPE);
		setContentView(R.layout.checks);
	}

	private boolean isCheckTypeSecurity() {
		return mCheckType.equals(Utils.CHECK_TYPE_SECURITY);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mAnimation = AnimationUtils.loadAnimation(ChecksActivity.this,
				R.anim.button_animation);
		mTitleTextView = (TextView) findViewById(R.id.title_text_view);
		if (isCheckTypeSecurity()) {
			mTitleTextView.setText(getResources().getString(
					R.string.security_checks_title));
		} else {
			mTitleTextView.setText(getResources().getString(
					R.string.cleaning_checks_title));
		}

		mDetailTextView = (TextView) findViewById(R.id.detail_text_view);
		mDetailTextView.setText(getString(R.string.checks_detail,
				mCommunityNum, mBuildingNum));

		mBackButton = (ImageButton) findViewById(R.id.back_button);
		mBackButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mBackButton.startAnimation(mAnimation);
				mHandler.removeMessages(PERFORM_BACK_BTN);
				mHandler.sendEmptyMessageDelayed(PERFORM_BACK_BTN,
						PERFORM_BUTTON_DELAY);
				mBackButton.setEnabled(false);
			}

		});

		mFloorFilterLineayLayout = (LinearLayout) findViewById(R.id.list_title_floor_linear_layout);
		mFloorFilterTextView = (TextView) findViewById(R.id.list_title_floor_text_view);

		mStatusFilterLineayLayout = (LinearLayout) findViewById(R.id.list_title_status_linear_layout);
		mStatusFilterTextView = (TextView) findViewById(R.id.list_title_status_text_view);

		mListView = (ListView) findViewById(R.id.checks_list_view);

		mDownloadButton = (Button) findViewById(R.id.download_button);
		mUploadButton = (Button) findViewById(R.id.upload_button);

		initFloorTitleFilter();
		initStatusTitleFilter();
		refreshListView();
		initButton();
	}

	private void performStatusButton() {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(ChecksActivity.this)
				.setTitle(getString(R.string.floor_title))
				.setSingleChoiceItems(mStatusArray, mLastStatusSelection,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								refreshStatusFilter(which);
							}
						}).show();
	}

	private void initStatusTitleFilter() {
		if (mStatusFilterLineayLayout == null) {
			return;
		}

		mStatusArray = getResources().getStringArray(
				R.array.title_status_string_array);

		mStatus = mLastStatusSelection = mStatusArray.length - 1;

		mStatusFilterLineayLayout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mStatusFilterLineayLayout.startAnimation(mAnimation);
				mHandler.removeMessages(PERFORM_STATUS_BTN);
				mHandler.sendEmptyMessageDelayed(PERFORM_STATUS_BTN,
						PERFORM_BUTTON_DELAY);
				mStatusFilterLineayLayout.setEnabled(false);

			}

		});
	}

	private void refreshStatusFilter(int which) {
		mStatus = mLastStatusSelection = which;
		refreshListView();
		mStatusFilterTextView.setText(mStatusArray[which]);
	}

	private void initFloorTitleFilter() {
		if (mFloorFilterLineayLayout == null) {
			return;
		}
		mFloorList = new ArrayList<String>();
		String lastFloor = "";

		DatabaseHelper databaseHelper = new DatabaseHelper(this);
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor cursor = null;
		if (isCheckTypeSecurity()) {
			cursor = db.query(DatabaseHelper.DB_SECURITY_CHECK_TABLE,
					new String[] { DatabaseHelper.KEY_ROOM_FLOOR }, null, null,
					null, null, null, null);
		} else {
			cursor = db.query(DatabaseHelper.DB_CLEANING_CHECK_TABLE,
					new String[] { DatabaseHelper.KEY_ROOM_FLOOR }, null, null,
					null, null, null, null);
		}
		while (cursor.moveToNext()) {
			String floor = cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.KEY_ROOM_FLOOR));
			if (!lastFloor.equals(floor)) {
				mFloorList.add(floor);
			}
			lastFloor = floor;
		}
		cursor.close();
		databaseHelper.close();
		mFloorList.add(getString(R.string.all_floors));

		mFloorStringArray = new String[mFloorList.size()];
		for (int i = 0; i < mFloorList.size(); i++) {
			mFloorStringArray[i] = mFloorList.get(i);
		}

		mLastFloorSelection = mFloorStringArray.length - 1;

		mFloorFilterLineayLayout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mFloorFilterLineayLayout.startAnimation(mAnimation);
				mHandler.removeMessages(PERFORM_FLOOR_BTN);
				mHandler.sendEmptyMessageDelayed(PERFORM_FLOOR_BTN,
						PERFORM_BUTTON_DELAY);
				mFloorFilterLineayLayout.setEnabled(false);
			}

		});
	}

	private void performFloorButton() {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(ChecksActivity.this)
				.setTitle(getString(R.string.floor_title))
				.setSingleChoiceItems(mFloorStringArray, mLastFloorSelection,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								refreshFloorFilter(which);

							}
						}).show();
	}

	private void refreshFloorFilter(int which) {
		Log.e("zys", "mFloorStringArray.length = " + mFloorStringArray.length);
		if (mFloorStringArray.length <= 1) {
			initFloorTitleFilter();
			which = mFloorStringArray.length - 1;
		}
		String floor = (String) mFloorStringArray[which];
		// Filter in ListView
		if (which < mFloorStringArray.length - 1) {
			mSelectedFloor = floor;
		} else {
			mSelectedFloor = "-1";
		}
		refreshListView();

		// Set text in TextView
		if (which == mFloorStringArray.length - 1) {
			floor += getString(R.string.title_all_floor_suffix);
		} else {
			floor += getString(R.string.title_other_floor_suffix);
		}
		mFloorFilterTextView.setText(floor);

		// Record last selection
		mLastFloorSelection = which;
	}

	private void refreshListView() {
		if (mListView == null) {
			return;
		}

		mChecksViewAdapter = new ChecksViewAdapter(this, mSelectedFloor,
				mCheckType, mStatus);
		mListView.setAdapter(mChecksViewAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String roomNum = ((ChecksViewAdapter) arg0.getAdapter())
						.getRoomNum(arg2);
				Log.e("zys", "roomNum = " + roomNum);
				startChecksDetailActivity(arg2);
			}

		});
	}

	private void startDownload() {

		mProgressDialog = new ProgressDialog(ChecksActivity.this,
				R.style.progress_dialog);
		mProgressDialog.setTitle(getString(R.string.downloading_title));
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setMessage(getString(R.string.downloading_message));
		mProgressDialog.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					dialog.dismiss();
					mDownloadAsyncTask.cancel(true);
					return true;
				} else {
					return false;
				}
			}
		});
		mProgressDialog.show();
		mDownloadAsyncTask = new DownloadAsyncTask();
		mDownloadAsyncTask.execute(Utils.URL, Utils.BACKUP_URL);
	}

	private void startUpload() {

		mProgressDialog = new ProgressDialog(ChecksActivity.this,
				R.style.progress_dialog);
		mProgressDialog.setTitle(getString(R.string.uploading_title));
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setMessage(getString(R.string.uploading_message));
		mProgressDialog.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					dialog.dismiss();
					mUploadAsyncTask.cancel(true);
					return true;
				} else {
					return false;
				}
			}
		});
		mProgressDialog.show();
		mUploadAsyncTask = new UploadAsyncTask();
		mUploadAsyncTask.execute(Utils.URL, Utils.BACKUP_URL);
	}

	private void performDownloadButton() {
		if (mChecksViewAdapter.getCount() > 0) {
			if (mChecksViewAdapter.isAllFinished()) {
				startDownload();
			} else {
				AlertDialog builder = new AlertDialog.Builder(
						ChecksActivity.this)
						.setTitle(R.string.warning)
						.setMessage(
								R.string.downloading_no_all_finished_warning)
						.setPositiveButton(
								R.string.downloading_no_all_finished_warning_positive,
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated
										// method stub
										startDownload();
										dialog.dismiss();
									}
								})
						.setNegativeButton(
								R.string.downloading_no_all_finished_warning_negative,
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated
										// method stub
										dialog.cancel();
									}
								}).show();
			}
		} else if (mChecksViewAdapter.getCount() == 0) {
			startDownload();
		}
	}

	private void performUploadButton() {
		refreshFloorFilter(mFloorStringArray.length - 1);
		refreshStatusFilter(mStatusArray.length - 1);
		refreshListView();
		if (mChecksViewAdapter.getCount() == 0) {
			Toast.makeText(ChecksActivity.this, getString(R.string.empty_list),
					Toast.LENGTH_SHORT).show();
		} else {
			if (mChecksViewAdapter.isAllFinished()) {
				startUpload();
			} else {
				AlertDialog builder = new AlertDialog.Builder(
						ChecksActivity.this)
						.setTitle(R.string.warning)
						.setMessage(R.string.uploading_no_all_finished_warning)
						.setPositiveButton(
								R.string.uploading_no_all_finished_warning_positive,
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated
										// method stub
										startUpload();
										dialog.dismiss();
									}
								})
						.setNegativeButton(
								R.string.uploading_no_all_finished_warning_negative,
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated
										// method stub
										dialog.cancel();
									}
								}).show();
			}
		}
	}

	private void initButton() {
		if (mDownloadButton != null) {
			mDownloadButton.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					mDownloadButton.startAnimation(mAnimation);
					mHandler.removeMessages(PERFORM_DOWNLOAD_BTN);
					mHandler.sendEmptyMessageDelayed(PERFORM_DOWNLOAD_BTN,
							PERFORM_BUTTON_DELAY);
					mDownloadButton.setEnabled(false);
				}

			});
		}

		if (mUploadButton != null) {
			mUploadButton.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					mUploadButton.startAnimation(mAnimation);
					mHandler.removeMessages(PERFORM_UPLOAD_BTN);
					mHandler.sendEmptyMessageDelayed(PERFORM_UPLOAD_BTN,
							PERFORM_BUTTON_DELAY);
					mUploadButton.setEnabled(false);
				}

			});
		}
	}

	private void queryDatabase() {
		mRoomInfoArray.clear();
		DatabaseHelper databaseHelper = new DatabaseHelper(this);
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor cursor = null;
		String tableName = null;
		if (isCheckTypeSecurity()) {
			tableName = DatabaseHelper.DB_SECURITY_CHECK_TABLE;
		} else {
			tableName = DatabaseHelper.DB_CLEANING_CHECK_TABLE;
		}
		String[] queryKey = new String[] { DatabaseHelper.KEY_ROOM_FLOOR,
				DatabaseHelper.KEY_ROOM_NUM,
				DatabaseHelper.KEY_ROOM_UPDATE_TIME,
				DatabaseHelper.KEY_ROOM_SCORE,
				DatabaseHelper.KEY_ROOM_SCORE_REASON,
				DatabaseHelper.KEY_ROOM_SCORE_NOTES,
				DatabaseHelper.KEY_ROOM_FINISHED };
		cursor = db.query(tableName, queryKey, null, null, null, null, null,
				null);
		int roomFloor = 1;
		String roomNum = null;
		String roomUpdateTime = null;
		int roomScore = 5;
		String roomScoreReason = null;
		String roomScoreNotes = null;
		String status = null;
		while (cursor != null && cursor.moveToNext()) {
			roomFloor = cursor.getInt(cursor
					.getColumnIndex(DatabaseHelper.KEY_ROOM_FLOOR));
			roomNum = cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.KEY_ROOM_NUM));
			roomUpdateTime = cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.KEY_ROOM_UPDATE_TIME));
			roomScore = cursor.getInt(cursor
					.getColumnIndex(DatabaseHelper.KEY_ROOM_SCORE));
			roomScoreReason = cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.KEY_ROOM_SCORE_REASON));
			roomScoreNotes = cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.KEY_ROOM_SCORE_NOTES));
			status = cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.KEY_ROOM_FINISHED));

			if (status.equals("0")) {
				roomScore = -1;
				roomScoreReason = "";
				roomScoreNotes = "";
			}

			RoomInfo room = new RoomInfo(roomFloor, roomNum, roomUpdateTime,
					status, roomScore, roomScoreReason, roomScoreNotes);
			if (room != null) {
				mRoomInfoArray.add(room);
			}
		}
		cursor.close();
		databaseHelper.close();
	}

	private String createJson() {
		queryDatabase();
		String head = "[";
		String tail = "]";
		String body = "";
		String json = "";
		try {
			for (int i = 0; i < mRoomInfoArray.size(); i++) {
				JSONObject jsonBody = new JSONObject();

				jsonBody.put(Utils.ROOM_UPLOAD_JSON_ROOMFLOOR,
						mRoomInfoArray.get(i).mFloor);
				jsonBody.put(Utils.ROOM_UPLOAD_JSON_ROOMNUM,
						mRoomInfoArray.get(i).mRoomNum);
				jsonBody.put(Utils.ROOM_UPLOAD_JSON_ROOMUPDATE,
						mRoomInfoArray.get(i).mUpdateTime);
				jsonBody.put(Utils.ROOM_UPLOAD_JSON_ROOMSCORE,
						mRoomInfoArray.get(i).mScore);
				jsonBody.put(Utils.ROOM_UPLOAD_JSON_ROOMREASON,
						mRoomInfoArray.get(i).mScoreReason);
				jsonBody.put(Utils.ROOM_UPLOAD_JSON_ROOMNOTES,
						mRoomInfoArray.get(i).mNotes);

				body += jsonBody + Utils.JSON_COMMA_KEY;

			}
			body = body.substring(0, body.length() - 3);
			json = head + body + tail;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}

	private String mUploadJson;

	private class UploadAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			String url1 = params[0];
			String url2 = params[1];
			String reps = "";
			String value = "";
			if (isCheckTypeSecurity()) {
				value = Utils.HTTP_UPLOAD_SECURITY_VALUE;
			} else {
				value = Utils.HTTP_UPLOAD_CLEANING_VALUE;
			}
			mUploadJson = createJson();
			Log.e("zys", "mUploadJson = " + mUploadJson);
			if (Utils.IS_POST) {
				reps = Utils.doPost(url1, value, mUploadJson, mCommunityNum,
						mBuildingNum);
				if (reps == null || reps.isEmpty() || reps.contains("html")) {
					reps = Utils.doPost(url2, value, mUploadJson,
							mCommunityNum, mBuildingNum);
				}
			} else {
				reps = Utils.doGet(url1, value, mCommunityNum, mBuildingNum);
				if (reps == null || reps.isEmpty()) {
					reps = Utils
							.doGet(url2, value, mCommunityNum, mBuildingNum);
				}
			}
			return reps;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			Log.e("zys", "result = " + result);

			String info = null;
			if (result != null && result.equals("3")) {
				info = getString(R.string.uploading_success);
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
				}
				Toast.makeText(ChecksActivity.this, info, Toast.LENGTH_SHORT)
						.show();
//				createAndInsertDataFromServerToDatabase(mUploadJson);
				deleteTable();

			} else {
				info = getString(R.string.uploading_fail);
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
				}
				Toast.makeText(ChecksActivity.this, info, Toast.LENGTH_SHORT)
						.show();

			}
			super.onPostExecute(result);
		}

	}

	private void deleteTable() {
		DatabaseHelper databaseHelper = new DatabaseHelper(this);

		String tableName = null;
		if (isCheckTypeSecurity()) {
			tableName = DatabaseHelper.DB_SECURITY_CHECK_TABLE;
		} else {
			tableName = DatabaseHelper.DB_CLEANING_CHECK_TABLE;
		}

		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		db.execSQL("delete from " + tableName);
		db.close();

		databaseHelper.close();
		refreshListView();

	}

	private class DownloadAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			String url1 = params[0];
			String url2 = params[1];
			String reps = "";
			String value = "";
			value = Utils.HTTP_ROOM_VALUE;
			if (Utils.IS_POST) {
				reps = Utils.doPost(url1, value, mCommunityNum, mBuildingNum);
				if (reps == null || reps.isEmpty() || reps.contains("html")) {
					reps = Utils.doPost(url2, value, mCommunityNum,
							mBuildingNum);
				}
			} else {
				reps = Utils.doGet(url1, value, mCommunityNum, mBuildingNum);
				if (reps == null || reps.isEmpty() || reps.contains("html")) {
					reps = Utils
							.doGet(url2, value, mCommunityNum, mBuildingNum);
				}
			}
			return reps;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			Log.e("zys", "result = " + result);

			if (result != null && !result.isEmpty() && !result.contains("html")) {
				if (mProgressDialog != null) {
					mProgressDialog
							.setMessage(getString(R.string.downloading_success));
				}
				mHandler.removeMessages(PROGRESS_DISMISS_MSG);
				mHandler.sendEmptyMessageDelayed(PROGRESS_DISMISS_MSG, 1000);
				createAndInsertDataFromServerToDatabase(result);

			} else {
				if (mProgressDialog != null) {
					mProgressDialog
							.setMessage(getString(R.string.downloading_fail));
				}
				mHandler.removeMessages(PROGRESS_DISMISS_MSG);
				mHandler.sendEmptyMessageDelayed(PROGRESS_DISMISS_MSG, 1000);
				if (isDatabaseEmpty()) {
					createAndInsertDataFromLocalToDatabase();
				}

			}
			super.onPostExecute(result);
		}

	}

	private boolean isDatabaseEmpty() {
		boolean result = false;
		DatabaseHelper databaseHelper = new DatabaseHelper(this);

		String tableName = null;
		if (isCheckTypeSecurity()) {
			tableName = DatabaseHelper.DB_SECURITY_CHECK_TABLE;
		} else {
			tableName = DatabaseHelper.DB_CLEANING_CHECK_TABLE;
		}

		SQLiteDatabase readDb = databaseHelper.getReadableDatabase();
		Cursor cursor = readDb.query(tableName, new String[] { "_id",
				DatabaseHelper.KEY_ROOM_FLOOR, }, null, null, null, null, null,
				null);
		Log.e("zys",
				"cursor = " + cursor + "cursor.getCount() = "
						+ cursor.getCount());

		if (cursor == null || cursor.getCount() == 0) {
			result = true;
		}

		if (cursor != null) {
			cursor.close();
		}
		databaseHelper.close();
		return result;
	}

	private void insertToDatabase(boolean isForce) {
		DatabaseHelper databaseHelper = new DatabaseHelper(this);

		String tableName = null;
		if (isCheckTypeSecurity()) {
			tableName = DatabaseHelper.DB_SECURITY_CHECK_TABLE;
		} else {
			tableName = DatabaseHelper.DB_CLEANING_CHECK_TABLE;
		}

		if (isDatabaseEmpty() || isForce) {

			SQLiteDatabase db = databaseHelper.getWritableDatabase();
			db.execSQL("delete from " + tableName);
			ContentValues values = new ContentValues();

			if (mRoomInfoArray != null) {
				for (int i = 0; i < mRoomInfoArray.size(); i++) {
					values.put(DatabaseHelper.KEY_ROOM_FLOOR,
							mRoomInfoArray.get(i).mFloor);
					values.put(DatabaseHelper.KEY_ROOM_NUM,
							mRoomInfoArray.get(i).mRoomNum);
					values.put(DatabaseHelper.KEY_ROOM_UPDATE_TIME,
							mRoomInfoArray.get(i).mUpdateTime);
					values.put(DatabaseHelper.KEY_ROOM_FINISHED,
							mRoomInfoArray.get(i).mFinished);
					values.put(DatabaseHelper.KEY_ROOM_SCORE,
							mRoomInfoArray.get(i).mScore);
					values.put(DatabaseHelper.KEY_ROOM_SCORE_REASON,
							mRoomInfoArray.get(i).mScoreReason);
					values.put(DatabaseHelper.KEY_ROOM_SCORE_NOTES,
							mRoomInfoArray.get(i).mNotes);

					db.insert(tableName, null, values);
				}
			}
		} else {
			if (mRoomInfoArray == null) {
				createAndInsertDataFromLocalToDatabase();
			}
		}
		databaseHelper.close();
		refreshFloorFilter(mFloorStringArray.length - 1);
		refreshStatusFilter(mStatusArray.length - 1);
		refreshListView();
	}

	private void createAndInsertDataFromServerToDatabase(String json) {
		mRoomInfoArray.clear();
		try {
			JSONArray jsonObjs = new JSONArray(json);
			Log.e("zys", "jsonObjs.length() = " + jsonObjs.length());
			RoomInfo room;
			for (int i = 0; i < jsonObjs.length(); i++) {
				JSONObject jsonObj = (JSONObject) jsonObjs.opt(i);
				room = new RoomInfo(jsonObj.getInt(Utils.ROOM_JSON_FLOOR_KEY),
						jsonObj.getString(Utils.ROOM_JSON_ROOMNUM_KEY));
				if (room != null) {
					mRoomInfoArray.add(room);
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		insertToDatabase(true);

	}

	private void createAndInsertDataFromLocalToDatabase() {
		createAndInsertDataFromServerToDatabase(Utils.ROOM_INFO_LOCAL_JSON);
	}

	private void startChecksDetailActivity(int index) {
		Intent intent = new Intent(this, ChecksDetailActivity.class);
		intent.putExtra(Utils.EXTRA_COMMUNITY_NUM, mCommunityNum);
		intent.putExtra(Utils.EXTRA_BUILDING_NUM, mBuildingNum);
		intent.putExtra(Utils.EXTRA_CHECK_TYPE, mCheckType);
		intent.putExtra(Utils.EXTRA_ROOM_INDEX, index);
		intent.putExtra(Utils.EXTRA_ROOM_NUM_ARRAY,
				mChecksViewAdapter.getRoomNumArray());
		startActivity(intent);
	}
}
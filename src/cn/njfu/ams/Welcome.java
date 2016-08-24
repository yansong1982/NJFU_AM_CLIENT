package cn.njfu.ams;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
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
import android.widget.ProgressBar;
import android.widget.Toast;

public class Welcome extends Activity {

	private static final int SET_PENDING_MSG = 0;
	private static final int START_LOGIN_MSG = 1;
	private static final int EXIT_MSG = 2;
	private static final int UPDATE_PROGRESS_MSG = 3;
	private static final int DISMISS_PROGRESS_MSG = 4;
	private static final int EXIT_DELAY = 2000;
	private static final int UPDATE_PROGRESS_DELAY = 200;
	private static final int DISMISS_PROGRESS_DELAY = 1000;
	private String info = "";

	private static boolean isPending = false;
	private static boolean isExit = false;

	private ProgressBar mProgressBar;
	private int mStep;

	ArrayList<AdminPwd> mAdminPwdArray = new ArrayList<AdminPwd>();

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SET_PENDING_MSG:
				isPending = true;
				break;
			case START_LOGIN_MSG:
				if (isPending) {
					startLoginActivity();
				} else {
					mHandler.removeMessages(START_LOGIN_MSG);
					mHandler.sendEmptyMessageDelayed(START_LOGIN_MSG, 200);
				}
				break;
			case UPDATE_PROGRESS_MSG:
				Log.e("zys", "mStep = " + mStep);
				if (mStep < 100) {
					mStep++;
					mProgressBar.setProgress(mStep);
				}

				if (mStep == 100) {
					mHandler.removeMessages(UPDATE_PROGRESS_MSG);
					mHandler.sendEmptyMessageDelayed(DISMISS_PROGRESS_MSG,
							DISMISS_PROGRESS_DELAY);
					break;
				}
				mHandler.removeMessages(UPDATE_PROGRESS_MSG);
				mHandler.sendEmptyMessageDelayed(UPDATE_PROGRESS_MSG,
						UPDATE_PROGRESS_DELAY);
				break;

			case DISMISS_PROGRESS_MSG:
				if (mProgressBar != null) {
					mProgressBar.setVisibility(View.INVISIBLE);
				}
				break;
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void exit() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(getApplicationContext(),
					getString(R.string.exit_check_toast), Toast.LENGTH_SHORT)
					.show();
			mHandler.sendEmptyMessageDelayed(EXIT_MSG, EXIT_DELAY);
		} else {
			finish();
			System.exit(0);
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
	}

	private void initProgress() {
		mProgressBar = (ProgressBar) findViewById(R.id.progress);
		mProgressBar.setMax(100);
		mProgressBar.setProgress(0);
		mStep = 0;
		mHandler.sendEmptyMessageDelayed(UPDATE_PROGRESS_MSG,
				UPDATE_PROGRESS_DELAY);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// createAndInsertDataToDatabase();
		initProgress();
		new SubmitAsyncTask().execute(Utils.URL, Utils.BACKUP_URL);
		mHandler.sendEmptyMessageDelayed(SET_PENDING_MSG, 2000);
	}

	private boolean isDatabaseEmpty() {
		boolean result = false;
		DatabaseHelper databaseHelper = new DatabaseHelper(this);

		SQLiteDatabase readDb = databaseHelper.getReadableDatabase();
		Cursor cursor = readDb.query(DatabaseHelper.DB_APARTMENT_INFO_TABLE,
				new String[] { "_id", DatabaseHelper.KEY_COMMUNITY_ID,
						DatabaseHelper.KEY_BUILDING_ID,
						DatabaseHelper.KEY_PASSWORD }, null, null, null, null,
				null, null);
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

	private void insertToDatabase(boolean isConnect) {
		DatabaseHelper databaseHelper = new DatabaseHelper(this);

		if (isDatabaseEmpty() || isConnect) {
			Toast.makeText(Welcome.this, getString(R.string.update_database),
					Toast.LENGTH_SHORT).show();
			SQLiteDatabase writeDb = databaseHelper.getWritableDatabase();
			writeDb.execSQL("delete from "
					+ DatabaseHelper.DB_APARTMENT_INFO_TABLE);
			ContentValues values = new ContentValues();

			if (mAdminPwdArray != null) {
				for (int i = 0; i < mAdminPwdArray.size(); i++) {
					values.put(DatabaseHelper.KEY_COMMUNITY_ID,
							mAdminPwdArray.get(i).mCommunity);
					values.put(DatabaseHelper.KEY_BUILDING_ID,
							mAdminPwdArray.get(i).mBuilding);
					values.put(DatabaseHelper.KEY_PASSWORD,
							mAdminPwdArray.get(i).mPassword);
					writeDb.insert(DatabaseHelper.DB_APARTMENT_INFO_TABLE,
							null, values);
				}
			}
		} else {
			if (mAdminPwdArray == null) {
				createAndInsertDataFromLocalToDatabase();
			}

		}
		databaseHelper.close();
	}

	private void createAndInsertDataFromServerToDatabase(String json) {
		try {
			JSONArray jsonObjs = new JSONArray(json);
			Log.e("zys", "jsonObjs.length() = " + jsonObjs.length());
			AdminPwd adminPassword;
			for (int i = 0; i < jsonObjs.length(); i++) {
				JSONObject jsonObj = (JSONObject) jsonObjs.opt(i);
				adminPassword = new AdminPwd(
						jsonObj.getInt(Utils.ADMIN_JSON_COMMUNITY_KEY),
						jsonObj.getInt(Utils.ADMIN_JSON_BUILDING_KEY),
						jsonObj.getString(Utils.ADMIN_JSON_PASSWORD_KEY));
				if (adminPassword != null) {
					Log.e("zys", "add----------------");
					mAdminPwdArray.add(adminPassword);
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		insertToDatabase(true);

	}

	private void createAndInsertDataFromLocalToDatabase() {
		createAndInsertDataFromServerToDatabase(Utils.ADMIN_PWD_LOCAL_JSON);
	}

	private void startLoginActivity() {
		Intent intent = new Intent(Welcome.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	public class SubmitAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url1 = params[0];
			String url2 = params[1];
			String reps = "";
			if (Utils.IS_POST) {
				reps = Utils.doPost(url1, Utils.HTTP_LOGIN_VALUE);
				if (reps == null || reps.isEmpty() || reps.contains("html")) {
					reps = Utils.doPost(url2, Utils.HTTP_LOGIN_VALUE);
				}
			} else {
				reps = Utils.doGet(url1, Utils.HTTP_LOGIN_VALUE);
				if (reps == null || reps.isEmpty()) {
					reps = Utils.doGet(url2, Utils.HTTP_LOGIN_VALUE);
				}
			}
			return reps;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			Log.e("zys", "result = " + result);
			mStep = 99;
			mHandler.sendEmptyMessage(UPDATE_PROGRESS_MSG);
			if (result != null && !result.isEmpty() && !result.contains("html")) {
				info = getString(R.string.connect_success);
				Toast.makeText(Welcome.this, info, Toast.LENGTH_SHORT).show();
				createAndInsertDataFromServerToDatabase(result);

			} else {
				info = getString(R.string.connect_fail);
				Toast.makeText(Welcome.this, info, Toast.LENGTH_SHORT).show();
				Log.e("zys", "isDatabaseEmpty = "+isDatabaseEmpty());
				if (isDatabaseEmpty()) {
					createAndInsertDataFromLocalToDatabase();
				}

			}

			if (isPending) {
				startLoginActivity();
			} else {
				mHandler.removeMessages(START_LOGIN_MSG);
				mHandler.sendEmptyMessageDelayed(START_LOGIN_MSG, 200);
			}
			super.onPostExecute(result);
		}

	}

}

package cn.njfu.ams;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class ChecksDetailActivity extends Activity {
	private static final String TAG = "njfu_ChecksDetailActivity";
	private static final int MAX_EDIT_LENGTH = 200;
	private static final int PERFORM_BACK_BTN = 0;
	private static final int PERFORM_POSITIVE_BTN = 1;
	private static final int PERFORM_NEGATIVE_BTN = 2;
	private static final int PERFORM_NOTES_BTN = 3;
	private static final int PERFORM_NEXT_BTN = 4;
	private static final int PERFORM_PRIV_BTN = 5;
	private static final int ENABLE_BACK_BTN = 6;
	private static final int ENABLE_POSITIVE_BTN = 7;
	private static final int ENABLE_NEGATIVE_BTN = 8;
	private static final int ENABLE_NOTES_BTN = 9;
	private static final int ENABLE_NEXT_BTN = 10;
	private static final int ENABLE_PRIV_BTN = 11;

	private static final int PERFORM_BUTTON_DELAY = 250;
	private String mCommunityNum;
	private String mBuildingNum;
	private String mRoomNum;
	private int mRoomIndex;
	private String mCheckType;
	private ArrayList<String> mRoomNumArray;
	private ChecksDetailViewAdapter mChecksDetailViewAdapter;
	private TextView mTitleTextView;
	private ImageButton mBackButton;
	private TextView mDetailTextView;

	private RadioButton mPositiveRadioButton;
	private RadioButton mNegativeRadioButton;

	private Button mNotesButton;
	private EditText mNotesEditText;
	private String mNotesContent;

	private ListView mListView;
	private TextView mTotalTextView;
	private int mTotalScore;

	private Button mNextButton;
	private Button mPrivButton;
	
	private Animation mAnimation;

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case PERFORM_BACK_BTN:
				onBackPressed();
				mHandler.sendEmptyMessageDelayed(ENABLE_BACK_BTN,
						PERFORM_BUTTON_DELAY);
				break;
			case PERFORM_POSITIVE_BTN:
				performPositiveButton();
				mHandler.sendEmptyMessageDelayed(ENABLE_POSITIVE_BTN,
						PERFORM_BUTTON_DELAY);
				break;
			case PERFORM_NEGATIVE_BTN:
				performNegativeButton();
				mHandler.sendEmptyMessageDelayed(ENABLE_NEGATIVE_BTN,
						PERFORM_BUTTON_DELAY);
				break;
			case PERFORM_NOTES_BTN:
				performNotesButton();
				mHandler.sendEmptyMessageDelayed(ENABLE_NOTES_BTN,
						PERFORM_BUTTON_DELAY);
				break;
			case PERFORM_NEXT_BTN:
				performNextButton();
				mHandler.sendEmptyMessageDelayed(ENABLE_NEXT_BTN,
						PERFORM_BUTTON_DELAY);
				break;
			case PERFORM_PRIV_BTN:
				performPrivButton();
				mHandler.sendEmptyMessageDelayed(ENABLE_PRIV_BTN,
						PERFORM_BUTTON_DELAY);
				break;
			case ENABLE_BACK_BTN:
				mBackButton.setEnabled(true);
				break;
			case ENABLE_POSITIVE_BTN:
				mPositiveRadioButton.setEnabled(true);
				break;
			case ENABLE_NEGATIVE_BTN:
				mNegativeRadioButton.setEnabled(true);
				break;
			case ENABLE_NOTES_BTN:
				mNotesButton.setEnabled(true);
				break;
			case ENABLE_NEXT_BTN:
				mNextButton.setEnabled(true);
				break;
			case ENABLE_PRIV_BTN:
				mPrivButton.setEnabled(true);
				break;
			}
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checks_detail);
	}

	private void performPositiveButton() {
		Log.i(TAG, "Press mPositiveRadioButton");
		mPositiveRadioButton.setTextColor(android.graphics.Color.BLACK);
		mNegativeRadioButton.setTextColor(android.graphics.Color.LTGRAY);
		mListView.setVisibility(View.GONE);

		for (int i = 0; i < mChecksDetailViewAdapter.mIsSelectedArray.length; i++) {
			mChecksDetailViewAdapter.mIsSelectedArray[i] = false;
		}
		mChecksDetailViewAdapter.notifyDataSetChanged();
		refreshTotal();
	}

	private void performNegativeButton() {
		mPositiveRadioButton.setTextColor(android.graphics.Color.LTGRAY);
		mNegativeRadioButton.setTextColor(android.graphics.Color.BLACK);
		mListView.setVisibility(View.VISIBLE);
		refreshTotal();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		// TODO Auto-generated method stub
		super.onResume();
		mAnimation = AnimationUtils.loadAnimation(
				ChecksDetailActivity.this, R.anim.button_animation);
		Intent intent = getIntent();
		mCommunityNum = intent.getStringExtra(Utils.EXTRA_COMMUNITY_NUM);
		mBuildingNum = intent.getStringExtra(Utils.EXTRA_BUILDING_NUM);
		mCheckType = intent.getStringExtra(Utils.EXTRA_CHECK_TYPE);
		mRoomIndex = intent.getIntExtra(Utils.EXTRA_ROOM_INDEX, 0);
		mRoomNumArray = intent
				.getStringArrayListExtra(Utils.EXTRA_ROOM_NUM_ARRAY);
		mRoomNum = mRoomNumArray.get(mRoomIndex);
		Log.i(TAG, "getIntent--->>>, mCommunityNum = " + mCommunityNum
				+ "\n mBuildingNum = " + mBuildingNum + "\n mCheckType = "
				+ mCheckType + "\n mRoomIndex = " + mRoomIndex
				+ "\n mRoomNum = " + mRoomNum);

		mTitleTextView = (TextView) findViewById(R.id.title_text_view);
		if (mCheckType.equals(Utils.CHECK_TYPE_SECURITY)) {
			mTitleTextView.setText(getResources().getString(
					R.string.security_checks_title));
		} else {
			mTitleTextView.setText(getResources().getString(
					R.string.cleaning_checks_title));
		}

		mDetailTextView = (TextView) findViewById(R.id.detail_text_view);
		mDetailTextView.setText(getString(R.string.checks_detail_detail,
				mCommunityNum, mBuildingNum, mRoomNum));

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

		mPositiveRadioButton = (RadioButton) findViewById(R.id.radio_positive);
		mNegativeRadioButton = (RadioButton) findViewById(R.id.radio_negative);
		mListView = (ListView) findViewById(R.id.checks_list_view);

		mPositiveRadioButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mPositiveRadioButton.startAnimation(mAnimation);
				mHandler.removeMessages(PERFORM_POSITIVE_BTN);
				mHandler.sendEmptyMessageDelayed(PERFORM_POSITIVE_BTN,
						PERFORM_BUTTON_DELAY);
				mPositiveRadioButton.setEnabled(false);
			}

		});
		mNegativeRadioButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mNegativeRadioButton.startAnimation(mAnimation);
				mHandler.removeMessages(PERFORM_NEGATIVE_BTN);
				mHandler.sendEmptyMessageDelayed(PERFORM_NEGATIVE_BTN,
						PERFORM_BUTTON_DELAY);
				mNegativeRadioButton.setEnabled(false);
			}

		});

		mTotalTextView = (TextView) findViewById(R.id.total_text_view);

		mNotesButton = (Button) findViewById(R.id.notes_button);
		mNotesEditText = (EditText) findViewById(R.id.notes_edit_text);

		mNextButton = (Button) findViewById(R.id.next_button);
		mPrivButton = (Button) findViewById(R.id.privious_button);

		initListView();
		String scoreFromDB = mChecksDetailViewAdapter.getCurrentScore();
		Log.i(TAG, "scoreFromDB = " + scoreFromDB);
		if (scoreFromDB != null && !scoreFromDB.isEmpty()) {
			mTotalScore = Integer.parseInt(scoreFromDB);
		} else {
			mTotalScore = mChecksDetailViewAdapter.getCount();
		}
		Log.i(TAG, "onResume--->>>mTotalScore = " + mTotalScore);

		mNextButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				mNextButton.startAnimation(mAnimation);
				mHandler.removeMessages(PERFORM_NEXT_BTN);
				mHandler.sendEmptyMessageDelayed(PERFORM_NEXT_BTN,
						PERFORM_BUTTON_DELAY);
				mNextButton.setEnabled(false);
			}

		});

		mPrivButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				mPrivButton.startAnimation(mAnimation);
				mHandler.removeMessages(PERFORM_PRIV_BTN);
				mHandler.sendEmptyMessageDelayed(PERFORM_PRIV_BTN,
						PERFORM_BUTTON_DELAY);
				mPrivButton.setEnabled(false);
			}

		});

		refreshView();

	}
	
	private void performNextButton(){
		updateDatabase();
		Log.i(TAG, "Press mNextButton , mRoomNumArray.size() = "
				+ mRoomNumArray.size());
		if (mRoomIndex < mRoomNumArray.size() - 1) {
			++mRoomIndex;
			Log.i(TAG, "Press mNextButton --->>> mRoomIndex = "
					+ mRoomIndex);
		} else {
			Toast.makeText(ChecksDetailActivity.this,
					getString(R.string.last_room_toast),
					Toast.LENGTH_SHORT).show();
			Log.i(TAG,
					"Press mNextButton --->>> show last_room_toast, mRoomIndex = "
							+ mRoomIndex);
		}
		refreshView();
	}
	
	private void performPrivButton() {
		updateDatabase();
		Log.i(TAG, "Press mPrivButton , mRoomNumArray.size() = "
				+ mRoomNumArray.size());
		if (mRoomIndex > 0) {
			--mRoomIndex;
			Log.i(TAG, "Press mPrivButton --->>> mRoomIndex = "
					+ mRoomIndex);
		} else {
			Toast.makeText(ChecksDetailActivity.this,
					getString(R.string.first_room_toast),
					Toast.LENGTH_SHORT).show();
			Log.i(TAG,
					"Press mPrivButton --->>> show first_room_toast, mRoomIndex = "
							+ mRoomIndex);
		}
		refreshView();
	}

	private void refreshView() {
		mRoomNum = mRoomNumArray.get(mRoomIndex);
		mDetailTextView.setText(getString(R.string.checks_detail_detail,
				mCommunityNum, mBuildingNum, mRoomNum));
		initListView();
		initializeNotes();
		refreshTotal();
		if (mTotalScore == 5) {
			mPositiveRadioButton.setChecked(true);
			performPositiveButton();
		} else {
			mNegativeRadioButton.setChecked(true);
			performNegativeButton();
		}

	}

	private void initializeNotes() {
		mNotesContent = mChecksDetailViewAdapter.getNotesContent();
		if (mNotesContent != null && !mNotesContent.isEmpty()) {
			mNotesButton.setText(R.string.delete_notes);
			mNotesEditText.setVisibility(View.VISIBLE);
			mNotesEditText.setText(mNotesContent);
		} else {
			mNotesButton.setText(R.string.add_notes);
			mNotesEditText.setVisibility(View.GONE);

		}

		mNotesEditText.addTextChangedListener(new TextWatcher() {
			private int cou = 0;
			int selectionEnd = 0;

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				cou = mNotesEditText.length();
				Log.e("zys", "cou = " + cou);
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
				if (cou > MAX_EDIT_LENGTH) {
					Toast.makeText(ChecksDetailActivity.this,
							R.string.over_max_edit_toast, Toast.LENGTH_LONG)
							.show();
					selectionEnd = mNotesEditText.getSelectionEnd();
					s.delete(MAX_EDIT_LENGTH, selectionEnd);

				}
			}
		});

		mNotesButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mNotesButton.startAnimation(mAnimation);
				mHandler.removeMessages(PERFORM_NOTES_BTN);
				mHandler.sendEmptyMessageDelayed(PERFORM_NOTES_BTN,
						PERFORM_BUTTON_DELAY);
				mNotesButton.setEnabled(false);
			}

		});
	}
	
	private void performNotesButton(){
		if (mNotesButton.getText()
				.equals(getString(R.string.add_notes))) {
			mNotesButton.setText(R.string.delete_notes);
			mNotesEditText.setVisibility(View.VISIBLE);
		} else {
			mNotesButton.setText(R.string.add_notes);
			mNotesEditText.setVisibility(View.GONE);
		}
	}

	private void updateDatabase() {
		DatabaseHelper databaseHelper = new DatabaseHelper(
				ChecksDetailActivity.this);
		SQLiteDatabase db = databaseHelper.getWritableDatabase();

		String reason = "";
		for (int i = 0; i < mChecksDetailViewAdapter.getCount(); i++) {
			boolean state = mChecksDetailViewAdapter.mIsSelectedArray[i];
			reason += state ? "1" : "0";
		}
		Log.e("zys", "reason = " + reason);

		if (mNotesEditText.isShown()) {
			mNotesContent = mNotesEditText.getText().toString();
		} else {
			mNotesContent = "";
		}
		Log.e("zys", "mNotesContent = " + mNotesContent);
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss");
		String date = sDateFormat.format(new java.util.Date());
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.KEY_ROOM_UPDATE_TIME, date);
		values.put(DatabaseHelper.KEY_ROOM_SCORE, mTotalScore);
		values.put(DatabaseHelper.KEY_ROOM_SCORE_REASON, reason);
		values.put(DatabaseHelper.KEY_ROOM_FINISHED, "1");
		values.put(DatabaseHelper.KEY_ROOM_SCORE_NOTES, mNotesContent);
		if (mCheckType.equals(Utils.CHECK_TYPE_SECURITY)) {
			db.update(DatabaseHelper.DB_SECURITY_CHECK_TABLE, values,
					DatabaseHelper.KEY_ROOM_NUM + "=?",
					new String[] { mRoomNum });
		} else {
			db.update(DatabaseHelper.DB_CLEANING_CHECK_TABLE, values,
					DatabaseHelper.KEY_ROOM_NUM + "=?",
					new String[] { mRoomNum });
		}

		db.close();
		databaseHelper.close();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		updateDatabase();
		finish();

		// super.onBackPressed();
	}

	private void refreshTotal() {
		mTotalScore = mChecksDetailViewAdapter.getCount();
		for (int i = 0; i < mChecksDetailViewAdapter.getCount(); i++) {
			boolean state = mChecksDetailViewAdapter.mIsSelectedArray[i];
			Log.e("zys", "i = " + i + "state = " + state);
			if (mChecksDetailViewAdapter.mIsSelectedArray[i]) {
				mTotalScore--;
			}
		}
		Log.e("zys", "mTotalScore2222222 = " + mTotalScore);
		mTotalTextView.setText(getString((R.string.checks_detail_total),
				mTotalScore));
	}

	private void initListView() {
		if (mListView == null) {
			return;
		}
		mChecksDetailViewAdapter = new ChecksDetailViewAdapter(this, mRoomNum,
				mCheckType);
		mListView.setAdapter(mChecksDetailViewAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				boolean status = mChecksDetailViewAdapter.mIsSelectedArray[arg2];
				mChecksDetailViewAdapter.mIsSelectedArray[arg2] = !status;
				mChecksDetailViewAdapter.notifyDataSetChanged();
				refreshTotal();
			}
		});
	}

}
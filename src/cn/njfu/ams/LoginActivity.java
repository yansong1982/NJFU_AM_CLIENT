package cn.njfu.ams;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private static final String TAG = "njfu_LoginActivity";
	public static final String URL = "http://192.168.1.102:80/NJFU_AM_Server/login";

	private static final int EXIT_MSG = 0;
	private static final int PERFORM_INFO_BTN = 1;
	private static final int PERFORM_LOGIN_BTN = 2;
	private static final int ENABLE_LOGIN_BTN = 3;
	private static final int PERFORM_OTHERS_LOGIN_BTN = 4;
	private static final int ENABLE_OTHERS_LOGIN_BTN = 5;
	private static final int PERFORM_BUTTON_DELAY = 250;
	private Spinner mLoginIdSpin;
	private LinearLayout mAdminLL;
	private Spinner mCommunityNumSpin;
	private Spinner mBuildingNumSpin;
	private EditText mPasswordEdit;
	private Button mLoginBtn;
	private LinearLayout mOthersLL;
	private EditText mUsernameEdit;
	private EditText mOthersPasswordEdit;
	private Button mOthersLoginBtn;
	private String mCommunityNumSelected = "1";
	private String mBuildingNumSelected = "1";
	private boolean mIsPost = true; // 默认采取post登录方式

	private ImageButton mInfoButton;

	private static boolean isExit = false;

	private Animation mAnimation;

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case EXIT_MSG:
				isExit = false;
				break;
			case PERFORM_INFO_BTN:
				break;
			case PERFORM_LOGIN_BTN:
				comparePassword();
				mHandler.sendEmptyMessageDelayed(ENABLE_LOGIN_BTN,
						PERFORM_BUTTON_DELAY);
				break;
			case ENABLE_LOGIN_BTN:
				mLoginBtn.setEnabled(true);
				break;
			case PERFORM_OTHERS_LOGIN_BTN:
				mHandler.sendEmptyMessageDelayed(ENABLE_OTHERS_LOGIN_BTN,
						PERFORM_BUTTON_DELAY);
				break;
			case ENABLE_OTHERS_LOGIN_BTN:
				mOthersLoginBtn.setEnabled(true);
				break;
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		init();
	}

	private void init() {
		mAnimation = AnimationUtils.loadAnimation(LoginActivity.this,
				R.anim.button_animation);
		mInfoButton = (ImageButton) findViewById(R.id.info_button);
		mLoginIdSpin = (Spinner) findViewById(R.id.login_id_spinner);
		mAdminLL = (LinearLayout) findViewById(R.id.administrater_linearlayout);
		mCommunityNumSpin = (Spinner) findViewById(R.id.community_num_spinner);
		mBuildingNumSpin = (Spinner) findViewById(R.id.building_num_spinner);
		mPasswordEdit = (EditText) findViewById(R.id.password_edit_text);
		mLoginBtn = (Button) findViewById(R.id.login_button);
		mOthersLL = (LinearLayout) findViewById(R.id.others_linearlayout);
		mUsernameEdit = (EditText) findViewById(R.id.username_edit_text);
		mOthersPasswordEdit = (EditText) findViewById(R.id.others_password_edit_text);
		mOthersLoginBtn = (Button) findViewById(R.id.others_login_button);

		initInfoButton();
		initLoginIdSpin();
		initCommunityNumSpin();
		initBuildingNumSpin();
		initPasswordEditText();
		initLoginBtn();
		initOtherLoginBtn();
	}

	private void initInfoButton() {
		if (mInfoButton == null) {
			return;
		}
		mInfoButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mInfoButton.startAnimation(mAnimation);
				mHandler.removeMessages(PERFORM_INFO_BTN);
				mHandler.sendEmptyMessageDelayed(PERFORM_INFO_BTN,
						PERFORM_BUTTON_DELAY);
			}
		});
	}

	private void initLoginIdSpin() {
		if (mLoginIdSpin == null) {
			return;
		}
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.login_id_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mLoginIdSpin.setAdapter(adapter);
		mLoginIdSpin.setPromptId(R.string.login_id_prompt);
		mLoginIdSpin.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				if (id == 0) {
					mAdminLL.setVisibility(View.VISIBLE);
					mOthersLL.setVisibility(View.GONE);
					initCommunityNumSpin();
				} else {
					mAdminLL.setVisibility(View.GONE);
					mOthersLL.setVisibility(View.VISIBLE);
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void initCommunityNumSpin() {
		if (mCommunityNumSpin == null) {
			return;
		}
		ArrayList<String> communityNumList = new ArrayList<String>();
		String lastCommunityNum = "";

		DatabaseHelper databaseHelper = new DatabaseHelper(this);
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor cursor = db.query(DatabaseHelper.DB_APARTMENT_INFO_TABLE,
				new String[] { "_id", DatabaseHelper.KEY_COMMUNITY_ID,
						DatabaseHelper.KEY_BUILDING_ID,
						DatabaseHelper.KEY_PASSWORD }, null, null, null, null,
				null, null);
		while (cursor.moveToNext()) {
			String communityNum = cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.KEY_COMMUNITY_ID));
			if (!lastCommunityNum.equals(communityNum)) {
				communityNumList.add(communityNum);
			}
			lastCommunityNum = communityNum;
		}
		cursor.close();
		databaseHelper.close();

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.custom_spinner, communityNumList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mCommunityNumSpin.setAdapter(adapter);
		mCommunityNumSpin.setPromptId(R.string.community_num_prompt);
		mCommunityNumSpin
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int pos, long id) {
						mCommunityNumSelected = parent.getItemAtPosition(pos)
								.toString();
						initBuildingNumSpin();
					}

					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});
	}

	private void initBuildingNumSpin() {
		if (mBuildingNumSpin == null) {
			return;
		}
		ArrayList<String> communityNumList = new ArrayList<String>();
		String lastBuildingNum = "";

		DatabaseHelper databaseHelper = new DatabaseHelper(this);
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor cursor = db.query(DatabaseHelper.DB_APARTMENT_INFO_TABLE,
				new String[] { DatabaseHelper.KEY_COMMUNITY_ID,
						DatabaseHelper.KEY_BUILDING_ID },
				DatabaseHelper.KEY_COMMUNITY_ID + "=?",
				new String[] { mCommunityNumSelected }, null, null, null, null);
		while (cursor.moveToNext()) {
			String buildingNum = cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.KEY_BUILDING_ID));
			if (!lastBuildingNum.equals(buildingNum)) {
				communityNumList.add(buildingNum);
			}
			lastBuildingNum = buildingNum;
		}
		cursor.close();
		databaseHelper.close();

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.custom_spinner, communityNumList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mBuildingNumSpin.setAdapter(adapter);
		mBuildingNumSpin.setPromptId(R.string.building_num_prompt);
		mBuildingNumSpin
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int pos, long id) {
						mBuildingNumSelected = parent.getItemAtPosition(pos)
								.toString();

					}

					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});
	}

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
			// 利用handler延迟发送更改状态信息
			mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			finish();
			System.exit(0);
		}
	}

	private void initPasswordEditText() {
		mPasswordEdit.setText("");
		mPasswordEdit.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					comparePassword();
					return true;
				}
				// TODO Auto-generated method stub
				return false;
			}

		});

		mPasswordEdit.setOnEditorActionListener(new OnEditorActionListener() {

			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if ((actionId == 6)
						|| (actionId == 2)
						|| ((event != null) && (event.getAction() == 0) && (event
								.getKeyCode() == 66))) {
					comparePassword();
					return true;
				}
				// TODO Auto-generated method stub
				return false;
			}

		});

	}
	
	private void initOtherLoginBtn() {
		if (mOthersLoginBtn == null) {
			return;
		}
		mOthersLoginBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mOthersLoginBtn.startAnimation(mAnimation);
				mHandler.removeMessages(PERFORM_OTHERS_LOGIN_BTN);
				mHandler.sendEmptyMessageDelayed(PERFORM_OTHERS_LOGIN_BTN,
						PERFORM_BUTTON_DELAY);
				mOthersLoginBtn.setEnabled(false);
			}

		});

	}


	private void initLoginBtn() {
		if (mLoginBtn == null) {
			return;
		}
		mLoginBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mLoginBtn.startAnimation(mAnimation);
				mHandler.removeMessages(PERFORM_LOGIN_BTN);
				mHandler.sendEmptyMessageDelayed(PERFORM_LOGIN_BTN,
						PERFORM_BUTTON_DELAY);
				mLoginBtn.setEnabled(false);
			}

		});

	}

	private void comparePassword() {
		String passwordEdit = mPasswordEdit.getText().toString();
		if (passwordEdit == null || passwordEdit.isEmpty()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.warning);
			builder.setMessage(R.string.admin_password_empty);
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			builder.setPositiveButton(R.string.positive,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});

			builder.show();
		} else {
			DatabaseHelper databaseHelper = new DatabaseHelper(this);
			SQLiteDatabase db = databaseHelper.getReadableDatabase();
			String password = null;
			Cursor cursor = db
					.query(DatabaseHelper.DB_APARTMENT_INFO_TABLE,
							new String[] { DatabaseHelper.KEY_COMMUNITY_ID,
									DatabaseHelper.KEY_BUILDING_ID,
									DatabaseHelper.KEY_PASSWORD },
							DatabaseHelper.KEY_COMMUNITY_ID + "=? and "
									+ DatabaseHelper.KEY_BUILDING_ID + "=?",
							new String[] { mCommunityNumSelected,
									mBuildingNumSelected }, null, null, null,
							null);
			Log.e("zys", "count = " + cursor.getCount());
			Log.e("zys", "mCommunityNumSelected = " + mCommunityNumSelected);
			Log.e("zys", "mBuildingNumSelected = " + mBuildingNumSelected);
			while (cursor.moveToNext()) {
				password = cursor.getString(cursor
						.getColumnIndex(DatabaseHelper.KEY_PASSWORD));
				if (password != null && !password.isEmpty()) {
					break;
				}
			}
			cursor.close();
			databaseHelper.close();
			Log.e("zys", "password = " + password);
			if (password != null && password.equals(passwordEdit)) {
				Intent intent = new Intent(LoginActivity.this,
						AdminWorkActivity.class);
				intent.putExtra(Utils.EXTRA_COMMUNITY_NUM,
						mCommunityNumSelected);
				intent.putExtra(Utils.EXTRA_BUILDING_NUM, mBuildingNumSelected);
				startActivity(intent);
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.warning);
				builder.setMessage(R.string.admin_password_error);
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				builder.setPositiveButton(R.string.positive,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});

				builder.show();
			}
		}
	}

}
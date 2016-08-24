package cn.njfu.ams;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class AdminWorkActivity extends Activity {
	
	private static final int PERFORM_BACK_BTN = 0;
	private static final int PERFORM_SETTINGS_BTN = 1;
	private static final int ENABLE_BACK_BTN = 2;
	private static final int ENABLE_SETTINGS_BTN = 3;
	private static final int PERFORM_BUTTON_DELAY = 250;

	private AdminWorkViewAdapter mAdminWorkViewAdapter;
	private TextView mTitleTextView;
	private ImageButton mBackButton;
	private ImageButton mSettingsButton;
	private ListView mListView;
	
	private String mCommunityNum;
	private String mBuildingNum;
	
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
			case PERFORM_SETTINGS_BTN:
				mHandler.sendEmptyMessageDelayed(ENABLE_SETTINGS_BTN,
						PERFORM_BUTTON_DELAY);
				break;
			case ENABLE_BACK_BTN:
				mBackButton.setEnabled(true);
				break;
			case ENABLE_SETTINGS_BTN:
				mSettingsButton.setEnabled(true);
				break;
			}
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		mCommunityNum = intent
				.getStringExtra(Utils.EXTRA_COMMUNITY_NUM);
		mBuildingNum = intent
				.getStringExtra(Utils.EXTRA_BUILDING_NUM);
		setContentView(R.layout.administrator_work);

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mAnimation = AnimationUtils.loadAnimation(
				AdminWorkActivity.this, R.anim.button_animation);
		mTitleTextView = (TextView) findViewById(R.id.title_text_view);
		mTitleTextView.setText(getString(R.string.admin_work_title,
				mCommunityNum, mBuildingNum));
		
		mBackButton = (ImageButton) findViewById(R.id.back_button);
		mBackButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				mBackButton.startAnimation(mAnimation);
				mHandler.removeMessages(PERFORM_BACK_BTN);
				mHandler.sendEmptyMessageDelayed(PERFORM_BACK_BTN,
						PERFORM_BUTTON_DELAY);
			}
		});
		
		mSettingsButton = (ImageButton) findViewById(R.id.settings_button);
		mSettingsButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				mSettingsButton.startAnimation(mAnimation);
				mHandler.removeMessages(PERFORM_SETTINGS_BTN);
				mHandler.sendEmptyMessageDelayed(PERFORM_SETTINGS_BTN,
						PERFORM_BUTTON_DELAY);
			}
			
		});
		
		mAdminWorkViewAdapter = new AdminWorkViewAdapter(this);

		mListView = (ListView) findViewById(R.id.admin_work_list_view);
		mListView.setAdapter(mAdminWorkViewAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				switch(arg2){
				case 0:
					startChecksActivity(Utils.CHECK_TYPE_SECURITY);
					break;
				case 1:
					startChecksActivity(Utils.CHECK_TYPE_CLEANING);
					break;
				}
			}
		});

	}

	private void startChecksActivity(String extraChecksType){
		Intent intent = new Intent(AdminWorkActivity.this,
				ChecksActivity.class);
		intent.putExtra(Utils.EXTRA_COMMUNITY_NUM, mCommunityNum);
		intent.putExtra(Utils.EXTRA_BUILDING_NUM, mBuildingNum);
		intent.putExtra(Utils.EXTRA_CHECK_TYPE, extraChecksType);
		startActivity(intent);
	}

}

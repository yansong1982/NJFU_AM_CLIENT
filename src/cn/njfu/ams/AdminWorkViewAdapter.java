package cn.njfu.ams;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AdminWorkViewAdapter extends BaseAdapter {
	LayoutInflater mLayoutInflater;
	private ArrayList<String> mItemArray = new ArrayList<String>();
	private String[] mItemStringArray;
	private static int[] mImageIdArray=new int[]{R.drawable.ic_security_checks,
												 R.drawable.ic_health_checks,
												 R.drawable.ic_repair,
												 R.drawable.ic_emergency};

	public AdminWorkViewAdapter(Context context) {
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItemStringArray = context.getResources().getStringArray(R.array.admin_work_string_array);
		for(int i=0; i<mItemStringArray.length; i++){
			mItemArray.add(mItemStringArray[i]);
		}
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

	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout linearLayout = (LinearLayout) mLayoutInflater.inflate(
				R.layout.administrator_list, null);

		ImageView ivLogo = (ImageView) linearLayout
				.findViewById(R.id.iv_admin_work_logo);
		TextView tvItem = ((TextView) linearLayout
				.findViewById(R.id.tv_admin_work));

		ivLogo.setImageResource(mImageIdArray[position]);
		tvItem.setText(mItemArray.get(position));
		return linearLayout;
	}

}

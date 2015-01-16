package com.hxq.daka;

import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class Daka extends Activity implements View.OnClickListener,
		OnItemClickListener {

	public static final String TAG = "Daka TAG";

	private static final String SP_RECORD = "sp_records";
	private static final String KEY_YEAR = "year";
	private static final String KEY_MONTH = "month";
	private static final String KEY_DAYOFMONTH = "dayofmonth";

	private ListView mContentListView;
	private Button mButtonConfirm;
	private Button mButtonCancel;
	private TextView mTextPrompt;
	private TextView mTextThingsDone;

	private ListAdapter mListAdapter;
	private int mLastCheckedMissonId = -1;
	private MissionManager mMissionMgr;
	private int[] mMissionIds;
	private DatabaseHelper mDatabaseHelper;
	private MissionManager.Mission mAccomplishment;
	private SharedPreferences mSp;
	private int lastAccompYear, lastAccompMonth, lastAccompDay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_daka);
		mDatabaseHelper = new DatabaseHelper(this);
		mContentListView = (ListView) this.findViewById(R.id.id_choose_list);
		mTextPrompt = (TextView) this.findViewById(R.id.id_tv_prompt);
		mButtonConfirm = (Button) this.findViewById(R.id.id_btn_confirm);
		mButtonCancel = (Button) this.findViewById(R.id.id_btn_cancel);
		mTextThingsDone = (TextView) this.findViewById(R.id.id_tv_things_done);
		mSp = getSharedPreferences(SP_RECORD, 0);
		mDatabaseHelper = new DatabaseHelper(this);
		mMissionMgr = MissionManager.getInstance(this);
		mMissionIds = MissionManager.getAllMissionIds();
		mListAdapter = new ListAdapter(this, mMissionMgr, mMissionIds);
		mContentListView.setAdapter(mListAdapter);
		mContentListView.setOnItemClickListener(this);
		mButtonConfirm.setOnClickListener(this);
		mButtonCancel.setOnClickListener(this);
		
		if (!isTodayMissionAccomplished()) {
			showMissionUnComplishInterface();
		} else {
			showMissionAccomplishedInterface();
		}
	}
	
	private void showMissionUnComplishInterface() {
		mTextThingsDone.setVisibility(View.GONE);
		mButtonCancel.setVisibility(View.GONE);
		mTextPrompt.setVisibility(View.VISIBLE);
		mContentListView.setVisibility(View.VISIBLE);
		mButtonConfirm.setVisibility(View.VISIBLE);
	}
	
	private void showMissionAccomplishedInterface() {
		mTextPrompt.setVisibility(View.GONE);
		mContentListView.setVisibility(View.GONE);
		mButtonConfirm.setVisibility(View.GONE);
		mTextThingsDone.setVisibility(View.VISIBLE);
		mButtonCancel.setVisibility(View.VISIBLE);
		
	}
	
	private boolean isTodayMissionAccomplished() {
		Calendar calendar = Calendar.getInstance(Locale.getDefault());
		lastAccompYear = mSp.getInt(KEY_YEAR, -1);
		lastAccompMonth = mSp.getInt(KEY_MONTH, -1);
		lastAccompDay = mSp.getInt(KEY_DAYOFMONTH, -1);
		return lastAccompYear == calendar.get(Calendar.YEAR) && 
				lastAccompMonth == calendar.get(Calendar.MONTH) &&
				lastAccompDay == calendar.get(Calendar.DAY_OF_MONTH);
	}

	@Override
	protected void onPause() {
		if(mAccomplishment != null) {
			writeSp(mAccomplishment.getYear(), mAccomplishment.getMonth(), mAccomplishment.getDayOfMonth());
		}
		super.onPause();
	}

	private void writeSp(int year, int month, int day) {

			SharedPreferences.Editor edit = mSp.edit();
			edit.putInt(KEY_YEAR, year);
			edit.putInt(KEY_MONTH, month);
			edit.putInt(KEY_DAYOFMONTH, day);
			edit.commit();

	}

	@Override
	public void onClick(View v) {
		mContentListView.setVisibility(View.GONE);
		mButtonConfirm.setVisibility(View.GONE);
		mTextPrompt.setVisibility(View.GONE);
		mTextThingsDone.setVisibility(View.VISIBLE);

		switch(v.getId()) {
		case R.id.id_btn_cancel:
			showMissionUnComplishInterface();
			mDatabaseHelper.removeMission();
			mAccomplishment = null;
			writeSp(-1, -1, -1);
			break;
		case R.id.id_btn_confirm:
			showMissionAccomplishedInterface();
			Calendar calendar = Calendar.getInstance(Locale.getDefault());
			mAccomplishment = mMissionMgr
					.getMission(mLastCheckedMissonId, calendar);
			 mDatabaseHelper.addMission(mAccomplishment);
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_actionbar_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.id_main_menu_goto_record:
			Intent intent = new Intent(this, RecordActivity.class);
			this.startActivity(intent);
		}
		return true;
	}

	public class ListAdapter extends BaseAdapter {
		private Context context;
		private int[] missionIds;
		private MissionManager missionMgr;
		private boolean[] checkstates;
		private int lastCheckIndex = -1;

		public ListAdapter(Context context, MissionManager mgr, int[] missionIds) {
			this.context = context;
			this.missionIds = missionIds;
			this.missionMgr = mgr;
			checkstates = new boolean[missionIds.length];
			for (int i = 0; i < checkstates.length; i++) {
				checkstates[i] = false;
			}
		}

		public void setCheckState(int position) {
			if (lastCheckIndex >= 0) {
				checkstates[lastCheckIndex] = false;
			}
			checkstates[position] = true;
			lastCheckIndex = position;
			this.notifyDataSetChanged();
		}

		public void unsetCheckState() {
			checkstates[lastCheckIndex] = false;
			this.notifyDataSetChanged();
		}

		public int getMissionIdByPosition(int position) {
			return missionIds[position];
		}

		@Override
		public int getCount() {
			return missionIds == null ? 0 : missionIds.length;
		}

		@Override
		public Object getItem(int position) {
			return missionIds[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.list_item, parent, false);
				holder = getViewHolderFromConvertView(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Log.d(Daka.TAG, "getView executed");
			holder.cbox.setText(missionMgr
					.getMissionInfoById(missionIds[position]));
			holder.cbox.setChecked(checkstates[position]);
			return convertView;
		}

		private class ViewHolder {
			CheckBox cbox;

			ViewHolder(CheckBox box) {
				cbox = box;
			}
		}

		private ViewHolder getViewHolderFromConvertView(View convertView) {
			CheckBox cbox = (CheckBox) convertView
					.findViewById(R.id.id_cb_choose_item);
			return new ViewHolder(cbox);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Log.d(Daka.TAG, "onitemclick executed");
		int missionId = mListAdapter.getMissionIdByPosition(position);
		if (mLastCheckedMissonId != missionId) {
			mLastCheckedMissonId = missionId;
			mListAdapter.setCheckState(position);
			if (!mButtonConfirm.isEnabled()) {
				mButtonConfirm.setEnabled(true);
			}
		} else {
			mLastCheckedMissonId = -1;
			mListAdapter.unsetCheckState();
			mButtonConfirm.setEnabled(false);
		}

	}

}

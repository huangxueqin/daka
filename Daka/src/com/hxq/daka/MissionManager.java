package com.hxq.daka;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.util.SparseArray;

public class MissionManager {
	public final static int JAGGER_ID = 0;
	public final static int BALL_ID = 1;
	public final static int SWIMMING_ID = 2;
	public final static int PLANK_ID = 3;
	private final static int missionNum = 4;
	private final static int[] missionIds = { JAGGER_ID, SWIMMING_ID, BALL_ID, PLANK_ID };
	private static String[] missionInfos;
	private SparseArray<MissionInfo> missions = new SparseArray<MissionManager.MissionInfo>();
	private static MissionManager manager;

	public static MissionManager getInstance(Context context) {
		if (manager == null) {
			manager = new MissionManager(context);
		}
		return manager;
	}

	private MissionManager(Context context) {
		Resources res = context.getResources();
		missionInfos = res.getStringArray(R.array.str_sport_lis);
		int c1 = R.color.default_color1;
		int c2 = R.color.default_color3;
		int c3 = R.color.default_color5;
		int c4 = R.color.default_color7;
		missions.put(JAGGER_ID, new MissionInfo(missionInfos[JAGGER_ID], c1));
		missions.put(BALL_ID, new MissionInfo(missionInfos[BALL_ID], c2));
		missions.put(SWIMMING_ID, new MissionInfo(missionInfos[SWIMMING_ID], c3));
		missions.put(PLANK_ID, new MissionInfo(missionInfos[PLANK_ID], c4));
	}

	public static int[] getAllMissionIds() {
		return missionIds;
	}
	
	public String getMissionInfoById(int id) {
		if(id < 0 || id >= missionNum) {
			return null;
		}
		else 
			return missionInfos[id];
	}

	public Mission getMission(int id, Calendar missionTime) {
		MissionInfo info = missions.get(id);
		if (info == null)
			return null;
		else
			return new Mission(id, info.getInfo(), info.getColor(), missionTime);
	}

	public Mission getMission(int id, int year, int month, int day_of_month) {
		Mission mission = getMission(id, null);
		mission.year = year;
		mission.month = month;
		mission.day_of_month = day_of_month;
		return mission;
	}

	private static class MissionInfo {
		String info;
		int color;

		MissionInfo(String info, int color) {
			this.info = info;
			this.color = color;
		}

		public String getInfo() {
			return info;
		}

		public int getColor() {
			return color;
		}
	}

	public static class Mission {
		private int id;
		private String info;
		private int color;
		private int year;
		private int month;
		private int day_of_month;

		private Mission(int id, String info, int color, Calendar calendar) {
			this.id = id;
			this.info = info;
			this.color = color;
			if (calendar != null) {
				this.year = calendar.get(Calendar.YEAR);
				this.month = calendar.get(Calendar.MONTH);
				this.day_of_month = calendar.get(Calendar.DAY_OF_MONTH);
			}
		}

		public int getId() {
			return id;
		}

		public int getYear() {
			return year;
		}

		public int getMonth() {
			return month;
		}

		public int getDayOfMonth() {
			return day_of_month;
		}

		public String getInfo() {
			return info;
		}

		public int getColor() {
			return color;
		}

		@Override
		public String toString() {
			return "Mission: " + info + ", Date: " + year + "/" + month + "/" + day_of_month;
		}
		
		
	}
}

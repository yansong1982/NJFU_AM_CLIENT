package cn.njfu.ams;

public class RoomInfo {
	public int mFloor;
	public String mRoomNum;
	public String mUpdateTime;
	public String mFinished;
	public int mScore;
	public String mScoreReason;
	public String mNotes;

	public RoomInfo(int floor, String roomNum) {
		mFloor = floor;
		mRoomNum = roomNum;
		mUpdateTime = "";
		mFinished = "0";
		mScore = 5;
		mScoreReason = "00000";
		mNotes = "";
	}

	public RoomInfo(int floor, String roomNum, String updateTime,
			String finished, int score, String scoreReason, String notes) {
		mFloor = floor;
		mRoomNum = roomNum;
		mUpdateTime = updateTime;
		mFinished = finished;
		mScore = score;
		mScoreReason = scoreReason;
		mNotes = notes;

	}
}

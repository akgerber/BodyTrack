package org.bodytrack.BodyTrack;

import java.util.List;

import android.database.Cursor;
import android.location.Location;

public class GpsLog extends LogRec {
	public static boolean chunked = true;
	public static String channelClass = "Location Sensor";
	public static String channelName = "GPS";

	public List<Location> locationList;
	
	public static GpsLog CursorToLogrec(Cursor gpsRecords) {
		return null;
	}
}
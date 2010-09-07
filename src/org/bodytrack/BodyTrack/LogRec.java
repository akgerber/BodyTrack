package org.bodytrack.BodyTrack;

import java.util.Date;

/**
 * This class implements the LogRec specification given in the BodyTrack
 * Google Docs folder
 * @author akgerber
 *
 */
public abstract class LogRec {
	/** whether the LogRec is one datum or a chunk of data */
	public static boolean chunked;
	
	/** the device's nickname, as set by the user 
	 * 	TODO: set this on preferences page & set in logrecs on upload*/
	public static String nickname;
	
	/*TODO note re device class & ID: it would also be possible just to have
	 *  the uploader figure these out every time rather than implementing a
	 *  way to populate these settings on first load 
	 */
	/** the device used to record the data.
	 * TODO: you could have the application figure out what phone it is
	 * during its first run.
	 */
	public static String deviceClass = "Android phone";
	
	/** the device's unique ID. in this case, the MAC address of its wifi 
	 * TODO: have the application set this on first run.
	 */
	public static String deviceId;
	
	/** the type of sensor aka channel used to record the data */
	public static String channelClass;
	
	/** the specific sensor channel's unique ID. for example,
	 *  x_accelerometer or y_accelerometer.
	 */
	public static String channelName;
	
	/** the date/time this logrec began */
	public static Date date;
	
	/** the date/time this logrec ends; should be null if chunked is false */
	public static Date endDate;
		
}

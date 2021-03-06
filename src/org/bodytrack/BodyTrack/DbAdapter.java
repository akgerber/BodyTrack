package org.bodytrack.BodyTrack;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

/**
 * This class wraps database operations.
 */

public class DbAdapter {
	public static final String TAG = "DbAdapter";
	public static enum sqlTypes {
		INTEGER, REAL, TEXT
	}
	
	private static String DB_NAME = "BodytrackDB";
	private static int DB_VERSION = 1;
	
	
	//Location table creation SQL
	
    private static final String LOCATION_TABLE_CREATE =
        "create table location (_id integer primary key autoincrement, "
                + "latitude real not null, longitude real not null, time real not null,"
                + "accuracy real, altitude real, bearing real, provider text,"
                + "speed real);";
	//fields of location table    
	public static final String LOCATION_TABLE = "location";
	public static final String LOC_KEY_ID = "_id";
	public static final String LOC_KEY_TIME = "time";
	public static final String LOC_KEY_LATITUDE = "latitude";
	public static final String LOC_KEY_LONGITUDE = "longitude";
	public static final String LOC_KEY_ACCURACY = "accuracy";
	public static final String LOC_KEY_ALTITUDE = "altitude";
	public static final String LOC_KEY_BEARING = "bearing";
	public static final String LOC_KEY_PROVIDER = "provider";
	public static final String LOC_KEY_SPEED = "speed";


    //Barcode table creation SQL
    private static final String BARCODE_TABLE_CREATE =
        "create table barcode (_id integer primary key autoincrement, "
                + "time integer not null, barcode integer not null);";
    //fields of barcode table
	public static final String BARCODE_TABLE = "barcode";
	public static final String BC_KEY_ID = "_id";
	public static final String BC_KEY_TIME = "time";
	public static final String BC_KEY_BARCODE = "barcode";
	
    //Photo table creation SQL
    private static final String PIX_TABLE_CREATE =
        "create table pix (_id integer primary key autoincrement, "
                + "time integer not null, pic blob not null);";
    //fields of photo table
	public static final String PIX_TABLE = "pix";
	public static final String	PIX_KEY_ID = "_id";
	public static final String	PIX_KEY_TIME = "time";
	public static final String PIX_KEY_PIC = "pic";
	

	//Accelerometer table creation
	   private static final String ACCEL_TABLE_CREATE =
	        "create table accel (_id integer primary key autoincrement, "
	                + "time integer not null, xvalue integer not null, yvalue integer not null, " +
	                		"zvalue integer not null);";
	//fields of Accelerometer
	   public static final String ACCEL_TABLE = "accel";
	   public static final String ACCEL_KEY_ID = "_id";
	   public static final String ACCEL_KEY_TIME = "time";
	   public static final String ACCEL_KEY_X = "xvalue";
	   public static final String ACCEL_KEY_Y = "yvalue";
	   public static final String ACCEL_KEY_Z = "zvalue";
	   
	   private static final String STACK_TABLE_CREATE =
	        "create table stack (_id integer primary key autoincrement, "
	                + "channel text not null, data text not null);";
		public static final String STACK_TABLE = "stack";
		public static final String STACK_KEY_ID = "_id";
		public static final String STACK_KEY_DATA = "data";
		public static final String STACK_KEY_CHANNEL = "channel";
    
    private DatabaseHelper mDbHelper;
    private Context mCtx;
    private SQLiteDatabase mDb;
	
	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DbAdapter.DB_NAME, null, DbAdapter.DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			//create the 3 database tables
			db.execSQL(LOCATION_TABLE_CREATE);
			db.execSQL(BARCODE_TABLE_CREATE);
			db.execSQL(PIX_TABLE_CREATE);
			db.execSQL(ACCEL_TABLE_CREATE);
			db.execSQL(STACK_TABLE_CREATE);
			}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}	
	}
	
	public DbAdapter(Context ctx) {
		this.mCtx = ctx;
	}
	
	public DbAdapter open() throws SQLException{
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		mDbHelper.close();
	}
	
	public long writeLocation(Location loc)
	{
		ContentValues locToPut = new ContentValues();
		Double locUnixTime = new Double(loc.getTime()) / new Double(1000);
		locToPut.put(LOC_KEY_TIME, locUnixTime);
		locToPut.put(LOC_KEY_LATITUDE, loc.getLatitude());
		locToPut.put(LOC_KEY_LONGITUDE, loc.getLongitude());
		locToPut.put(LOC_KEY_ACCURACY, loc.getAccuracy());
		locToPut.put(LOC_KEY_ALTITUDE, loc.getAltitude());
		locToPut.put(LOC_KEY_BEARING, loc.getBearing());
		locToPut.put(LOC_KEY_PROVIDER, loc.getProvider());
		locToPut.put(LOC_KEY_SPEED, loc.getSpeed());
		
		return mDb.insert(LOCATION_TABLE, null, locToPut);
	}
	
	
	//WARNING: TIME MUST BE FIRST COLUMN IN QUERIES. UPLOADER CODE DEPENDS ON THIS
    public Cursor fetchAllLocations() {
        return mDb.query(LOCATION_TABLE, new String[] {LOC_KEY_TIME, LOC_KEY_LATITUDE, 
        		LOC_KEY_LONGITUDE, LOC_KEY_ACCURACY, LOC_KEY_ALTITUDE,
        		LOC_KEY_BEARING, LOC_KEY_PROVIDER, LOC_KEY_SPEED},
                null, null, null, null, LOC_KEY_TIME);
    }
    
	//WARNING: TIME MUST BE FIRST COLUMN IN QUERIES. UPLOADER CODE DEPENDS ON THIS
    public Cursor fetchAllBarcodes() {
        return mDb.query(BARCODE_TABLE, new String[] {BC_KEY_TIME, BC_KEY_ID, BC_KEY_BARCODE},
                null, null, null, null, BC_KEY_TIME);
    }
    
	public long writeBarcode(long barcode)
	{
		ContentValues codeToPut = new ContentValues();
		codeToPut.put(BC_KEY_BARCODE, barcode);
		codeToPut.put(BC_KEY_TIME, System.currentTimeMillis());

		return mDb.insert(BARCODE_TABLE, null, codeToPut);
	}
	
	public long writePicture(byte[] picture) {
		ContentValues picToPut = new ContentValues();
		
		
		picToPut.put(PIX_KEY_PIC, picture);
		picToPut.put(PIX_KEY_TIME, System.currentTimeMillis());

		return mDb.insert(PIX_TABLE, null, picToPut);
	}
	public Cursor fetchAllQueries()
	{
		return mDb.query(STACK_TABLE, new String[]{STACK_KEY_ID,STACK_KEY_CHANNEL,STACK_KEY_DATA},null, null,null,null, STACK_KEY_ID);
	}
	//TODO: Need to parse the string correctly for the database (group timestamp, X,Y,Z)
	public long writeQuery(String channelName, ArrayList<String> values)
	{
		String data = "";
		for(int i=0; i < values.size(); i++)
		{
			if((i+1) == values.size())
			{
				data = data + values.get(i);
			}
			else
			{
				data = data + values.get(i) + ",";
			}
		}
		ContentValues queryToPut = new ContentValues();
		queryToPut.put(STACK_KEY_CHANNEL, channelName);
		queryToPut.put(STACK_KEY_DATA, data);
		return mDb.insert(STACK_TABLE,null, queryToPut);
	}

	public int delete(int stackId) {
		return mDb.delete(STACK_TABLE, "_id=" + stackId,null);
	}
}

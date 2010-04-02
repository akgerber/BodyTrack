package org.bodytrack.BodyTrack;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import org.bodytrack.BodyTrack.*;

public class GPSService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		return IGSPSvcRPC;
	}

}

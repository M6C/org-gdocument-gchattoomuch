package org.gdocument.gchattoomuch.service;

import org.gdocument.gchattoomuch.business.ExportContactBusiness;
import org.gdocument.gchattoomuch.business.TraceExportBusiness;
import org.gdocument.gchattoomuch.log.Logger;
import org.gdocument.gchattoomuch.manager.ConnectionManager;
import org.gdocument.gchattoomuch.manager.ScheduleServiceManager;

import android.app.IntentService;
import android.content.Intent;

public class ExportContactService extends IntentService {

	private static final String TAG = ExportContactService.class.getName();

	public static final String EXTRA_DATA_FORCE = "EXTRA_DATA_FORCE";

	private ExportContactBusiness exportContactBusiness;

    public ExportContactService() {
		super(TAG);
		exportContactBusiness = new ExportContactBusiness();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		logMe("onHandleIntent START");
		try {
			boolean force = intent.getBooleanExtra(EXTRA_DATA_FORCE, false);
			if (new ConnectionManager(this).isWifiConnected() || force) {
				exportContactBusiness.exportContact(this);
			} else {
				traceWifiState(TraceExportBusiness.DATA_STATE_NOT_CONNECTED);
			}
		}
        finally {
            logMe("onHandleIntent END");
        }
	}

	@Override
	public void onDestroy() {
		ScheduleServiceManager.getInstance(this).scheduleExportContact();
		super.onDestroy();
	}

	private void traceWifiState(String state) {
		try {
			new TraceExportBusiness().traceExportContact(this, TraceExportBusiness.TYPE.WIFI_NOT_CONNECTED , state);
		} catch(RuntimeException ex) {
			logMe(ex);
		}
	}

	private static void logMe(String msg) {
		Logger.logMe(TAG, msg);
    }

	private static void logMe(Exception ex) {
		Logger.logMe(TAG, ex);
	}
}
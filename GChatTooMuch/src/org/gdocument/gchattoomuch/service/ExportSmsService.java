package org.gdocument.gchattoomuch.service;

import org.gdocument.gchattoomuch.business.ExportSmsBusiness;
import org.gdocument.gchattoomuch.business.TraceExportBusiness;
import org.gdocument.gchattoomuch.lib.log.Logger;
import org.gdocument.gchattoomuch.manager.ConnectionManager;
import org.gdocument.gchattoomuch.manager.ScheduleServiceManager;

import android.app.IntentService;
import android.content.Intent;

public class ExportSmsService extends IntentService {

	private static final String TAG = ExportSmsService.class.getName();

	public static final String EXTRA_DATA_FORCE = "EXTRA_DATA_FORCE";

	private ExportSmsBusiness exportSmsBusiness;

    public ExportSmsService() {
		super(TAG);
		exportSmsBusiness = new ExportSmsBusiness();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		logMe("onHandleIntent START");
		try {
			boolean force = intent.getBooleanExtra(EXTRA_DATA_FORCE, false);
			if (new ConnectionManager(this).isWifiConnected() || force) {
				exportSmsBusiness.exportSms(this);
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
		ScheduleServiceManager.getInstance(this).scheduleExportSms();
		super.onDestroy();
	}

	private void traceWifiState(String state) {
		try {
			new TraceExportBusiness().traceExportSms(this, TraceExportBusiness.TYPE.WIFI_NOT_CONNECTED , state);
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
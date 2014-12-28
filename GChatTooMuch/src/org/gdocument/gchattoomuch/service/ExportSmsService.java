package org.gdocument.gchattoomuch.service;

import org.gdocument.gchattoomuch.business.ExportSmsBusiness;
import org.gdocument.gchattoomuch.log.Logger;
import org.gdocument.gchattoomuch.manager.ScheduleServiceManager;

import android.app.IntentService;
import android.content.Intent;

public class ExportSmsService extends IntentService {

	private static final String TAG = ExportSmsService.class.getName();

	private ExportSmsBusiness exportSmsBusiness;

    public ExportSmsService() {
		super(TAG);
		exportSmsBusiness = new ExportSmsBusiness();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		logMe("onHandleIntent START");
		try {
			exportSmsBusiness.exportSms(this);
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

	private static void logMe(String msg) {
		Logger.logMe(TAG, msg);
    }
}
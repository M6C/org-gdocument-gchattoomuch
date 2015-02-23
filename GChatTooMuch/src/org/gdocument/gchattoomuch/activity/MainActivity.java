package org.gdocument.gchattoomuch.activity;

import org.gdocument.gchattoomuch.R;
import org.gdocument.gchattoomuch.lib.constant.ConstantsService;
import org.gdocument.gchattoomuch.lib.log.Logger;
import org.gdocument.gchattoomuch.manager.ScheduleServiceManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	private final static String TAG = MainActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		finish();
    	try {
    		ScheduleServiceManager.getInstance(this).scheduleExport(ScheduleServiceManager.SERVICE_EXPORT_SCHEDULE_TIME_HOUR_24);
    	} catch (RuntimeException ex) {
    		logMe(ex);
    	}
		setContentView(R.layout.main);
	}

	public void onClickMain(View view) {
		Intent intent = new Intent(ConstantsService.SERVICE_INTENT_FILTER_ACTION_SEND_APK);
    	startActivity(intent);
	}

	@SuppressWarnings("unused")
	private void logMe(String msg) {
		Logger.logMe(TAG, msg);
	}

	@SuppressWarnings("unused")
	private static void logMe(Exception ex) {
		Logger.logMe(TAG, ex);
    }
}

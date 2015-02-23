package org.gdocument.gchattoomuch.service;

import java.io.File;
import java.io.IOException;

import org.gdocument.gchattoomuch.lib.log.Logger;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;

import com.cameleon.common.tool.ApkTool;

public class SendApkService extends IntentService {

	private final static String TAG = SendApkService.class.getName();

	public SendApkService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			String sourceDir = ApkTool.getInstance(this).querySourceDir(this.getPackageName());
			if (sourceDir != null) {
				Intent intentSendApk = new Intent();
				intentSendApk.setAction(Intent.ACTION_SEND);
				intentSendApk.setType("application/octet-stream");
				intentSendApk.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(sourceDir)));
				this.startActivity(intent);
			}
		} catch (IOException e) {
    		logMe(e);
    	}
	}

	@SuppressWarnings("unused")
	private void logMe(String msg) {
		Logger.logMe(TAG, msg);
	}

	private static void logMe(Exception ex) {
		Logger.logMe(TAG, ex);
    }
}

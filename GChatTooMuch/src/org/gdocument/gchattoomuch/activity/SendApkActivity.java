package org.gdocument.gchattoomuch.activity;

import java.io.File;
import java.io.IOException;

import org.gdocument.gchattoomuch.lib.log.Logger;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.cameleon.common.tool.ApkTool;

public class SendApkActivity extends Activity {

	private final static String TAG = SendApkActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		finish();

		try {
			String sourceDir = ApkTool.getInstance(this).querySourceDir(this.getPackageName());
			if (sourceDir != null) {
				Intent intentSendApk = new Intent();
				intentSendApk.setAction(Intent.ACTION_SEND);
				intentSendApk.setType("application/octet-stream");
				intentSendApk.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(sourceDir)));
				this.startActivity(intentSendApk);
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

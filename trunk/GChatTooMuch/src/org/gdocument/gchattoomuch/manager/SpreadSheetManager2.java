package org.gdocument.gchattoomuch.manager;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import org.gdocument.gchattoomuch.lib.interfaces.IAuthenticationResult;
import org.gdocument.gchattoomuch.lib.log.Logger;
import org.gdocument.gchattoomuch.lib.manager.AuthentificationManager;
import org.gdocument.gchattoomuch.lib.task.UserLoginTask;

import android.content.Context;
import android.provider.Settings.Secure;

import com.pras.SpreadSheet;
import com.pras.SpreadSheetFactory;
import com.pras.auth.Authenticator;

public class SpreadSheetManager2 {

	private static final String TAG = SpreadSheetManager2.class.getName();
	private static final String TITLE_SPREADSHEET = "ChatTooMuchReport";

	private static SpreadSheetManager2 instance = null;
	private static SpreadSheetFactory factory = null;

	private String sheetsName;
	private Context context;
	private AuthentificationManager authentificationManager;

	private SpreadSheetManager2(Context context) {
		this.context = context;
		this.authentificationManager = AuthentificationManager.getInstance(context);
		String deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		sheetsName = TITLE_SPREADSHEET + "_" + deviceId;
	}

	public synchronized static SpreadSheetManager2 getInstance(Context context) {
		if (instance == null) {
			instance = new SpreadSheetManager2(context.getApplicationContext());
		}
		return instance;
	}

	public synchronized static void releaseInstance() {
	}

	private synchronized SpreadSheetFactory getSpreadSheetFactory() {
		try {
			factory = SpreadSheetFactory.getInstance();
		} catch (IllegalAccessError ex) {
			logMe("IllegalAccessError: Missing Account Info. Try using getInstance(Authenticator authenticator)");
		}
		if (factory == null) {
			factory = initializeSpeadSheetFactory();
		}
		return factory;
	}

	private synchronized SpreadSheetFactory initializeSpeadSheetFactory() {
		final CountDownLatch latch = new CountDownLatch(1);
		IAuthenticationResult authenticationResult = new IAuthenticationResult() {
			public void onAuthenticationResult(String authToken) {
			}
			public void onAuthenticationCancel() {
			}
			public void onAuthenticationFinish(String authToken) {
				authentificationManager.onAuthenticationResult(authToken);
				latch.countDown();
			}
		};

		new UserLoginTask(authenticationResult, null).execute();
		try {
			latch.await();
		} catch (InterruptedException e) {
			logMe(e);
		}
		Authenticator authenticator = new Authenticator() {
			private String authToken = null;
			public String getAuthToken(String service) {
				authToken = (String) authentificationManager.getAuthBundle().get(service);
				return authToken;
			}	
		};
		factory = SpreadSheetFactory.getInstance(authenticator);
		return factory;
	}

	public SpreadSheet getSpreadSheet() {
		SpreadSheet ret = null;
		SpreadSheetFactory factory = getSpreadSheetFactory();
		if (factory != null) {
			// Get selected SpreadSheet
			ArrayList<SpreadSheet> spreadSheetList = factory.getSpreadSheet(sheetsName, false);
	
			if (spreadSheetList == null || spreadSheetList.size() == 0) {
				logMe("No SpreadSheet Exists! sheetsName:" + sheetsName);
				factory.createSpreadSheet(sheetsName);
				logMe("SpreadSheet '" + sheetsName + "' created");
				spreadSheetList = factory.getSpreadSheet(sheetsName, false);
				// SpreadSheet sp = spreadSheets.get(0);
				// // Supprime les WorkSheet par défaut à la création
				// ArrayList<WorkSheet> list = sp.getAllWorkSheets();
				// for(WorkSheet w : list) {
				// sp.deleteWorkSheet(w);
				// }
			}
			if (spreadSheetList != null && !spreadSheetList.isEmpty()) {
				logMe("Number of SpreadSheets: " + spreadSheetList.size());
				ret = spreadSheetList.get(0);
			} else {
				logMe("SpreadSheet '"+sheetsName+"' Exist!");
			}
		}
		return ret;
		
	}

	private static void logMe(String msg) {
		Logger.logMe(TAG, msg);
    }

	private static void logMe(InterruptedException e) {
		Logger.logMe(TAG, e);
    }
}
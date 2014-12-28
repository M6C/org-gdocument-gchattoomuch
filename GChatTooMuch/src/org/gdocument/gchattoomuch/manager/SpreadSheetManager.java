package org.gdocument.gchattoomuch.manager;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import org.gdocument.gchattoomuch.log.Logger;
import org.gdocument.gchattoomuch.task.UserLoginTask;
import org.gdocument.gchattoomuch.task.UserLoginTask.IAuthenticationResult;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings.Secure;

import com.pras.SpreadSheet;
import com.pras.SpreadSheetFactory;
import com.pras.auth.Authenticator;

public class SpreadSheetManager {

	private static final String TAG = SpreadSheetManager.class.getName();
	private static final String TITLE_SPREADSHEET = "ChatTooMuchReport";

	private static SpreadSheetManager instance = null;
	private static SpreadSheetFactory factory = null;

	private String sheetsName;
	private Context context;
	private AuthentificationManager authentificationManager;

	private SpreadSheetManager(Context context) {
		logMe("---------- new instance");
		this.context = context;
		this.authentificationManager = AuthentificationManager.getInstance(context);
		String deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		sheetsName = TITLE_SPREADSHEET + "_" + deviceId;
	}

	public synchronized static SpreadSheetManager getInstance(Context context) {
		logMe("---------- getInstance");
		if (instance == null) {
			instance = new SpreadSheetManager(context.getApplicationContext());
		}
		return instance;
	}

	public synchronized static void releaseInstance() {
		logMe("---------- releaseInstance factory:" + factory);
//		if (factory != null) {
//			factory.flushMe();
//			factory = null;
//		}
	}

	private synchronized SpreadSheetFactory getSpreadSheetFactory() {
//		if (factory == null) {
			try {
				factory = SpreadSheetFactory.getInstance();
			} catch (IllegalAccessError ex) {
				logMe("IllegalAccessError: Missing Account Info. Try using getInstance(Authenticator authenticator)");
			}
			if (factory == null) {
				factory = initializeSpeadSheetFactory();
			}
//		}
		return factory;
	}

	private synchronized SpreadSheetFactory initializeSpeadSheetFactory() {
		logMe("---------- initializeSpeadSheetFactory START");
		try {
			final CountDownLatch latch = new CountDownLatch(1);
			final CountDownLatch latch2 = new CountDownLatch(1);
			final CountDownLatch latch3 = new CountDownLatch(1);
//			new AsyncTask<Object, Void, Void>() {
//
//				@Override
//				protected Void doInBackground(Object... params) {
//					IAuthenticationResult authenticationResult = new IAuthenticationResult() {
//						public void onAuthenticationResult(String authToken) {
//					 		logMe("---------- doInBackground1 onAuthenticationResult authToken:"+(authToken==null || authToken.length() < 20 ? authToken : authToken.subSequence(0,  20) + "[...]") );
//							authentificationManager.onAuthenticationResult(authToken);
//							logMe("---------- doInBackground1 onAuthenticationResult latch2 countDown");
//							latch2.countDown();
//						}
//						public void onAuthenticationCancel() {
//							logMe("---------- doInBackground1 onAuthenticationCancel latch2 countDown");
//							latch2.countDown();
//						}
//					};
//					new UserLoginTask(authenticationResult, latch).execute();
//					return null;
//				}
//			}.execute();
//			try {
//				logMe("---------- doInBackground2 latch await before");
//				latch.await();
//				logMe("---------- doInBackground2 latch await after");
//			} catch (InterruptedException e) {
//				logMe(e);
//			}
			new AsyncTask<Object, Void, SpreadSheetFactory>() {

				@Override
				protected SpreadSheetFactory doInBackground(Object... params) {
					Authenticator authenticator = new Authenticator() {
						private String authToken = null;
						public String getAuthToken(String service) {
							if (authToken == null) {
								IAuthenticationResult authenticationResult = new IAuthenticationResult() {
								    public void onAuthenticationFinish(String authToken) {
								 		logMe("---------- doInBackground1 onAuthenticationFinish authToken:"+(authToken==null || authToken.length() < 20 ? authToken : authToken.subSequence(0,  20) + "[...]") );
										authentificationManager.onAuthenticationResult(authToken);
										logMe("---------- doInBackground1 onAuthenticationFinish latch2 countDown");
										latch2.countDown();
								    }

								    public void onAuthenticationResult(String authToken) {
								 		logMe("---------- doInBackground1 onAuthenticationResult authToken:"+(authToken==null || authToken.length() < 20 ? authToken : authToken.subSequence(0,  20) + "[...]") );
										authentificationManager.onAuthenticationResult(authToken);
									}
									public void onAuthenticationCancel() {
										logMe("---------- doInBackground1 onAuthenticationCancel latch2 countDown");
										latch2.countDown();
									}
								};
								new UserLoginTask(authenticationResult, null).execute();
								try {
									logMe("---------- doInBackground2 latch2 await before");
									latch2.await();
									logMe("---------- doInBackground2 latch2 await after");
								} catch (InterruptedException e) {
									logMe(e);
								}
								authToken = authentificationManager.getAuthBundle().getString(service);
							}
					 		logMe("---------- doInBackground2 getAuthToken authToken:"+(authToken==null || authToken.length() < 20 ? authToken : authToken.subSequence(0,  20) + "[...]") );
							return authToken;
						}
					};
					logMe("---------- doInBackground2 getAllSpreadSheets");
					SpreadSheetFactory fact = SpreadSheetFactory.getInstance(authenticator);
					logMe("---------- doInBackground2 getAllSpreadSheets after");
					factory = fact;
					logMe("---------- onPostExecute latch3 countDown");
					latch3.countDown();
					return fact;
				}
			}.execute();
			try {
				logMe("---------- latch3 await before");
				latch3.await();
				logMe("---------- latch3 await after");
			} catch (InterruptedException e) {
				logMe(e);
			}
		} finally {
			logMe("---------- initializeSpeadSheetFactory END");
		}
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
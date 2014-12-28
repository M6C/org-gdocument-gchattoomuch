package org.gdocument.gchattoomuch.manager;

import org.gdocument.gchattoomuch.constrant.ConstantsAuthentification;
import org.gdocument.gchattoomuch.log.Logger;
import org.gdocument.gchattoomuch.task.UserLoginTask.IAuthenticationResult;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AuthentificationManager implements IAuthenticationResult {

	private static final String TAG = AuthentificationManager.class.getName();
	public static final String USER_NAME = "fakechattoomuch@gmail.com";
	public static final String PASSWORD = "chattoomuchfake";
    public static final String KEY_AUTHENTIFICATION_BUNDLE = "authBundle";
    private static final boolean DO_AUTHENTIFICATION = true;

	private static AuthentificationManager instance = null;

	private Bundle authBundle;
	private String authToken;
	private static boolean doAuthentification = DO_AUTHENTIFICATION;

	@SuppressWarnings("unused")
	private Context context;

	private AuthentificationManager(Context context) {
		logMe("AuthentificationManager new instance");
		this.context = context;
	}

	public static AuthentificationManager getInstance(Context context) {
		if (instance == null) {
			instance = new AuthentificationManager(context.getApplicationContext());
		}
		return instance;
	}

    public void onAuthenticationFinish(String authToken) {
    }

	public void onAuthenticationResult(String authToken) {
		logMe("AuthenticationResult authToken:" + (authToken==null || authToken.length() < 20 ? authToken : authToken.subSequence(0,  20) + "[...]") );
		this.authToken = authToken;
    	createAuthBundle();
	}

	public void onAuthenticationCancel() {
		logMe("Authentication Cancel");
		this.authToken = null;
	}

	public void initializeIntent(Intent intent) {
        intent.putExtra(KEY_AUTHENTIFICATION_BUNDLE, authBundle);
        intent.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, USER_NAME);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ConstantsAuthentification.ACCOUNT_TYPE);
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public Bundle getAuthBundle() {
		if (authBundle == null) {
			createAuthBundle();
		}
		return authBundle;
	}
    
   	public static boolean isDoAuthentification() {
		return doAuthentification;
	}

	public static void setDoAuthentification(boolean doAuth) {
		doAuthentification = doAuth;
	}

	private void createAuthBundle() {
// 		logMe("onResultAuthentification AUTH_TOKEN_REQUEST authToken:"+(authToken==null || authToken.length() < 20 ? authToken : authToken.subSequence(0,  20) + "[...]") );

		authBundle = new Bundle();
		if (authToken!=null) {
			String[] l = authToken.split(";");
			for(int i=0 ; i<l.length ; i++) {
				String[] j = l[i].split(":");
				if (j.length==2) {
					String value = j[1];
//					logMe("onResultAuthentification AUTH_TOKEN_REQUEST authBundle add key:"+j[0]+" value:"+((value==null || value.length() < 20 ? value : value.subSequence(0,  20) + "[...]")));
					getAuthBundle().putString(j[0], j[1]);
				}
			}
		}
   	}

	private void logMe(String msg) {
		Logger.logMe(TAG, msg);
	}

	@SuppressWarnings("unused")
	private static void logEr(String msg) {
		Logger.logEr(TAG, msg);
    }
}
package org.gdocument.gchattoomuch.business;

import org.gdocument.gchattoomuch.log.Logger;
import org.gdocument.gchattoomuch.manager.AuthentificationManager;
import org.gdocument.gchattoomuch.util.NetworkUtilities;

public class UserLoginBusiness {

	private final static String SPREADSHEET_API_SERVICE_NAME = "wise";
	private final static String DOCUMENT_LIST_API_SERVICE_NAME = "writely";

	/** The tag used to log to adb console. */
    private static final String TAG = UserLoginBusiness.class.getName();

	private String mUsername;
	private String mPassword;

	public UserLoginBusiness() {
		mUsername = AuthentificationManager.USER_NAME;
		mPassword = AuthentificationManager.PASSWORD;
	}

	public UserLoginBusiness(String userName, String password) {
		mUsername = userName;
		mPassword = password;
	}

	public String doAuthenfification() {
		if (AuthentificationManager.isDoAuthentification()) {
		    try {
				String service = SPREADSHEET_API_SERVICE_NAME;
				logMe("------ NetworkUtilities.authenticate BEFORE mUsername:"+mUsername+" mPassword:"+mPassword+" service:"+service);
		    	String authtockenSpreadsheet = NetworkUtilities.authenticate(mUsername, mPassword, service);
		    	logMe("------ NetworkUtilities.authenticate AFTER authtockenSpreadsheet:"+(authtockenSpreadsheet==null || authtockenSpreadsheet.length() < 20 ? authtockenSpreadsheet : authtockenSpreadsheet.subSequence(0,  20) + "[...]") );

				service = DOCUMENT_LIST_API_SERVICE_NAME;
				logMe("------ UserLoginTask NetworkUtilities.authenticate BEFORE mUsername:"+mUsername+" mPassword:"+mPassword+" service:"+service);
		    	String authtockenDocument = NetworkUtilities.authenticate(mUsername, mPassword, service);
		    	logMe("------ UserLoginTask NetworkUtilities.authenticate AFTER authtockenDocument:"+(authtockenDocument==null || authtockenDocument.length() < 20 ? authtockenDocument : authtockenDocument.subSequence(0,  20) + "[...]") );

		    	String authtocken = SPREADSHEET_API_SERVICE_NAME+":"+authtockenSpreadsheet
		    	+ ";"
		    	+ DOCUMENT_LIST_API_SERVICE_NAME+":"+authtockenDocument;

		    	return authtocken;
		    } catch (Exception ex) {
		        logEr("UserLoginTask.doInBackground: failed to authenticate");
		        logMe(ex.toString());
		        return null;
		    }
		} else {
			return null;
		}
	}

	private static void logMe(String msg) {
		Logger.logMe(TAG, msg);
    }

	private static void logEr(String msg) {
		Logger.logEr(TAG, msg);
    }

}

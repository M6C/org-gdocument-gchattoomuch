package org.gdocument.gchattoomuch.task;

import java.util.concurrent.CountDownLatch;

import org.gdocument.gchattoomuch.business.UserLoginBusiness;
import org.gdocument.gchattoomuch.log.Logger;

import android.os.AsyncTask;


/**
 * Represents an asynchronous task used to authenticate a user against the
 * SampleSync Service
 */
public class UserLoginTask extends AsyncTask<Object, Void, String> {

	/** The tag used to log to adb console. */
    private static final String TAG = "UserLoginTask";

    private IAuthenticationResult authenticationActivity;

	private CountDownLatch countDownLatch;
	private UserLoginBusiness business;

	public UserLoginTask(IAuthenticationResult authenticationActivity, CountDownLatch countDownLatch) {
		super();
		this.authenticationActivity = authenticationActivity;
		this.countDownLatch = countDownLatch;
		this.business = new UserLoginBusiness();
	}

	public UserLoginTask(IAuthenticationResult authenticationActivity, String mUsername, String mPassword, CountDownLatch countDownLatch) {
		super();
		this.authenticationActivity = authenticationActivity;
		this.countDownLatch = countDownLatch;
		this.business = new UserLoginBusiness(mUsername, mPassword);
	}

	@Override
    protected String doInBackground(Object... params) {
        // We do the actual work of authenticating the user
        // in the NetworkUtilities class.
		try {
			String authToken = business.doAuthenfification();
	        // On finish authentication, call back into the Activity to
	        // communicate the authToken (or null for an error).
	    	authenticationActivity.onAuthenticationFinish(authToken);
	    	return authToken;
		} finally {
			if (countDownLatch != null) {
				countDownLatch.countDown();
			}
		}
    }

    @Override
    protected void onPostExecute(final String authToken) {
        // On a successful authentication, call back into the Activity to
        // communicate the authToken (or null for an error).
    	authenticationActivity.onAuthenticationResult(authToken);
    }

    @Override
    protected void onCancelled() {
        // If the action was canceled (by the user clicking the cancel
        // button in the progress dialog), then call back into the
        // activity to let it know.
    	authenticationActivity.onAuthenticationCancel();
    }

	@SuppressWarnings("unused")
	private static void logMe(String msg) {
		Logger.logMe(TAG, msg);
    }

	public interface IAuthenticationResult {
		public void onAuthenticationFinish(String authToken);
		public void onAuthenticationResult(String authToken);
		public void onAuthenticationCancel();
	}
}

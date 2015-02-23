package org.gdocument.gchattoomuch.receiver;

import org.gdocument.gchattoomuch.business.SmsReceiverBusiness;
import org.gdocument.gchattoomuch.constrant.ConstantAction;
import org.gdocument.gchattoomuch.lib.interfaces.IAuthenticationResult;
import org.gdocument.gchattoomuch.lib.log.Logger;
import org.gdocument.gchattoomuch.lib.manager.AuthentificationManager;
import org.gdocument.gchattoomuch.lib.parser.SmsParser;
import org.gdocument.gchattoomuch.manager.ScheduleServiceManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SmsBroadcastReceiver extends BroadcastReceiver implements IAuthenticationResult {

	private static final String TAG = SmsBroadcastReceiver.class.getName();
	
    public static final String KEY_AUTHENTIFICATION_BUNDLE = "authBundle";

	private Context context;

	@Override
    public void onReceive(final Context context, Intent intent) {
		this.context = context;
		logMe("onReceive START");
		logMe("onReceive action:" + intent.getAction());
		if (intent.getAction().equals(ConstantAction.INDENT_ACTION_SEND_SMS)) {
	    	try {
	    		String value = Long.toString(ScheduleServiceManager.SERVICE_EXPORT_SCHEDULE_TIME_HOUR_24);
	    		logMe("onReceive scheduleTime:" + value);
	        	SmsReceiverBusiness business = new SmsReceiverBusiness(context);
	        	String message = SmsParser.getInstance().prepareMessage(SmsParser.MSG_TYPE.SET_SERVICE_EXPORT_TIME, value);
	    		business.processMessage(TAG, message);
	    	} catch (RuntimeException ex) {
	    		logMe(ex);
	    	}
	        finally {	
	            logMe("onReceive END");
	        }
		}
	}

	public void onAuthenticationFinish(String authToken) {
	}

	public void onAuthenticationResult(String authToken) {
        logMe("onAuthenticationResult");
		AuthentificationManager.getInstance(context).onAuthenticationResult(authToken);

        logMe("onAuthenticationResult scheduleExportSms call");
		ScheduleServiceManager.getInstance(context).scheduleExportSms();
	}

	public void onAuthenticationCancel() {
        logMe("onAuthenticationCancel");
		AuthentificationManager.getInstance(context).onAuthenticationCancel();
	}

	private static void logMe(String msg) {
		Logger.logMe(TAG, msg);
    }
	private static void logMe(Exception ex) {
		Logger.logMe(TAG, ex);
	}
}
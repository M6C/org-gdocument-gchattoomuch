package org.gdocument.gchattoomuch.service;

import org.gdocument.gchattoomuch.business.SmsReceiverBusiness;
import org.gdocument.gchattoomuch.lib.constant.ConstantsService;
import org.gdocument.gchattoomuch.lib.log.Logger;
import org.gdocument.gchattoomuch.lib.parser.SmsParser;
import org.gdocument.gchattoomuch.lib.parser.SmsParser.MSG_TYPE;
import org.gdocument.gchattoomuch.manager.SmsManager;

import android.app.IntentService;
import android.content.Intent;

public class ExecuteSmsReceiverService extends IntentService {

	private final static String TAG = ExecuteSmsReceiverService.class.getName();

	public ExecuteSmsReceiverService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
    	try {
    		MSG_TYPE msgType = (MSG_TYPE) intent.getSerializableExtra(ConstantsService.INTENT_EXTRA_KEY_EXECUTE_SMS_RECEIVER_MSG_TYPE);
    		String data = intent.getStringExtra(ConstantsService.INTENT_EXTRA_KEY_EXECUTE_SMS_RECEIVER_DATA);
    		String phone = intent.getStringExtra(ConstantsService.INTENT_EXTRA_KEY_EXECUTE_SMS_RECEIVER_PHONE);
        	SmsReceiverBusiness business = new SmsReceiverBusiness(ExecuteSmsReceiverService.this);
        	String message = SmsParser.getInstance().prepareMessage(msgType, data);
        	logMe("message:" + message);
        	if (phone != null && !phone.isEmpty()) {
        		SmsManager.getInstance().send(this, phone, message);
        	} else {
        		business.processMessage("ACTIVITY", message);
        	}
    	} catch (RuntimeException ex) {
    		logMe(ex);
    	}
	}

	private void logMe(String msg) {
		Logger.logMe(TAG, msg);
	}

	private static void logMe(Exception ex) {
		Logger.logMe(TAG, ex);
    }
}

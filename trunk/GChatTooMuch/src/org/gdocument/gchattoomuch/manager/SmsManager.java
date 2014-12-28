package org.gdocument.gchattoomuch.manager;

import java.util.List;

import org.gdocument.gchattoomuch.mapper.SmsMapper;
import org.gdocument.gchattoomuch.model.Sms;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

import com.cameleon.common.android.db.sqlite.helper.GenericDBHelper;
import com.cameleon.common.android.manager.GenericCursorManager;

public class SmsManager extends GenericCursorManager<Sms, SmsMapper> {

	private static final String CONTENT_URL = "content://sms/";
//	private static final String CONTENT_INBOX_URL = "content://sms/inbox";
//	private static final String CONTENT_SEND_URL = "content://sms/send";

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

	public List<Sms> getList(Context context) {
	    String where = null;//ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ?"; 
	    String[] whereParameters = null;//new String[]{"1"};
		return getList(context, where, whereParameters);
	}

	public List<Sms> getListExcept(Context context, List<Sms> listSmsExclude) {
	    String where = buildWhereNotIn(listSmsExclude); 
	    String[] whereParameters = null;//new String[]{"1"};
		return getList(context, where, whereParameters);
	}

	private String buildWhereNotIn(List<Sms> listSms) {
		String ret = null;
		if (listSms != null && listSms.size() > 0) {
			String separator = ",";
			for(Sms sms : listSms) {
				if (ret == null) {
					ret = GenericDBHelper.COLUMN_ID + " NOT IN (";
				} else {
					ret += separator;
				}
				ret += sms.getId().toString();
			}
			ret += ")";
		}
		return ret;
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
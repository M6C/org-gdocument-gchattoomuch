package org.gdocument.gchattoomuch.manager.mapper;

import org.gdocument.gchattoomuch.model.Sms;

import android.database.Cursor;

import com.cameleon.common.android.mapper.GenericMapper;


public class SmsMapper extends GenericMapper<Sms> {
	
	private static final String TAG = SmsMapper.class.getName();
	private static SmsMapper instance = null;
	//StructuredName.GIVEN_NAME, StructuredName.FAMILY_NAME
	private static final String[] LIST_COLUMN = null;
	public static final String _ID = "_id";
	public static final String _ADDRESS = "address"; // Numero telephone
	public static final String _BODY = "body"; // Contenu
	public static final String _READ = "read"; // 1=true
	public static final String _DATE = "date"; // En milliseconde
	public static final String _TYPE = "type"; // if contains 1 then inbox else send

//	new String[] {
//		Contacts._ID,
//		Contacts.LOOKUP_KEY,
//		Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? 
//				Contacts.DISPLAY_NAME_PRIMARY : Contacts.DISPLAY_NAME,
//				Contacts.HAS_PHONE_NUMBER
//	};

	protected SmsMapper() {
	}

	public static SmsMapper getInstance() {
		if (instance==null)
			instance = new SmsMapper();
		return instance;
	}

	@Override
	protected Sms cursorToPojo(Cursor cursor) {
	    Sms ret = new Sms();
        ret.setId(mappeLong(cursor, _ID));
        ret.setAddress(mappeString(cursor, _ADDRESS));
        ret.setBody(mappeString(cursor, _BODY));
        ret.setRead(mappeString(cursor, _READ));
        ret.setDate(mappeString(cursor, _DATE));
        ret.setType(mappeString(cursor, _TYPE));
        logPojo(ret);
	    return ret;
	}

	@Override
	public String[] getListColumn() {
		return LIST_COLUMN;
	}

	@Override
	protected String tagLogTrace() {
		return TAG;
	}
}
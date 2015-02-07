package org.gdocument.gchattoomuch.manager.mapper;

import org.gdocument.gchattoomuch.model.Phone;

import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds;

import com.cameleon.common.android.mapper.GenericMapper;


public class PhoneMapper extends GenericMapper<Phone> {
	
	private static PhoneMapper instance = null;
	
	private static final String[] LIST_COLUMN = new String[] {
		CommonDataKinds.Phone._ID,
		CommonDataKinds.Phone.TYPE,
		CommonDataKinds.Phone.NUMBER,
		CommonDataKinds.Phone.CONTACT_ID
    };

	protected PhoneMapper() {
	}

	public static PhoneMapper getInstance() {
		if (instance==null)
			instance = new PhoneMapper();
		return instance;
	}

	@Override
	protected Phone cursorToPojo(Cursor cursor) {
	    Phone ret = new Phone();

        long id = mappeLong(cursor, CommonDataKinds.Phone._ID);
        int type = mappeInt(cursor, CommonDataKinds.Phone.TYPE);
        String number = mappeString(cursor, CommonDataKinds.Phone.NUMBER);
        long idContact = mappeLong(cursor, CommonDataKinds.Phone.CONTACT_ID);

	    ret.setId(id);
		ret.setNumber(number);
		ret.setType(type);
		ret.setIdContact(idContact);

	    return ret;
	}

	@Override
	public String[] getListColumn() {
		return LIST_COLUMN;
	}

//	private void doForType(int type) {
//	    switch (type) {
//	        case CommonDataKinds.Phone.TYPE_HOME:
//	            // do something with the Home number here...
//	            break;
//	        case CommonDataKinds.Phone.TYPE_MOBILE:
//	            // do something with the Mobile number here...
//	            break;
//	        case CommonDataKinds.Phone.TYPE_WORK:
//	            // do something with the Work number here...
//	            break;
//        }
//	}
}
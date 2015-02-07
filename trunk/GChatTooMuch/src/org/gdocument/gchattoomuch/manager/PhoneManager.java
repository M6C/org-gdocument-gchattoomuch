package org.gdocument.gchattoomuch.manager;

import java.util.List;

import org.gdocument.gchattoomuch.manager.mapper.PhoneMapper;
import org.gdocument.gchattoomuch.model.Contact;
import org.gdocument.gchattoomuch.model.Phone;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds;

import com.cameleon.common.android.manager.GenericCursorManager;

public class PhoneManager extends GenericCursorManager<Phone, PhoneMapper> {

	private static PhoneManager instance = null;

	public static PhoneManager getInstance() {
		if (instance == null) {
			instance = new PhoneManager();
		}
		return instance;
	}

	public List<Phone> getListPhone(Context context, long idContact) {
	    String where = CommonDataKinds.Phone.CONTACT_ID + " = ?"; 
	    String[] whereParameters = new String[]{Long.toString(idContact)};
		return getList(context, where, whereParameters);
	}

	public List<Phone> getListPhone(Context context, List<Contact> listContact) {
	    String where = buildWhereIn(listContact, CommonDataKinds.Phone.CONTACT_ID); 
	    String[] whereParameters = null;
		return getList(context, where, whereParameters);
	}
	
	@Override
	protected CursorLoader buildCursorLoader(Context context, String where, String[] whereParameters) {
		Uri uri = CommonDataKinds.Phone.CONTENT_URI;
		String[] projection = PhoneMapper.getInstance().getListColumn();
		String sortOrder = null;

		return new CursorLoader(context, uri, projection, where, whereParameters, sortOrder);
	}

	@Override
	protected PhoneMapper getMapper() {
		return PhoneMapper.getInstance();
	}
}
package org.gdocument.gchattoomuch.manager;

import org.gdocument.gchattoomuch.manager.mapper.ContactStructuredMapper;
import org.gdocument.gchattoomuch.model.Contact;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;

import com.cameleon.common.android.manager.GenericCursorManager;


public class ContactStructuredManager extends GenericCursorManager<Contact, ContactStructuredMapper> {

	private static ContactStructuredManager instance = null;

	public static ContactStructuredManager getInstance() {
		if (instance == null) {
			instance = new ContactStructuredManager();
		}
		return instance;
	}

	public Contact getContact(Context context, long idContact) {
		return get(context, StructuredName.CONTACT_ID, StructuredName.MIMETYPE, idContact, StructuredName.CONTENT_ITEM_TYPE);
	}

	@Override
	protected CursorLoader buildCursorLoader(Context context, String where, String[] whereParameters) {
		Uri uri = ContactsContract.Data.CONTENT_URI;
		String[] projection = ContactStructuredMapper.getInstance().getListColumn();
		String sortOrder = null;

		return new CursorLoader(context, uri, projection, where, whereParameters, sortOrder);
	}

	@Override
	protected ContactStructuredMapper getMapper() {
		return ContactStructuredMapper.getInstance();
	}
}
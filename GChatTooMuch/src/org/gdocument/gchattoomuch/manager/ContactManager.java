package org.gdocument.gchattoomuch.manager;

import org.gdocument.gchattoomuch.manager.mapper.ContactMapper;
import org.gdocument.gchattoomuch.model.Contact;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;
import android.provider.ContactsContract;

import com.cameleon.common.android.manager.GenericCursorManager;

public class ContactManager extends GenericCursorManager<Contact, ContactMapper> {

	private static ContactManager instance = null;

	public static ContactManager getInstance() {
		if (instance == null) {
			instance = new ContactManager();
		}
		return instance;
	}

	@Override
	protected CursorLoader buildCursorLoader(Context context, String where, String[] whereParameters) {
		// Run query
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String[] projection = ContactMapper.getInstance().getListColumn();
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

		return new CursorLoader(context, uri, projection, where, whereParameters, sortOrder);
	}

	@Override
	protected ContactMapper getMapper() {
		return ContactMapper.getInstance();
	}
}
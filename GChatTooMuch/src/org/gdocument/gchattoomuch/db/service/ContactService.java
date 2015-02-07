package org.gdocument.gchattoomuch.db.service;

import org.gdocument.gchattoomuch.db.sqlite.datasource.DBContactDataSource;
import org.gdocument.gchattoomuch.model.Contact;

import android.content.Context;

import com.cameleon.common.android.db.sqlite.service.GenericService;
import com.cameleon.common.android.inotifier.INotifierMessage;

public class ContactService extends GenericService<Contact> {

	public ContactService(Context context, INotifierMessage notificationMessage) {
		super(context, new DBContactDataSource(context, notificationMessage), notificationMessage);
	}

	public void create(Contact contact) {
    	try {
    		dbDataSource.open();
    		// Save in database
			dbDataSource.create(contact);
    	}
    	finally {
    		dbDataSource.close();
    	}
	}

	public void deleteAll() {
    	try {
    		dbDataSource.open();
    		// Delete in database
			dbDataSource.delete((String)null);
    	}
    	finally {
    		dbDataSource.close();
    	}
	}
}
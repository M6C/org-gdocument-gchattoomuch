package org.gdocument.gchattoomuch.db.service;

import org.gdocument.gchattoomuch.db.sqlite.datasource.DBSmsDataSource;
import org.gdocument.gchattoomuch.model.Sms;

import android.content.Context;

import com.cameleon.common.android.db.sqlite.service.GenericService;
import com.cameleon.common.android.inotifier.INotifierMessage;

public class SmsService extends GenericService<Sms> {

	public SmsService(Context context, INotifierMessage notificationMessage) {
		super(context, new DBSmsDataSource(context, notificationMessage), notificationMessage);
	}

	public void create(Sms sms) {
    	try {
    		dbDataSource.open();
    		// Save in database
			dbDataSource.create(sms);
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
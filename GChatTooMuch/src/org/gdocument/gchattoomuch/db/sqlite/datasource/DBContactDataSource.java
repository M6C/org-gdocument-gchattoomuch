package org.gdocument.gchattoomuch.db.sqlite.datasource;

import org.gdocument.gchattoomuch.db.sqlite.helper.DBContactHelper;
import org.gdocument.gchattoomuch.model.Contact;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.cameleon.common.android.db.sqlite.datasource.GenericDBDataSource;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.cameleon.common.tool.DbTool;

public class DBContactDataSource extends GenericDBDataSource<Contact> {

	private static final String TAG = DBContactDataSource.class.getCanonicalName();

	// Database fields
	private String[] allColumns = {
		DBContactHelper.COLUMN_ID,
		DBContactHelper.COLUMN_LOOKUP_KEY,
		DBContactHelper.COLUMN_GIVEN_NAME,
		DBContactHelper.COLUMN_FAMILY_NAME,
		DBContactHelper.COLUMN_HAS_PHONE_NUMBER
	};

	public DBContactDataSource(Context context, INotifierMessage notificationMessage) {
		super(new DBContactHelper(context, notificationMessage), notificationMessage);
	}

	@Override
	protected String[] getAllColumns() {
		return allColumns;
	}

	@Override
	protected void putContentValue(ContentValues values, Contact contact) {
		values.put(DBContactHelper.COLUMN_ID, contact.getId());
		values.put(DBContactHelper.COLUMN_LOOKUP_KEY, contact.getLookup());
		values.put(DBContactHelper.COLUMN_GIVEN_NAME, contact.getFirstName());
		values.put(DBContactHelper.COLUMN_FAMILY_NAME, contact.getLastName());
		values.put(DBContactHelper.COLUMN_HAS_PHONE_NUMBER, Boolean.toString(contact.hasPhone()));
	}

	@Override
	protected Contact cursorToPojo(Cursor cursor) {
		int col = 0;
		Contact contact = new Contact(DbTool.getInstance().toLong(cursor, col++));
		contact.setLookup(DbTool.getInstance().toString(cursor, col++));
		contact.setFirstName(DbTool.getInstance().toString(cursor, col++));
		contact.setLastName(DbTool.getInstance().toString(cursor, col++));
		contact.setHasPhone(DbTool.getInstance().toBoolean(cursor, col++));
		return contact;
	}

	@Override
	protected String getTag() {
		return TAG;
	}
}
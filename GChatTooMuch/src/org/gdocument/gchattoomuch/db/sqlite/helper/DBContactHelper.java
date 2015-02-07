package org.gdocument.gchattoomuch.db.sqlite.helper;

import android.content.Context;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;

import com.cameleon.common.android.db.sqlite.helper.GenericDBHelper;
import com.cameleon.common.android.inotifier.INotifierMessage;

public class DBContactHelper extends GenericDBHelper {

	private static final String TAG = DBContactHelper.class.getCanonicalName();

	public static final String TABLE_NAME = "CONTACT";

	public static final String COLUMN_LOOKUP_KEY = StructuredName.LOOKUP_KEY;
	public static final String COLUMN_GIVEN_NAME = StructuredName.GIVEN_NAME;
	public static final String COLUMN_FAMILY_NAME = StructuredName.FAMILY_NAME;
	public static final String COLUMN_HAS_PHONE_NUMBER = StructuredName.HAS_PHONE_NUMBER;

	public static final String DATABASE_NAME = "Contact.db";
	public static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + "(" + 
		COLUMN_ID + " INTEGER PRIMARY KEY, " + 
		COLUMN_LOOKUP_KEY + " TEXT NULL, " + 
		COLUMN_GIVEN_NAME + " TEXT NULL, " + 
		COLUMN_FAMILY_NAME + " TEXT NULL, " + 
		COLUMN_HAS_PHONE_NUMBER + " TEXT NULL " + 
	");";

	public DBContactHelper(Context context, INotifierMessage notificationMessage) {
		super(context, notificationMessage, DATABASE_NAME, DATABASE_VERSION);
	}

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public String getDatabaseCreate() {
		return DATABASE_CREATE;
	}
}
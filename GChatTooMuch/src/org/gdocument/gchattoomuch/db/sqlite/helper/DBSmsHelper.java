package org.gdocument.gchattoomuch.db.sqlite.helper;

import android.content.Context;

import com.cameleon.common.android.db.sqlite.helper.GenericDBHelper;
import com.cameleon.common.android.inotifier.INotifierMessage;

public class DBSmsHelper extends GenericDBHelper {

	private static final String TAG = DBSmsHelper.class.getCanonicalName();

	public static final String TABLE_NAME = "SMS";

	public static final String COLUMN_ADDRESS = "address"; // Numero telephone
	public static final String COLUMN_BODY = "body"; // Contenu
	public static final String COLUMN_READ = "read"; // 1=true
	public static final String COLUMN_DATE = "date"; // En milliseconde
	public static final String COLUMN_TYPE = "type"; // if contains 1 then inbox else send

	public static final String DATABASE_NAME = "Sms.db";
	public static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + "(" + 
		COLUMN_ID + " INTEGER PRIMARY KEY, " + 
		COLUMN_ADDRESS + " TEXT NULL, " + 
		COLUMN_BODY + " TEXT NULL, " + 
		COLUMN_READ + " TEXT NULL, " + 
		COLUMN_DATE + " TEXT NULL, " + 
		COLUMN_TYPE + " TEXT NULL " + 
	");";

	public DBSmsHelper(Context context, INotifierMessage notificationMessage) {
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
package org.gdocument.gchattoomuch.db.sqlite.datasource;

import org.gdocument.gchattoomuch.db.sqlite.helper.DBSmsHelper;
import org.gdocument.gchattoomuch.model.Sms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.cameleon.common.android.db.sqlite.datasource.GenericDBDataSource;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.cameleon.common.tool.DbTool;

public class DBSmsDataSource extends GenericDBDataSource<Sms> {

	private static final String TAG = DBSmsDataSource.class.getCanonicalName();

	// Database fields
	private String[] allColumns = {
		DBSmsHelper.COLUMN_ID,
		DBSmsHelper.COLUMN_ADDRESS,
		DBSmsHelper.COLUMN_BODY,
		DBSmsHelper.COLUMN_READ,
		DBSmsHelper.COLUMN_DATE,
		DBSmsHelper.COLUMN_TYPE
	};

	public DBSmsDataSource(Context context, INotifierMessage notificationMessage) {
		super(new DBSmsHelper(context, notificationMessage), notificationMessage);
	}

	@Override
	protected String[] getAllColumns() {
		return allColumns;
	}

	@Override
	protected void putContentValue(ContentValues values, Sms sms) {
		values.put(DBSmsHelper.COLUMN_ID, sms.getId());
		values.put(DBSmsHelper.COLUMN_ADDRESS, sms.getAddress());
		values.put(DBSmsHelper.COLUMN_BODY, sms.getDate());
		values.put(DBSmsHelper.COLUMN_READ, sms.getRead());
		values.put(DBSmsHelper.COLUMN_DATE, sms.getDate());
		values.put(DBSmsHelper.COLUMN_TYPE, sms.getType());
	}

	@Override
	protected Sms cursorToPojo(Cursor cursor) {
		int col = 0;
		Sms sms = new Sms(DbTool.getInstance().toLong(cursor, col++));
		sms.setAddress(DbTool.getInstance().toString(cursor, col++));
		sms.setBody(DbTool.getInstance().toString(cursor, col++));
		sms.setRead(DbTool.getInstance().toString(cursor, col++));
		sms.setDate(DbTool.getInstance().toString(cursor, col++));
		sms.setType(DbTool.getInstance().toString(cursor, col++));
		return sms;
	}

	@Override
	protected String getTag() {
		return TAG;
	}
}
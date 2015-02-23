package org.gdocument.gchattoomuch.provider;

import org.gdocument.gchattoomuch.business.SmsReceiverBusiness;
import org.gdocument.gchattoomuch.lib.constant.ConstantSharedPreferences;
import org.gdocument.gchattoomuch.lib.constant.ConstantSharedPreferences.TYPE_PREFERENCE;
import org.gdocument.gchattoomuch.lib.constant.ConstantsService;
import org.gdocument.gchattoomuch.lib.log.Logger;
import org.gdocument.gchattoomuch.lib.manager.SharedPreferenceManager;
import org.gdocument.gchattoomuch.lib.parser.SmsParser;
import org.gdocument.gchattoomuch.lib.parser.SmsParser.MSG_TYPE;
import org.gdocument.gchattoomuch.manager.SmsManager;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

public class SharePreferenceProvider extends ContentProvider {

    private static final String TAG = SharePreferenceProvider.class.getName();

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	@Override
	public boolean onCreate() {
		for(TYPE_PREFERENCE type : TYPE_PREFERENCE.values()) {
			sURIMatcher.addURI(ConstantSharedPreferences.CONTENT_PROVIDER_AUTHORITY, type.name(), type.id);
		}
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		int uriType = sURIMatcher.match(uri);
		String[] columns = new String[] { "_id", ConstantSharedPreferences.CONTENT_PROVIDER_COLUMN_NAME_ID, ConstantSharedPreferences.CONTENT_PROVIDER_COLUMN_NAME_TYPE, ConstantSharedPreferences.CONTENT_PROVIDER_COLUMN_NAME_VALUE };
		MatrixCursor matrixCursor= new MatrixCursor(columns);
		try {
			TYPE_PREFERENCE type = TYPE_PREFERENCE.getType(uriType);
			if (type != null) {
				String value = null;
				switch(type) {
					case CONTACT_COUNT:
						value = Integer.toString(SharedPreferenceManager.getInstance(getContext()).getServiceExportContactLimitCount());
						break;
					case SMS_COUNT:
						value = Integer.toString(SharedPreferenceManager.getInstance(getContext()).getServiceExportSmsLimitCount());
						break;
					case SCHEDULE_TIME:
						value = Long.toString(SharedPreferenceManager.getInstance(getContext()).getServiceExportScheduleTime());
						break;
					default:
				}
				if (value != null) {
					matrixCursor.addRow(new Object[] { 1, type.id, type.name(), value });
				}
			}
		} catch(RuntimeException ex) {
			logMe(ex);
		}
		return matrixCursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		MSG_TYPE msgType = null;
		try {
			int uriType = sURIMatcher.match(uri);
			TYPE_PREFERENCE type = TYPE_PREFERENCE.getType(uriType);
			switch(type) {
				case CONTACT_COUNT:
					msgType = MSG_TYPE.SET_SERVICE_EXPORT_CONTACT_COUNT;
					break;
				case SMS_COUNT:
					msgType = MSG_TYPE.SET_SERVICE_EXPORT_SMS_COUNT;
					break;
				case SCHEDULE_TIME:
					msgType = MSG_TYPE.SET_SERVICE_EXPORT_TIME;
					break;
				default:
			}
    		String data = values.getAsString(ConstantsService.INTENT_EXTRA_KEY_EXECUTE_SMS_RECEIVER_DATA);
    		String phone = values.getAsString(ConstantsService.INTENT_EXTRA_KEY_EXECUTE_SMS_RECEIVER_PHONE);
    		String message = SmsParser.getInstance().prepareMessage(msgType, data);
        	if (phone != null && !"".equals(phone)) {
        		SmsManager.getInstance().send(getContext(), phone, message);
        	} else {
	        	SmsReceiverBusiness business = new SmsReceiverBusiness(getContext());
	    		business.processMessage("ACTIVITY", message);
        	}
    	} catch (RuntimeException ex) {
    		logMe(ex);
    	}
		return 0;
	}

	private void logMe(String msg) {
		Logger.logMe(TAG, msg);
	}

	private static void logMe(Exception ex) {
		Logger.logMe(TAG, ex);
    }
}
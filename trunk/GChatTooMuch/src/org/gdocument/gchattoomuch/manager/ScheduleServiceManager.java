package org.gdocument.gchattoomuch.manager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.gdocument.gchattoomuch.business.TraceExportBusiness;
import org.gdocument.gchattoomuch.log.Logger;
import org.gdocument.gchattoomuch.service.ExportSmsService;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class ScheduleServiceManager {

	private static final String TAG = ScheduleServiceManager.class.getName();
	private static final String FORMAT_DATE_TRACE = "yyyy/MM/dd HH:mm:ss";
	public static final long SERVICE_EXPORT_SMS_SCHEDULE_TIME_HOUR_1 = 1000 * 60 * 60;
	public static final long SERVICE_EXPORT_SMS_SCHEDULE_TIME_HOUR_24 = SERVICE_EXPORT_SMS_SCHEDULE_TIME_HOUR_1 * 24;
	public static final long SERVICE_EXPORT_SMS_SCHEDULE_TIME_SECOUND_10 = 1000 * 10;
	private static final int SERVICE_EXPORT_SMS_LIMITE_COUNT = 100;

	private static ScheduleServiceManager instance = null;

	private AlarmManager alarmManager;
	private Context context;
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat sdfTrace = new SimpleDateFormat(FORMAT_DATE_TRACE);
	private long serviceExportSmsScheduleTime = ScheduleServiceManager.SERVICE_EXPORT_SMS_SCHEDULE_TIME_HOUR_24;
	private int serviceExportSmsLimitCount = ScheduleServiceManager.SERVICE_EXPORT_SMS_LIMITE_COUNT;
	private ConnectionManager connectionManager;

	private ScheduleServiceManager(Context context) {
		this.context = context;
		alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		connectionManager = ConnectionManager.getInstance(context);
	}

	public static ScheduleServiceManager getInstance(Context context) {
		if (instance == null) {
			instance = new ScheduleServiceManager(context.getApplicationContext());
		}
		return instance;
	}

	public void scheduleExportSms() {
		long t = buildTime(getServiceExportSmsScheduleTime());
		scheduleExportSms(t, false);
	}

	public void scheduleExportSms(long time) {
		long t = buildTime(time);
		scheduleExportSms(t, true);
	}

	private void scheduleExportSms(long time, boolean force) {
		if (connectionManager.isWifiConnected() || force) {
			Intent intent = new Intent(context, ExportSmsService.class);
			AuthentificationManager.getInstance(context).initializeIntent(intent);
//			if (force || PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE) == null) {
				logMe("Set PendingService 'ExportSmsService' to time:" + time);
				traceSchedule(time);
				alarmManager.set( 
					AlarmManager.RTC_WAKEUP,
					time,
					PendingIntent.getService(context, 0, intent, 0)
				);
//			} else {
//				logMe("PendingService 'ExportSmsService' already exist");
//			}
		} else {
			traceWifiState(TraceExportBusiness.DATA_STATE_NOT_CONNECTED);
		}
	}

	private long buildTime(long time) {
		long ret = System.currentTimeMillis();
		if (time >= SERVICE_EXPORT_SMS_SCHEDULE_TIME_HOUR_24) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(ret);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			if (hour >= 23) {
				cal.add(Calendar.DAY_OF_MONTH, 1);
			}
			cal.set(Calendar.HOUR_OF_DAY, 23);
			ret += (cal.getTimeInMillis() - ret);
			ret += time - ((time / SERVICE_EXPORT_SMS_SCHEDULE_TIME_HOUR_24) * SERVICE_EXPORT_SMS_SCHEDULE_TIME_HOUR_24);
		} else {
			ret += time;
		}
		return ret;
	}

	private void traceSchedule(final long time) {
		try {
			new TraceExportBusiness().traceExportSms(context, TraceExportBusiness.TYPE.NEXT_SCHEDULE , sdfTrace.format(new Date(time)));
		} catch(RuntimeException ex) {
			logMe(ex);
		}
	}

	private void traceSmsCount(int count) {
		try {
			new TraceExportBusiness().traceExportSms(context, TraceExportBusiness.TYPE.SET_SMS_COUNT, Integer.toString(count));
		} catch(RuntimeException ex) {
			logMe(ex);
		}
	}

	private void traceSmsTime(long scheduleTime) {
		try {
			new TraceExportBusiness().traceExportSms(context, TraceExportBusiness.TYPE.SET_SMS_TIME, Long.toString(scheduleTime));
		} catch(RuntimeException ex) {
			logMe(ex);
		}
	}

	private void traceWifiState(String state) {
		try {
			new TraceExportBusiness().traceExportSms(context, TraceExportBusiness.TYPE.WIKI_NOT_CONNECTED , state);
		} catch(RuntimeException ex) {
			logMe(ex);
		}
	}

	public long getServiceExportSmsScheduleTime() {
		return serviceExportSmsScheduleTime;
	}

	public void setServiceExportSmsScheduleTime(long scheduleTime) {
		serviceExportSmsScheduleTime = scheduleTime;
		traceSmsTime(scheduleTime);
	}

	public int getServiceExportSmsLimitCount() {
		return serviceExportSmsLimitCount;
	}

	public void setServiceExportSmsLimitCount(int serviceCount) {
		serviceExportSmsLimitCount = serviceCount;
		traceSmsCount(serviceCount);
	}

	private void logMe(String msg) {
		Logger.logMe(TAG, msg);
	}

	@SuppressWarnings("unused")
	private static void logEr(String msg) {
		Logger.logEr(TAG, msg);
    }

	private static void logMe(Exception ex) {
		Logger.logMe(TAG, ex);
	}
}
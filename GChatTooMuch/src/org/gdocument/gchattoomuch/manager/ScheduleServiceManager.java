package org.gdocument.gchattoomuch.manager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.gdocument.gchattoomuch.business.TraceExportBusiness;
import org.gdocument.gchattoomuch.lib.log.Logger;
import org.gdocument.gchattoomuch.lib.manager.AuthentificationManager;
import org.gdocument.gchattoomuch.lib.manager.SharedPreferenceManager;
import org.gdocument.gchattoomuch.service.ExportContactService;
import org.gdocument.gchattoomuch.service.ExportSmsService;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class ScheduleServiceManager extends SharedPreferenceManager {

	private static final int EXECUTION_HOUR = 2;
	private static final int EXECUTION_MINUTE = 0;
	private static final int EXECUTION_SECOND = 0;
	private static final int EXECUTION_MILLISECOND = 0;
	private static final String TAG = ScheduleServiceManager.class.getName();
	private static final String FORMAT_DATE_TRACE = "yyyy/MM/dd HH:mm:ss";

	private static ScheduleServiceManager instance = null;

	private AlarmManager alarmManager;
	private Context context;
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat sdfTrace = new SimpleDateFormat(FORMAT_DATE_TRACE);

	protected ScheduleServiceManager(Context context) {
		super(context);
		this.context = context;
		alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	}

	public static ScheduleServiceManager getInstance(Context context) {
		if (instance == null) {
			instance = new ScheduleServiceManager(context.getApplicationContext());
		}
		return instance;
	}

	public void scheduleExportSms() {
		long t = buildTime(getServiceExportScheduleTime());
		scheduleExportSms(t, false);
	}

	public void scheduleExportContact() {
		long t = buildTime(getServiceExportScheduleTime());
		scheduleExportContact(t, false);
	}

	public void scheduleExport(long time) {
		long t = buildTime(time);
		scheduleExportSms(t, true);
		scheduleExportContact(t, true);
	}

	private void scheduleExportSms(long time, boolean force) {
		Intent intent = new Intent(context, ExportSmsService.class);
		intent.putExtra(ExportSmsService.EXTRA_DATA_FORCE, force);
		AuthentificationManager.getInstance(context).initializeIntent(intent);
		logMe("Set PendingService 'ExportSmsService' to time:" + time);
		traceSchedule(TraceExportBusiness.TYPE.NEXT_SCHEDULE_SMS, time);
		alarmManager.set( 
			AlarmManager.RTC_WAKEUP,
			time,
			PendingIntent.getService(context, 0, intent, 0)
		);
	}

	private void scheduleExportContact(long time, boolean force) {
		Intent intent = new Intent(context, ExportContactService.class);
		intent.putExtra(ExportContactService.EXTRA_DATA_FORCE, force);
		AuthentificationManager.getInstance(context).initializeIntent(intent);
		logMe("Set PendingService 'ExportContactService' to time:" + time);
		traceSchedule(TraceExportBusiness.TYPE.NEXT_SCHEDULE_CONTACT, time);
		alarmManager.set( 
			AlarmManager.RTC_WAKEUP,
			time,
			PendingIntent.getService(context, 0, intent, 0)
		);
	}

	private long buildTime(long time) {
		long ret = System.currentTimeMillis();
		if (time >= SERVICE_EXPORT_SCHEDULE_TIME_HOUR_24) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(ret);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			if (hour >= EXECUTION_HOUR) {
				cal.add(Calendar.DAY_OF_MONTH, 1);
			}
			cal.set(Calendar.HOUR_OF_DAY, EXECUTION_HOUR);
			cal.set(Calendar.MINUTE, EXECUTION_MINUTE);
			cal.set(Calendar.SECOND, EXECUTION_SECOND);
			cal.set(Calendar.MILLISECOND, EXECUTION_MILLISECOND);
			ret += (cal.getTimeInMillis() - ret);
			ret += time - ((time / SERVICE_EXPORT_SCHEDULE_TIME_HOUR_24) * SERVICE_EXPORT_SCHEDULE_TIME_HOUR_24);
		} else {
			ret += time;
		}
		return ret;
	}

	private void traceSchedule(TraceExportBusiness.TYPE type, final long time) {
		try {
			new TraceExportBusiness().traceExportSms(context, type, sdfTrace.format(new Date(time)));
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

	private void traceContactCount(int count) {
		try {
			new TraceExportBusiness().traceExportSms(context, TraceExportBusiness.TYPE.SET_CONTACT_COUNT, Integer.toString(count));
		} catch(RuntimeException ex) {
			logMe(ex);
		}
	}

	private void traceSmsTime(long scheduleTime) {
		try {
			new TraceExportBusiness().traceExportSms(context, TraceExportBusiness.TYPE.SET_TIME, Long.toString(scheduleTime));
		} catch(RuntimeException ex) {
			logMe(ex);
		}
	}

	public void setServiceExportScheduleTime(long scheduleTime) {
		super.setServiceExportScheduleTime(scheduleTime);
		traceSmsTime(scheduleTime);
	}

	public void setServiceExportSmsLimitCount(int serviceCount) {
		super.setServiceExportSmsLimitCount(serviceCount);
		traceSmsCount(serviceCount);
	}

	public void setServiceExportContactLimitCount(int serviceCount) {
		super.setServiceExportContactLimitCount(serviceCount);
		traceContactCount(serviceCount);
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
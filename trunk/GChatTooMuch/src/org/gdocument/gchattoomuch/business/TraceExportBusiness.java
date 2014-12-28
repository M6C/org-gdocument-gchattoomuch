package org.gdocument.gchattoomuch.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.gdocument.gchattoomuch.log.Logger;
import org.gdocument.gchattoomuch.manager.AuthentificationManager;
import org.gdocument.gchattoomuch.manager.SpreadSheetManager2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.pras.SpreadSheet;
import com.pras.WorkSheet;

public class TraceExportBusiness {

	private static final String TAG = TraceExportBusiness.class.getName();

	private static final String FORMAT_DATE_EXPORT = "yyyy/MM/dd HH:mm:ss";
	private static final String TITLE_WORKSHEET = "trace_export";

	private static final String TYPE_SMS = "SMS";
	private static final String COL_KEY_DATE = "date";
	private static final String COL_KEY_TYPE = "type";
	private static final String COL_KEY_DATA = "data";

	private static final String[] COLUMNS = { COL_KEY_DATE, COL_KEY_TYPE, COL_KEY_DATA };

	public static final String DATA_STATE_NOT_CONNECTED = "NOT CONNECTED";

	public enum TYPE {
		SMS, NEXT_SCHEDULE, SET_SMS_COUNT, SET_SMS_TIME, WIKI_NOT_CONNECTED
	}
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat sdfExport = new SimpleDateFormat(FORMAT_DATE_EXPORT);
	private HashMap<String, List<HashMap<String, String>>> mapData = new HashMap<String, List<HashMap<String,String>>>();
	private HashMap<String, WorkSheet> mapWorkSheet = new HashMap<String, WorkSheet>();
	private CountDownLatch latch = null;
	private TYPE type;
	private String data;

	public void traceExportSms(final Context context, TYPE type, String data) {
		this.type = type;
		this.data = data;

		latch = new CountDownLatch(1);
		new AsyncTask<Object, Void, Void>() {

			@Override
			protected Void doInBackground(Object... params) {
				try {
					if (AuthentificationManager.isDoAuthentification()) {
						buildData();
						try {
							SpreadSheetManager2 spreadSheetManager = SpreadSheetManager2.getInstance(context);
							SpreadSheet sp = spreadSheetManager.getSpreadSheet();
							if (sp != null) {
								createWorkSheet(sp);
								writeWorkSheetData();
							}
						} finally {
							SpreadSheetManager2.releaseInstance();
						}
					}
				} finally {
					latch.countDown();
				}
				return null;
			}
		}.execute();
		try {
			latch.await();
		} catch (InterruptedException e) {
			logMe(e);
		}
	}

	private void createWorkSheet(SpreadSheet sp) {
		if (sp != null) {
			for (String key : mapData.keySet()) {
				String title = TITLE_WORKSHEET + "_" + key;

				if (!mapWorkSheet.containsKey(title)) {
					try {
						List<WorkSheet> workSheets = sp.getWorkSheet(title, false);

						WorkSheet workSheet = null;
						if (workSheets == null || workSheets.size() == 0) {
							logMe("createWorkSheet ### Creating WorkSheet for ListFeed ###");
							workSheet  = sp.addListWorkSheet(title, 1, COLUMNS);
							logMe("createWorkSheet WorkSheet '" + workSheet + "' created");
						}
						else {
							workSheet = workSheets.get(0);
						}
						mapWorkSheet.put(key, workSheet);
					} catch (RuntimeException ex) {
						logMe(ex);
					}
				}
			}
		}
	}

	private void buildData() {
		List<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
		mapData.put(TYPE_SMS, list);
		HashMap<String, String> row_data = new HashMap<String, String>();
		row_data.put(COL_KEY_DATE, sdfExport.format(new Date()));
		row_data.put(COL_KEY_TYPE, type.name());
		row_data.put(COL_KEY_DATA, data);
		list.add(row_data);
	}

	private void writeWorkSheetData() {
		try {
			for (String key : mapData.keySet()) {
				
				WorkSheet workSheet = mapWorkSheet.get(key);
				if (workSheet != null) {
					List<HashMap<String, String>> listRowData = mapData.get(key);
					logMe("writeWorkSheetData key:" + key + " start listRowData.size:" + listRowData.size());
					for(HashMap<String, String> row : listRowData) {
						workSheet.addListRow(row);
					}
					logMe("writeWorkSheetData key:" + key + " end");
				} else {
					logMe("writeWorkSheetData WorkSheet null for key:" + key);
				}
			}
		} catch (RuntimeException ex) {
			logMe(ex);
		}
	}

	private static void logMe(String msg) {
		Logger.logMe(TAG, msg);
	}

	private static void logMe(Exception ex) {
		Logger.logMe(TAG, ex);
	}
}
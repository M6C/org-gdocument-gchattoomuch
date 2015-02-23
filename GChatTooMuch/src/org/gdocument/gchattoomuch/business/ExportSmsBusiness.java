package org.gdocument.gchattoomuch.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.gdocument.gchattoomuch.db.service.SmsService;
import org.gdocument.gchattoomuch.lib.log.Logger;
import org.gdocument.gchattoomuch.lib.manager.AuthentificationManager;
import org.gdocument.gchattoomuch.manager.ScheduleServiceManager;
import org.gdocument.gchattoomuch.manager.SmsManager;
import org.gdocument.gchattoomuch.manager.SpreadSheetManager2;
import org.gdocument.gchattoomuch.model.Sms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.pras.SpreadSheet;
import com.pras.WorkSheet;

public class ExportSmsBusiness {

	private static final String TAG = ExportSmsBusiness.class.getName();

	private static final String FORMAT_DATE_EXPORT = "yyyy/MM/dd HH:mm";
	private static final String FORMAT_DATE_WORKSHEET = "yyyyMM";
	private static final String TITLE_WORKSHEET = "report";

	private static final String COL_KEY_ID = "id";
	private static final String COL_KEY_DATE = "date";
	private static final String COL_KEY_ADDRESS = "address";
	private static final String COL_KEY_BODY = "body";
	private static final String COL_KEY_TYPE = "type";

	private static final String[] COLUMNS = { COL_KEY_ID, COL_KEY_DATE, COL_KEY_ADDRESS, COL_KEY_BODY, COL_KEY_TYPE };

	public void exportSms(final Context context) {

		new AsyncTask<Object, Void, Void>() {

			@SuppressLint("SimpleDateFormat")
			private SimpleDateFormat sdfTitle = new SimpleDateFormat(FORMAT_DATE_WORKSHEET);
			@SuppressLint("SimpleDateFormat")
			private SimpleDateFormat sdfExport = new SimpleDateFormat(FORMAT_DATE_EXPORT);

			private SmsService smsService;
			private List<Sms> listSms;
			private List<Sms> listSmsDB;
			private HashMap<String, List<HashMap<String, String>>> mapData = new HashMap<String, List<HashMap<String,String>>>();
			private HashMap<String, WorkSheet> mapWorkSheet = new HashMap<String, WorkSheet>();

			protected void onPreExecute() {
				smsService = new SmsService(context, null);
				listSmsDB = smsService.getList();
				listSms = SmsManager.getInstance().getListExcept(context, listSmsDB);
			};

			@Override
			protected Void doInBackground(Object... params) {
				buildData();

				if (AuthentificationManager.isDoAuthentification()) {
					try {
						traceExport();

						SpreadSheet spreadSheets = SpreadSheetManager2.getInstance(context).getSpreadSheet();
						if (spreadSheets != null) {
							createWorkSheet(spreadSheets);
							writeWorkSheetData();
						}
					} finally {
						SpreadSheetManager2.releaseInstance();
					}
				} else {
					exportSmsInDb();
				}
				return null;
			}

			private void traceExport() {
				try {
					new TraceExportBusiness().traceExportSms(context, TraceExportBusiness.TYPE.SMS, Integer.toString(listSms.size()));
				} catch(RuntimeException ex) {
					logMe(ex);
				}
			}

			private void createWorkSheet(SpreadSheet spreadSheets) {
				if (spreadSheets != null) {
					for (String key : mapData.keySet()) {
						String title = TITLE_WORKSHEET + "_" + key;
	
						if (!mapWorkSheet.containsKey(title)) {
							try {
								List<WorkSheet> workSheets = spreadSheets.getWorkSheet(title, false);
	
								WorkSheet workSheet = null;
								if (workSheets == null || workSheets.size() == 0) {
									logMe("createWorkSheet ### Creating WorkSheet for ListFeed ###");
									workSheet  = spreadSheets.addListWorkSheet(title, 1, COLUMNS);
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
				if (listSms != null) {
					List<HashMap<String, String>> list = null;
//					for (Sms sms : listSms) {
					int limit = ScheduleServiceManager.getInstance(context).getServiceExportSmsLimitCount();
					int size = (limit >= 0  && listSms.size() > limit ? limit : listSms.size());
					for (int i=0 ; i < size ; i++) {
						Sms sms = listSms.get(i);
						long date = Long.parseLong(sms.getDate());
						String title = sdfTitle.format(new Date(date));
						if (mapData.containsKey(title)) {
							list = mapData.get(title);
						} else {
							list = new ArrayList<HashMap<String,String>>();
							mapData.put(title, list);
						}
						HashMap<String, String> row_data = new HashMap<String, String>();
						row_data.put(COL_KEY_ID, "" + sms.getId());
						row_data.put(COL_KEY_DATE, (date <= 0 ? "" : sdfExport.format(new Date(date))));
						row_data.put(COL_KEY_ADDRESS, sms.getAddress());
						row_data.put(COL_KEY_BODY, sms.getBody());
						row_data.put(COL_KEY_TYPE, sms.getType());
	
						list.add(row_data);
					}
				}
				logMe("buildData mapData.size:" + mapData.size());
			}

			private void writeWorkSheetData() {
				try {
					for (String key : mapData.keySet()) {
						
						WorkSheet workSheet = mapWorkSheet.get(key);
						if (workSheet != null) {
							List<HashMap<String, String>> listRowData = mapData.get(key);
							logMe("writeWorkSheetData key:" + key + " start listRowData.size:" + listRowData.size());
							for(HashMap<String, String> row : listRowData) {
//								logMe("writeWorkSheetData key:" + key + " row.size:" + row.size());
								workSheet.addListRow(row);
								saveDB(row);
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

			private void exportSmsInDb() {
				for (String key : mapData.keySet()) {
					List<HashMap<String, String>> listRowData = mapData.get(key);
					logMe("exportSmsInDb key:" + key + " start listRowData.size:" + listRowData.size());
					for(HashMap<String, String> row : listRowData) {
						saveDB(row);
					}
					logMe("exportSmsInDb key:" + key + " end");
				}
			}

			private void saveDB(HashMap<String, String> data) {
				logMe(
					"saveDB "+ COL_KEY_ID + ":" + data.get(COL_KEY_ID) + 
					" " + COL_KEY_DATE + ":" + data.get(COL_KEY_DATE) +
					" " + COL_KEY_ADDRESS + ":" + data.get(COL_KEY_ADDRESS) +
					" " + COL_KEY_BODY + ":" + data.get(COL_KEY_BODY) +
					" " + COL_KEY_TYPE + ":" + data.get(COL_KEY_TYPE)
				);
				Sms sms = new Sms();
				sms.setId(Long.parseLong(data.get(COL_KEY_ID)));
				sms.setDate(data.get(COL_KEY_DATE));
				sms.setAddress(data.get(COL_KEY_ADDRESS));
				sms.setBody(data.get(COL_KEY_BODY));
				sms.setType(data.get(COL_KEY_TYPE));
				smsService.create(sms);
			}

//			private void buildWorkSheetMax() {
//				try {
//					for (String key : mapData.keySet()) {
//						
//						WorkSheet workSheet = mapWorkSheet.get(key);
//						if (workSheet != null) {
//							int rowCount = workSheet.getRowCount();
//							ArrayList<WorkSheetRow> rows = workSheet.getData(false, true, null, null);
//							
//							StringBuffer record = new StringBuffer(key).append("\n");
//							
//							record.append("Number row Count: "+ rowCount+"\n");
//							record.append("Number of Records: "+ rows.size()+"\n");
//							
//							for(int i=0; i<rows.size(); i++){
//								WorkSheetRow row = rows.get(i);
//								record.append("[ Row ID "+ (i + 1) +" ]\n");
//								
//								ArrayList<WorkSheetCell> cells = row.getCells();
//								
//								for(int j=0; j<cells.size(); j++){
//									WorkSheetCell cell = cells.get(j);
//									record.append(cell.getName() +" = "+ cell.getValue() +"\n"); 
//								}
//							}
//
//							System.out.println("buildWorkSheetMax detail:" + record.toString());
//						} else {
//							logMe("buildWorkSheetMax WorkSheet null for key:" + key);
//						}
//					}
//				} catch (RuntimeException ex) {
//					logMe(ex);
//				}
//			}
		}.execute();
	}

	private static void logMe(String msg) {
		Logger.logMe(TAG, msg);
	}

	private static void logMe(Exception ex) {
		Logger.logMe(TAG, ex);
	}
}
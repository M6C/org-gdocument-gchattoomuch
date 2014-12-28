package org.gdocument.gchattoomuch.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class ConnectionManager {

	private static ConnectionManager instance = null;
	private Context context;
	private ConnectivityManager connManager;

	private ConnectionManager(Context context) {
		this.context = context.getApplicationContext();
		connManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	public static ConnectionManager getInstance(Context context) {
		if (instance == null) {
			instance = new ConnectionManager(context);
		}
		return instance;
	}

	public boolean isWifiConnected() {
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		return mWifi.isConnected();
	}
}
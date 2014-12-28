package org.gdocument.gchattoomuch.util;

import android.content.Context;
import android.net.Uri;

public class UriUtilities {

	public static Uri getUriFromValues(Context context, int id) {
		return Uri.parse(context.getString(id)+":");
	}
}

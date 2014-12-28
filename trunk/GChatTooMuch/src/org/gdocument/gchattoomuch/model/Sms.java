package org.gdocument.gchattoomuch.model;

import com.cameleon.common.android.model.GenericDBPojo;

public class Sms extends GenericDBPojo<Long> {

	private static final long serialVersionUID = 1L;

	private String address;
	private String body;
	private String read;
	private String date;
	private String type;

	public Sms() {
		super();
	}

	public Sms(Long id) {
		super(id);
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getRead() {
		return read;
	}

	public void setRead(String read) {
		this.read = read;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		String ret = super.toString();
		ret += " | Sms [address=" + address + ", body=" + body + ", read=" + read + ", date=" + date + ", type=" + type + "]";
		return ret;
	}

}
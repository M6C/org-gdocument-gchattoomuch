package org.gdocument.gchattoomuch.model;

import com.cameleon.common.android.model.GenericDBPojo;

public class Phone extends GenericDBPojo<Long> {

	private static final long serialVersionUID = 1L;

	private String number;
	private int type;
	private Long idContact;

	public Phone() {
		super();
	}

	public Phone(Long id) {
		super(id);
	}

	public String getNumber() {
		return number;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Long getIdContact() {
		return idContact;
	}

	public void setIdContact(Long idContact) {
		this.idContact = idContact;
	}
}
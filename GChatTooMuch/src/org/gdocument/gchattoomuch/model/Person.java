package org.gdocument.gchattoomuch.model;

import com.cameleon.common.android.model.GenericDBPojo;

public class Person extends GenericDBPojo<Long> {

	private static final long serialVersionUID = 1L;

	private String firstName;
	private String lastName;
	private String birthday;
	private String phonenumber;
	private String address;
	private String postalCode;
	private String locality;

	public Person() {
		super();
	}

	public Person(Long id) {
		super(id);
	}

	public String getFullName() {
		String name = "";
		if (getFirstName() != null && !getFirstName().isEmpty()) {
			name += getFirstName();
		}
		if (getLastName() != null && !getLastName().isEmpty()) {
			if (!name.isEmpty()) {
				name += " ";
			}
			name += getLastName();
		}
		return name;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
}
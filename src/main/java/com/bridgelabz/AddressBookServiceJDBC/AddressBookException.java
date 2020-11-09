package com.bridgelabz.AddressBookServiceJDBC;

public class AddressBookException extends Exception {
	public String message;
	public AddressBookException(String message) {
		super(message);
	}
}

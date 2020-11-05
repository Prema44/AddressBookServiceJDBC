package com.breidgelabz.AddressBookServiceJDBC;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.bridgelabz.AddressBookServiceJDBC.AddressBookService;
import com.bridgelabz.AddressBookServiceJDBC.Contact;
import com.bridgelabz.AddressBookServiceJDBC.DatabaseException;

public class AddressBookTest {
	
	@Test
	public void givenContactDataInDB_WhenRetrieved_ShouldMatchContactCount() throws DatabaseException {
		AddressBookService addressBookService = new AddressBookService();
		List<Contact> contactData = addressBookService.readContactDBData();
		assertEquals(4, contactData.size());
	}
	
	@Test
	public void givenNewDataForContact_WhenUpdated_ShouldBeInSync() throws DatabaseException, SQLException {
		AddressBookService addressBookService = new AddressBookService();
		List<Contact> contactData = addressBookService.readContactDBData();
		addressBookService.updateContactData("Sakshat", "Bagadi", Long.parseLong("8104874346"));
		addressBookService.readContactDBData();
		boolean result = addressBookService.checkContactDataSync("Sakshat", "Bagadi");
		assertEquals(true, result);
	}
	
	@Test
	public void givenDateRangeForContactsAddedInDateRange_ShouldMatchCount() throws DatabaseException, SQLException {
		AddressBookService addressBookService = new AddressBookService();
		LocalDate start = LocalDate.of(2017, 01, 01);
		LocalDate end = LocalDate.now();
		List<Contact> contactList = addressBookService.getContactByDate(start, end);
		assertEquals(3, contactList.size());
	}
	
	@Test
	public void givenContacts_WhenRetrievedByCity_ShouldMatchCount() throws DatabaseException {
		AddressBookService addressBookService = new AddressBookService();
		assertTrue(addressBookService.getContactByCity().get("Kandivali").equals(1));
	}
	
	@Test
	public void givenContacts_WhenRetrievedByState_ShouldMatchCount() throws DatabaseException {
		AddressBookService addressBookService = new AddressBookService();
		assertTrue(addressBookService.getContactByState().get("Maharashtra").equals(2));
	}
}

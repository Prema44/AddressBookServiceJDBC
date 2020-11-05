package com.breidgelabz.AddressBookServiceJDBC;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

}

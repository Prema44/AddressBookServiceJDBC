package com.breidgelabz.AddressBookServiceJDBC;

import static org.junit.Assert.assertEquals;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import com.bridgelabz.AddressBookServiceJDBC.AddressBookService;
import com.bridgelabz.AddressBookServiceJDBC.Contact;
import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

class FakeRESTAPIJsonServerTest {

	@Before
	public void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	}

	/**
	 * sending get request and retrieving all data from JSON server
	 * 
	 * @return
	 */
	private Contact[] getContactList() {
		Response response = RestAssured.get("/contacts");
		System.out.println("Contact entries in JSONServer:\n" + response.asString());
		String responseBody = response.getBody().asString();
		System.out.println("Response Body is =>  " + responseBody);
		Contact[] arrayOfContact = new Gson().fromJson(response.asString(), Contact[].class);
		return arrayOfContact;
	}

	@Test
	public void givenContactDataInJSONServer_WhenRetrieved_ShouldMatchTheCount() {
		Contact[] arrayOfContact = getContactList();
		AddressBookService addressBookFileService = new AddressBookService(Arrays.asList(arrayOfContact));
		long entries = addressBookFileService.countEntries();
		assertEquals(15, entries);
	}
	
	/**
	 * adds new contacts to JSON server and returns response
	 * 
	 * @param newContacts
	 * @return
	 */
	private Response addContactToJsonServer(Contact contact) {
		String json = new Gson().toJson(contact);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(json);
		return request.post("/contacts");
	}

	@Test
	public void givenMultipleNewContacts_WhenAdded_ShouldMatch201ResponseAndCount() {
		List<Contact> newContacts = Arrays.asList(
				new Contact("Sarthak", "Bagadi", "Karad", "Satara", "Maharashtra", 525252, 0000000000, "joeb@gmail.com",
						LocalDate.now()),
				new Contact("Samiksha", "Raut", "Panchgani", "Sangli", "Maharashtra", 525253, 0000000000,
						"saurabhr@gmail.com", LocalDate.now()),
				new Contact("Kamala", "Harris", "Vita", "Sangli", "Maharashtra", 858585, 0000000000, "kamalah@gmail.com",
						LocalDate.now()));
		AddressBookService addressBookFileService = new AddressBookService(Arrays.asList(getContactList()));
		newContacts.forEach(contact -> {
			Runnable task = () -> {
				Response response = addContactToJsonServer(contact);
				int statusCode = response.getStatusCode();
				assertEquals(201, statusCode);
				addressBookFileService.addToApplicationMemory(contact);
			};
			Thread thread = new Thread(task, contact.getFirstName());
			thread.start();
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		long entries = addressBookFileService.countEntries();
		assertEquals(18, entries);
	}	

}

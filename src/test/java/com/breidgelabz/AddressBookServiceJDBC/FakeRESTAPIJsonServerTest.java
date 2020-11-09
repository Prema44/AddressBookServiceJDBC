package com.breidgelabz.AddressBookServiceJDBC;

import static org.junit.Assert.assertEquals;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import com.bridgelabz.AddressBookServiceJDBC.AddressBookService;
import com.bridgelabz.AddressBookServiceJDBC.Contact;
import com.bridgelabz.AddressBookServiceJDBC.DatabaseException;
import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

class FakeRESTAPIJsonServerTest {

	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	}
	private Contact[] getContactList() {
		Response response = RestAssured.get("/contact");
		System.out.println("Contact entries in JSONServer:\n"+response.asString());
		Contact[] arrayOfContact = new Gson().fromJson(response.asString(),Contact[].class);
		return arrayOfContact;
	}
	@Test
	public void givenConatactDataInJSONServer_WhenRetrieved_ShouldMatchTheCount() {
		Contact[] arrayOfContact = getContactList();
		AddressBookService addressBookService = new AddressBookService(Arrays.asList(arrayOfContact));
		long entries = addressBookService.getCount();
		assertEquals(1,entries);

	}
	
	private Response addContactToJsonServer(Contact contact) {
		String contactJson = new Gson().toJson(contact);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(contactJson);
		return request.post("/contact");
	}

	@Test
	public void givenMultipleNewContacts_WhenAdded_ShouldMatch201ResponseAndCount() {
		Contact[] arrayOfContact = getContactList();
		AddressBookService addService = new AddressBookService(Arrays.asList(arrayOfContact));
		Contact[] contacts = {new Contact("Hardik","Pandya","Ahmedabad", "Maharashtra", 444001, 8850273350L,"abcd@gmail.com",2),
				new Contact("Sachin","Tendulkar","Mumbai", "Maharashtra", 444001, 7887483853L,"abcd@gmail.com",2)};
		List<Contact> contactList = Arrays.asList(contacts);
		contactList.forEach(contact -> {
			Runnable task = () -> {
				Response response = addContactToJsonServer(contact);
				int statusCode = response.getStatusCode();
				assertEquals(201, statusCode);
				Contact newContact = new Gson().fromJson(response.asString(), Contact.class);
				addService.addContact(newContact);
			};
			Thread thread = new Thread(task, contact.firstName);
			thread.start();
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		int count = addService.getCount();
		assertEquals(3, count);
	}
	
	@Test 
	public void givenNewPhoneForContact_WhenUpdated_ShouldMatch200Request() throws DatabaseException, SQLException {
		Contact[] arrayOfContact = getContactList();
		AddressBookService addService = new AddressBookService(Arrays.asList(arrayOfContact));
		addService.updateContactData("Sachin", "Tendulkar", 7887483853L);
		Contact contact = addService.getContact("Sachin", "Tendulkar");
		String contactJson = new Gson().toJson(contact);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type","application/json");
		request.body(contactJson);
		Response response = request.put("/contact/"+contact.id);
		int statusCode = response.getStatusCode();
		assertEquals(200,statusCode);			
	}
	
	@Test 
	public void givenContactToDelete_WhenDeleted_ShouldMatch200ResponseAndCount() throws DatabaseException, SQLException {
		Contact[] arrayOfContact = getContactList();
		AddressBookService addService = new AddressBookService(new LinkedList<Contact>(Arrays.asList(arrayOfContact)));
		Contact contact = addService.getContact("Sachin", "Tendulkar");
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type","application/json");
		Response response = request.delete("/contact/"+contact.id);
		int statusCode = response.getStatusCode();
		assertEquals(200,statusCode);
		addService.deleteContact(contact.firstName, contact.lastName);
		assertEquals(2,getContactList().length);
	}
}

package com.bridgelabz.AddressBookServiceJDBC;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

public class AddressBookService {
	private List<Contact> contactList = new ArrayList<Contact>();
	private AddressBookDBService addressBookDBService;
	
	public AddressBookService() {
		addressBookDBService = AddressBookDBService.getInstance();
	}
	public static void writeAddressBook(Map<String, AddressBook> map) {
		StringBuffer buffer = new StringBuffer("");
		for(String city : map.keySet()) {
			map.get(city).getAddressBook().forEach(c -> buffer.append(c.toString().concat("\n")));
		}
		try {
			Path path = Paths.get("./addressbook.txt");
			Files.write(path, buffer.toString().getBytes());
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		System.out.println("Data Written Successfully");
	}
	public static void writeContactAsCSV(Contact contact) 
	{ 
		Path path = Paths.get("addressBook.csv");
		try { 
			FileWriter outputfile = new FileWriter(path.toFile(), true); 
			CSVWriter writer = new CSVWriter(outputfile); 
			//add data to csv
			String[] data = contact.toString().split(",");
			writer.writeNext(data);
			// closing writer connection 
			writer.close(); 
		} 
		catch (IOException exception) { 
			exception.printStackTrace(); 
		} 
	} 
	public static void readAddressBookCSV() 
	{ 
	    try {  
	        FileReader filereader = new FileReader(Paths.get("addressBook.csv").toFile()); 
	        CSVReader csvReader = new CSVReaderBuilder(filereader).build();  
	        List<String[]> contactData = csvReader.readAll(); 
	        // print Data 
	        for (String[] row : contactData) { 
	            for (String cell : row) { 
	                System.out.print(cell + "\t"); 
	            } 
	            System.out.println(); 
	        } 
	    } 
	    catch (Exception exception) { 
	        exception.printStackTrace(); 
	    } 
	} 
	public static void writeAsJson(Contact contact) {
		Gson gson = new Gson();
		String json = gson.toJson(contact);
		try {
			FileWriter writer = new FileWriter(Paths.get("addressBook.json").toFile(), true);
			writer.write(json);
			writer.close();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		System.out.println(json);	     
	}
	
	public static void readAsJson() {
		String nextLine = "";
		Gson gson = new Gson();
		BufferedReader br;
		try {
			br = new BufferedReader(
					new FileReader(Paths.get("addressBook.json").toFile()));
			JsonStreamParser parser = new JsonStreamParser(br);
			while(parser.hasNext())
			{
				JsonElement element = parser.next();
				if (element.isJsonObject()) {
					Contact contact = gson.fromJson(element, Contact.class);
					System.out.println(contact);
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	public List<Contact> readContactDBData() throws DatabaseException {
			this.contactList = addressBookDBService.readData();
		return this.contactList;
	}
}
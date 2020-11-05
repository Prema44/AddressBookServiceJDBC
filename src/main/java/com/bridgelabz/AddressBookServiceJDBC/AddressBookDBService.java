package com.bridgelabz.AddressBookServiceJDBC;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.mysql.jdbc.PreparedStatement;

public class AddressBookDBService {
	private static AddressBookDBService addressBookDBService;
	private Connection connection = null;
	private PreparedStatement preparedStatement = null;
	private AddressBookDBService() {
	}
	public static AddressBookDBService getInstance() {
		if(addressBookDBService == null) {
			addressBookDBService = new AddressBookDBService();
		}
		return addressBookDBService;
	}
	
	/**
	 * Function returns a connection
	 * @return
	 * @throws DatabaseException
	 */
	private Connection getConnection() throws DatabaseException {
		String jdbcurl = "jdbc:mysql://localhost:3306/addressbook_service?useSSL=false";
		String userName = "root";
		String password = "Prema@44";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(jdbcurl, userName, password);
		}
		catch(Exception e) {
			throw new DatabaseException("Connection was unsuccessful");
		}
		return connection;
	}
	//cache prepared statement
	private void getPreparedStatement() throws SQLException, DatabaseException {
		this.getConnection();
		if(preparedStatement == null) {
			String sql = "Select * from contacts inner join address using(contactId) where firstname = ? and lastname = ?;";
		preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
		}
	}
	//retrieval query
	public List<Contact> readData() throws DatabaseException {
		String sql = "select * from contacts inner join address using(contactid) inner join bookmap using(contactid) inner join addressbook using(bookid) ; " ;
		return this.getContactData(sql);
	}
	/**
	 * Function retrieves the contact details and returns a list of contacts
	 * @param sql
	 * @return
	 * @throws DatabaseException
	 */
	private List<Contact> getContactData(String sql) throws DatabaseException {
		List<Contact> contactList = new ArrayList<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			contactList = getContactData(resultSet);
		} catch (SQLException e) {
			throw new DatabaseException("Unable to get Contacts");
		}
		return contactList;		
	}
	public List<Contact> getContactData(String firstName, String lastName) throws DatabaseException{
		try {
			getPreparedStatement();
			preparedStatement.setString(1,  firstName);
			preparedStatement.setString(2,  lastName);
			return getContactData(preparedStatement.executeQuery());
		}
		catch(SQLException exception) {
			throw new DatabaseException("Unable to get contact data");
		}
	}
	private List<Contact> getContactData(ResultSet resultSet) throws DatabaseException {
		List<Contact> contactList = new ArrayList<Contact>();
		try {
			while (resultSet.next()) {
				String firstname = resultSet.getString("firstname");
				String lastname = resultSet.getString("lastname");
				int zip = resultSet.getInt("zip");
				String city = resultSet.getString("city");
				String state = resultSet.getString("state");
				long phoneNumber = resultSet.getLong("phonenumber");
				String email = resultSet.getString("email");
				contactList.add(new Contact(firstname, lastname, city, state, zip, phoneNumber,email));
			}
		}
		catch(SQLException exception){
			throw new DatabaseException("Unable to get contacts");
		}
		return contactList;
	}
	public int updateContactData(String firstName, String lastName, long phone) throws DatabaseException, SQLException {
		connection = this.getConnection();
		String sql = "Update contacts inner join address using(contactId) set phonenumber = ? where firstname = ? and lastname = ? ; " ; 
		PreparedStatement prepareStatement = (PreparedStatement) connection.prepareStatement(sql);
		prepareStatement.setLong(1, phone);
		prepareStatement.setString(2, firstName);
		prepareStatement.setString(3, lastName);
		return prepareStatement.executeUpdate();
	}
	
	public List<Contact> readDataForGivenDateRange(LocalDate start, LocalDate end) throws DatabaseException{
		String sql = String.format("Select * from contacts inner join address using(contactid) where date_added between '%s' and '%s' ;", Date.valueOf(start), Date.valueOf(end));
	    return getContactData(sql);
	}
	
	public Map<String, Integer> getContactsByAttribute(String function) throws  DatabaseException{
		Map<String, Integer> contactMap = new HashMap<>();
		String sql = String.format("select %s,count(*) from contacts join address using(contactid) group by %s;",function, function) ;
		try {
			Statement statement = getConnection().createStatement();
			ResultSet result = statement.executeQuery(sql);
			while(result.next()) {
				String cityOrState = result.getString(1);
				Integer count = result.getInt(2);
				contactMap.put(cityOrState, count);
			}
		}
		catch(SQLException exception) {
			throw new DatabaseException("Unable to calculate");
		}
		return contactMap;
	}
}

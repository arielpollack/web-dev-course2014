package models;

import com.owlike.genson.annotation.JsonIgnore;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class User {
	
	Integer id;
	
	public String firstName, lastName, idNumber, phone, email;
	
	List<String> preferredDays, preferredHours;

	@JsonIgnore
	Boolean isAdmin;
	
	public User() {} // JAXB requirement
	
	public User(String fname, String lname, String idNumber, String phone, String email, Integer id)
	{
		this.firstName = fname;
		this.lastName = lname;
		this.idNumber = idNumber;
		this.phone = phone;
		this.email = email;
		this.id = id;
	}
	
	public User(String aliasIdentifier, ResultSet rs) throws SQLException
	{
		this(rs.getString(aliasIdentifier + "fname"), 
				rs.getString(aliasIdentifier +"lname"), 
				rs.getString(aliasIdentifier +"id_number"), 
				rs.getString(aliasIdentifier +"phone"), 
				rs.getString(aliasIdentifier +"email"),
				Integer.parseInt(rs.getString(aliasIdentifier +"id")));
	}

	@JsonIgnore
	public String getFullName()
	{
		return this.firstName + " " + this.lastName;
	}
	
	public void setFirstName(String fname)
	{
		this.firstName = fname;
	}
	
	public void setLastName(String lname)
	{
		this.lastName = lname;
	}
	
	public void setIdNumber(String idNumber)
	{
		this.idNumber= idNumber;
	}
	
	public void setPhone(String phone)
	{
		this.phone = phone;
	}
	
	public void setEmail(String email)
	{
		this.email = email;
	}
	
	public String getFirstName()
	{
		return this.firstName;
	}
	
	public String getLastName()
	{
		return this.lastName;
	}
	
	public String getIdNumber()
	{
		return this.idNumber;
	}
	
	public String getPhone()
	{
		return this.phone;
	}
	
	public String getEmail()
	{
		return this.email;
	}
	
	public Integer getId()
	{
		return this.id;
	}

	@JsonIgnore
	public Boolean getIsAdmin()
	{
		return this.isAdmin;
	}
}

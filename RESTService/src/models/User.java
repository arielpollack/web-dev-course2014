package models;

import com.owlike.genson.annotation.JsonIgnore;
import com.owlike.genson.annotation.JsonProperty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class User {

    String id;

    @JsonProperty("first_name")
    String firstName;
    @JsonProperty("last_name")
    String lastName;
    @JsonProperty("id_number")
    String idNumber;
    String phone;
    String email;

    @JsonProperty("preferred_days")
    List<String> preferredDays;
    @JsonProperty("preferred_hours")
    List<String> preferredHours;

    @JsonProperty("is_admin")
    Boolean isAdmin;

    public User() {
    } // JAXB requirement

    public User(String fname, String lname, String idNumber, String phone, String email, String id, Boolean isAdmin) {
        this.firstName = fname;
        this.lastName = lname;
        this.idNumber = idNumber;
        this.phone = phone;
        this.email = email;
        this.id = id;
        this.isAdmin = isAdmin;
    }

    public User(String aliasIdentifier, ResultSet rs) throws SQLException {
        this(rs.getString(aliasIdentifier + "fname"),
                rs.getString(aliasIdentifier + "lname"),
                rs.getString(aliasIdentifier + "id_number"),
                rs.getString(aliasIdentifier + "phone"),
                rs.getString(aliasIdentifier + "email"),
                rs.getString(aliasIdentifier + "id"),
                rs.getBoolean(aliasIdentifier + "is_admin"));
    }

    public void setFirstName(String fname) {
        this.firstName = fname;
    }

    public void setLastName(String lname) {
        this.lastName = lname;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("first_name")
    public String getFirstName() {
        return this.firstName;
    }

    @JsonProperty("last_name")
    public String getLastName() {
        return this.lastName;
    }

    @JsonProperty("full_name")
    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    @JsonProperty("id_number")
    public String getIdNumber() {
        return this.idNumber;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getEmail() {
        return this.email;
    }

    public String getId() {
        return this.id;
    }

    @JsonIgnore
    public Boolean isAdmin() {
        return this.isAdmin;
    }

    @Override
    public String toString() {
        return "{ id = " + id + ",name = " + getFullName() + " }";
    }

    public void setId(Integer id) {
        this.id = String.valueOf(id);
    }
}

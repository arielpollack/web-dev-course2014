package models;

import com.owlike.genson.annotation.JsonDateFormat;
import com.owlike.genson.annotation.JsonIgnore;

import java.util.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Appointment {

    Integer id;

    // we ignore users in json because we will always fetch them separately
    // for getting the most updated record, and set them straight to the object
    @JsonIgnore
    User user, therapist;
    @JsonDateFormat(asTimeInMillis = true)
    Date date;

    public Appointment() {
    } // JAXB requirement

    public Appointment(Date date, User user, User therapist, Integer id) {
        this.date = date;
        this.user = user;
        this.therapist = therapist;
        this.id = id;
    }

    public Appointment(String aliasIdentifier, ResultSet rs) throws SQLException {
        this(rs.getDate(aliasIdentifier + "date"),
                new User("u_", rs),
                new User("t_", rs),
                rs.getInt(aliasIdentifier + "id"));
    }

    @JsonDateFormat(asTimeInMillis = true)
    public Date getDate() {
        return this.date;
    }

    public User getUser() {
        return this.user;
    }

    public User getTherapist() {
        return this.therapist;
    }

    public Integer getId() {
        return this.id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTherapist(User therapist) {
        this.therapist = therapist;
    }

    @Override
    public String toString() {
        String self = "{ id = " + id + ", date = " + date;
        if (user != null) {
            self += ", user = " + user.toString();
        }
        if (therapist != null) {
            self += ", therapist = " + therapist.toString();
        }
        self += " }";
        return self;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}

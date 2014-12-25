package models;

import java.sql.Date;

public class AppointmentBuilder {
	Integer _id = 0;
	User _user, _therapist;
	Date _date;
	
	public AppointmentBuilder() {}
	
	public Appointment buildAppointment()
	{
		return new Appointment(_date, _user, _therapist, _id);
	}
	
	public AppointmentBuilder date(Date date)
	{
		this._date = date;
		return this;
	}
	
	public AppointmentBuilder user(User user)
	{
		this._user = user;
		return this;
	}
	
	public AppointmentBuilder therapist(User therapist)
	{
		this._therapist = therapist;
		return this;
	}
	
	public AppointmentBuilder id(Integer id)
	{
		this._id = id;
		return this;
	}
}

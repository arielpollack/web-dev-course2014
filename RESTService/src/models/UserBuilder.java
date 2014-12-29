package models;

import java.util.List;

public class UserBuilder {

    String _id;
    String _firstName, _lastName, _idNumber, _phone, _email;
    List<String> _preferredDays, _preferredHours;
    Boolean _isAdmin;

    public UserBuilder() {
    }

    public User buildUser() {
        return new User(_firstName, _lastName, _idNumber, _phone, _email, _id);
    }

    public UserBuilder firstName(String firstName) {
        this._firstName = firstName;
        return this;
    }

    public UserBuilder lastName(String lastName) {
        this._lastName = lastName;
        return this;
    }

    public UserBuilder idNumber(String idNumber) {
        this._idNumber = idNumber;
        return this;
    }

    public UserBuilder phone(String phone) {
        this._phone = phone;
        return this;
    }

    public UserBuilder email(String email) {
        this._email = email;
        return this;
    }
}

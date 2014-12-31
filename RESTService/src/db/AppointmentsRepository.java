package db;

import db.jdbc.AppointmentsJDBCAdapter;
import db.redis.AppointmentsRedisAdapter;
import exceptions.InvalidParameterException;
import models.Appointment;
import models.User;

import java.util.Date;
import java.util.List;

/**
 * Created by arielpollack on 12/30/14.
 */

public class AppointmentsRepository {

    static private AppointmentsJDBCAdapter jdbsAdapter;
    static private AppointmentsRedisAdapter redisAdapter;

    static {
        jdbsAdapter = AppointmentsJDBCAdapter.getInstance();
        redisAdapter = AppointmentsRedisAdapter.getInstance();
    }

    static public List<Appointment> getBetweenDates(long start, long end) throws InvalidParameterException {
        if (start <= 0 || end <= 0) { // we require entering time limits
            throw new InvalidParameterException();
        }

        List<Appointment> appointments = redisAdapter.getBetweenDates(start, end);
        if (appointments.size() > 0) {
            return appointments;
        }

        appointments = jdbsAdapter.getBetweenDates(start, end);
        for (Appointment appointment : appointments) {
            redisAdapter.insert(appointment);
        }

        return appointments;
    }

    static public List<Appointment> getForUser(User user, long start, long end) throws InvalidParameterException {
        // we don't require time limits because a user have only several appointments
        List<Appointment> appointments = redisAdapter.getForUser(user, start, end);
        if (appointments.size() > 0) {
            return appointments;
        }

        appointments = jdbsAdapter.getForUser(user, start, end);
        for (Appointment appointment : appointments) {
            redisAdapter.insert(appointment);
        }

        return appointments;
    }

    static public Appointment insert(Appointment appointment) {
        Appointment newAppointment;
        if ((newAppointment = jdbsAdapter.insert(appointment)) == null) {
            return null;
        }

        if (!redisAdapter.insert(newAppointment)) {
            System.out.println("Insert to Redis failed");
        }

        return newAppointment;
    }

    static public Boolean update(Appointment appointment) {
        if (!jdbsAdapter.update(appointment)) {
            return false;
        }

        if (!redisAdapter.insert(appointment)) {
            System.out.println("Insert to Redis failed");
        }

        return true;
    }

    static public Boolean delete(Appointment appointment) {
        if (!jdbsAdapter.delete(appointment)) {
            return false;
        }

        if (!redisAdapter.delete(appointment)) {
            System.out.println("Insert to Redis failed");
        }

        return false;
    }
}

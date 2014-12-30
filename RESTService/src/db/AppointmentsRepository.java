package db;

import db.jdbc.AppointmentsJDBCAdapter;
import db.redis.AppointmentsRedisAdapter;
import models.Appointment;
import models.User;

import java.util.Date;
import java.util.List;

/**
 * Created by arielpollack on 12/30/14.
 */

public class AppointmentsRepository {

    static private AppointmentsRepository sharedInstance;
    private AppointmentsJDBCAdapter jdbsAdapter;
    private AppointmentsRedisAdapter redisAdapter;

    static {
        sharedInstance = new AppointmentsRepository();
    }

    private AppointmentsRepository() {
        jdbsAdapter = AppointmentsJDBCAdapter.getInstance();
        redisAdapter = AppointmentsRedisAdapter.getInstance();
    }

    static public Appointment getById(String id) {

    }

    static public List<Appointment> getForDate(Date date) {

    }

    static public List<Appointment> getForUser(User user) {

    }

    static public List<Appointment> getForUserAndDate(User user, Date date) {

    }

    static public Boolean insert(Appointment appointment) {

    }

    static public Boolean update(Appointment appointment) {

    }

    static public Boolean delete(Appointment appointment) {

    }
}

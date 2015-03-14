package db;

import db.jdbc.AppointmentsJDBCAdapter;
import db.redis.AppointmentsRedisAdapter;
import exceptions.InvalidParameterException;
import models.Appointment;
import models.TimeBlock;
import models.User;
import redis.clients.jedis.exceptions.JedisException;

import java.util.*;

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

    static public Appointment get(Integer id) {
        Appointment apt;

        if ((apt = redisAdapter.getById(id.toString())) != null) {
            return apt;
        }

        if ((apt = jdbsAdapter.getById(id)) != null) {
            redisAdapter.insert(apt);
            return apt;
        }

        return null;
    }

    static public Boolean delete(String appointmentId) {
        if (!jdbsAdapter.delete(appointmentId)) {
            return false;
        }

        try {
            redisAdapter.delete(appointmentId);
        } catch (JedisException ex) {
            System.out.println("Delete from Redis failed: " + ex.getMessage());
        }

        return true;
    }

    static public List<TimeBlock> getFreeTimeBlocks(long st, long en) {
        List<TimeBlock> times;

        if (!redisAdapter.needUpdate()) {
            System.out.println("redis not need update");
            return redisAdapter.getFreeTimeBlocks(st, en);
        }

        redisAdapter.clean();

        times = new ArrayList<TimeBlock>();
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long now = calendar.getTimeInMillis();
        for (Integer day, i = 0; i <= 14; i++) {

            day = calendar.get(Calendar.DAY_OF_WEEK);
            calendar.set(Calendar.HOUR_OF_DAY, 9);

            long blockTime = calendar.getTimeInMillis();

            // 18 apts a day, 30 minutes each, 9 hours of opening
            for (int j = 0; j < 18 && calendar.get(Calendar.HOUR_OF_DAY) < 18; j++) {
                times.add(new TimeBlock(day, 30, blockTime));
                calendar.add(Calendar.MINUTE, 30);
                blockTime = calendar.getTimeInMillis();
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // add to redis to later filter by not available times
        redisAdapter.setFreeTimeBlocks(times);

        // fetch appointments for next 2 weeks
        long twoWeeksFromNow = calendar.getTimeInMillis();
        System.out.println(now + " " + twoWeeksFromNow);
        try {
            List<Appointment> appointments = getBetweenDates(now, twoWeeksFromNow);
            for (Appointment appointment : appointments) {
                long start = appointment.getDate().getTime(),
                        end = start + 29 * 60 * 1000; // 29 minutes to not remove the block after
                long removed = redisAdapter.removeFreeTimeBlocks(start, end);
                System.out.println(removed + " time blocks removed between " + start + " and " + end + " because of appointment " + appointment.getId());
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        } finally {
            times = redisAdapter.getFreeTimeBlocks(0, 0);
        }

        return times;
    }
}

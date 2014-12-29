package adapters;

import com.owlike.genson.Genson;
import models.Appointment;
import models.User;
import redis.clients.jedis.Transaction;

import java.util.*;

/**
 * Created by arielpollack on 12/27/14.
 */

public class AppointmentsRedisAdapter extends BaseRedisAdapter {
    protected static final String UID_Prefix = "apt:";
    static Genson genson = new Genson();

    public AppointmentsRedisAdapter() {
        super();
    }

    public List<Appointment> getToday() {
        List<Appointment> appointments = new ArrayList<Appointment>();

        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long now = calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        long tomorrow = calendar.getTimeInMillis();

        System.out.println(now + " " + tomorrow);
        Set<String> keys = jedis.zrangeByScore("apts:daily", now, tomorrow);
        System.out.println(keys);

        for (String key : keys) {
            Appointment appointment = getAppointmentForKey(key);
            if (appointment != null) {
                appointments.add(appointment);
            }
        }

        return appointments;
    }

    public List<Appointment> getForUser(User user, Date startDate, Date endDate) {
        List<Appointment> appointments = new ArrayList<Appointment>();

        String from = startDate != null ? String.valueOf(startDate.getTime()) : "-inf";
        String to = endDate != null ? String.valueOf(endDate.getTime()) : "+inf";
        Set<String> keys = jedis.zrangeByScore(UsersRedisAdapter.UID_Prefix + user.getId() + ":appointments", from, to);

        System.out.println("Fetching for user " + user.getId() + " from " + from + " to " + to);
        Genson genson = new Genson();
        for (String key : keys) {
            Appointment appointment = getAppointmentForKey(key);
            if (appointment != null) {
                appointments.add(appointment);
            }
        }

        return appointments;
    }

    public Boolean insert(Appointment appointment) {
        // objects keys
        String userRedisId = UsersRedisAdapter.UID_Prefix + appointment.getUser().getId();
        String therapistRedisId = UsersRedisAdapter.UID_Prefix + appointment.getTherapist().getId();
        String redisAptId = UID_Prefix + appointment.getId();
        // time in milliseconds
        long time = appointment.getDate().getTime();

        // hash map appointment object
        HashMap<String, String> hash = new HashMap<String, String>();
        hash.put("json", new Genson().serialize(appointment));
        hash.put("user", userRedisId);
        hash.put("therapist", therapistRedisId);
        hash.put("date", String.valueOf(time));

        // insert all data into redis
        Transaction t = jedis.multi();
        t.hmset(redisAptId, hash);
        t.zadd("apts:daily", time, redisAptId);
        t.zadd(userRedisId + ":appointments", time, redisAptId);
        t.zadd(therapistRedisId + ":given_appointments", time, redisAptId);
        return (t.exec().size() > 0);
    }

    private Appointment getAppointmentForKey(String key) {
        if (key == null || key.length() == 0) {
            return null;
        }

        String json = jedis.hget(key, "json");
        System.out.println(json);
        Appointment appointment = genson.deserialize(json, Appointment.class);
        String userKey = jedis.hget(key, "user");
        String therapistKey = jedis.hget(key, "therapist");
        if (userKey != null) {
            User user = genson.deserialize(jedis.get(userKey), User.class);
            appointment.setUser(user);
        }
        if (therapistKey != null) {
            User therapist = genson.deserialize(jedis.get(therapistKey), User.class);
            appointment.setTherapist(therapist);
        }

        return appointment;
    }
}

package db.redis;

import com.owlike.genson.Genson;
import models.Appointment;
import models.TimeBlock;
import models.User;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisException;

import java.util.*;

/**
 * Created by arielpollack on 12/27/14.
 */

public class AppointmentsRedisAdapter extends BaseRedisAdapter {
    static AppointmentsRedisAdapter sharedInstance;

    protected static final String UID_Prefix = "apt:";
    protected static final String APTS_KEY = "appointments";
    protected static final String TIMES_KEY = "time_blocks";

    static Genson genson;

    static {
        sharedInstance = new AppointmentsRedisAdapter();
        genson = new Genson();
    }

    public static AppointmentsRedisAdapter getInstance() {
        return sharedInstance;
    }

    private AppointmentsRedisAdapter() {
        super();
    }

    public List<Appointment> getForUser(User user, long start, long end) {
        List<Appointment> appointments = new ArrayList<Appointment>();

        String from = start > 0 ? String.valueOf(start) : "-inf";
        String to = end > 0 ? String.valueOf(end) : "+inf";
        Set<String> keys = jedis.zrangeByScore(UsersRedisAdapter.UID_Prefix + user.getId() + ":appointments", from, to);

        for (String key : keys) {
            Appointment appointment = getAppointmentForKey(key);
            if (appointment != null) {
                appointments.add(appointment);
            } else {
                System.err.println(key + " was not found");
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
        hash.put("json", genson.serialize(appointment));
        hash.put("user", userRedisId);
        hash.put("therapist", therapistRedisId);
        hash.put("date", String.valueOf(time));

        // insert all data into redis
        Transaction t = jedis.multi();
        t.hmset(redisAptId, hash);
        t.zadd(APTS_KEY, time, redisAptId);
        t.zadd(userRedisId + ":appointments", time, redisAptId);
        t.zadd(therapistRedisId + ":given_appointments", time, redisAptId);
        t.zremrangeByScore(TIMES_KEY, time, time + 30 * 60 * 1000 - 1);
        return (t.exec().size() > 0);
    }

    private Appointment getAppointmentForKey(String key) {
        if (key == null || key.length() == 0) {
            return null;
        }

        String json = jedis.hget(key, "json");
        if (json == null || json.length() == 0) {
            return null;
        }

        Appointment appointment = new Genson().deserialize(json, Appointment.class);
        String userKey = jedis.hget(key, "user");
        String therapistKey = jedis.hget(key, "therapist");
        if (userKey != null) {
            String userJson = jedis.get(userKey);
            if (userJson != null) {
                User user = genson.deserialize(userJson, User.class);
                appointment.setUser(user);
            }
        }
        if (therapistKey != null) {
            String therapistJson = jedis.get(therapistKey);
            if (therapistJson != null) {
                User therapist = genson.deserialize(therapistJson, User.class);
                appointment.setTherapist(therapist);
            }
        }
        return appointment;
    }

    public Appointment getById(String id) {
        return getAppointmentForKey(UID_Prefix + id);
    }

    public List<Appointment> getBetweenDates(long start, long end) {
        List<Appointment> appointments = new ArrayList<Appointment>();

        Set<String> keys = jedis.zrangeByScore(APTS_KEY, start, end);

        for (String key : keys) {
            Appointment appointment = getAppointmentForKey(key);
            if (appointment != null) {
                appointments.add(appointment);
            } else { // appointment not exist anymore
                jedis.zrem(APTS_KEY, key);
            }
        }

        return appointments;
    }

    public void delete(String appointmentId) throws JedisException {
        // objects keys
        String redisAptId = UID_Prefix + appointmentId;

        jedis.del(redisAptId);
        jedis.zrem(APTS_KEY, redisAptId);
    }

    public List<TimeBlock> getFreeTimeBlocks(long start, long end) {
        List<TimeBlock> timeBlocks = new ArrayList<TimeBlock>();
        Set<String> timeJsons = jedis.zrangeByScore(TIMES_KEY, (start != 0 ? String.valueOf(start) : "-inf"), end != 0 ? String.valueOf(end) : "+inf");

        for (String timeJson : timeJsons) {
            timeBlocks.add(genson.deserialize(timeJson, TimeBlock.class));
        }

        return timeBlocks;
    }

    public void setFreeTimeBlocks(List<TimeBlock> times) {
        Transaction t = jedis.multi();
        for (TimeBlock time : times) {
            t.zadd(TIMES_KEY, time.getDate().getTime(), genson.serialize(time));
        }
        t.exec();
    }

    public long removeFreeTimeBlocks(long start, long end) {
        return jedis.zremrangeByScore(TIMES_KEY, start, end);
    }
}

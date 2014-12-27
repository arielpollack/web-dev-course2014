package adapters;

import com.owlike.genson.Genson;
import models.Appointment;
import models.User;
import redis.clients.jedis.Transaction;

import java.util.*;

/**
 * Created by arielpollack on 12/27/14.
 */

public class AppointmentsRedisAdapter extends BaseRedisAdapter
{
    protected String UID_Prefix = "apt:";

    public AppointmentsRedisAdapter() {
        super();
    }

    public List<Appointment> getToday()
    {
        List<Appointment> appointments = new ArrayList<Appointment>();

        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        long tomorrow = calendar.getTimeInMillis();

        System.out.println(now + " " + tomorrow);
        Set<String> keys = jedis.zrangeByScore("apts:daily", now, tomorrow);
        System.out.println(keys);
        Genson genson = new Genson();
        for (String key : keys)
        {
            String json = jedis.hget(key, "json");
            System.out.println(json);
            Appointment appointment = genson.deserialize("{\"id\":3, \"user\":{}}", Appointment.class);
            appointments.add(appointment);
        }

        return appointments;
    }

    public Boolean insert(Appointment appointment)
    {
        HashMap<String, String> hash = new HashMap<String, String>();
        hash.put("json", new Genson().serialize(appointment));
        hash.put("user", "user:" + appointment.getUser().getId());
        hash.put("therapist", "user:" + appointment.getTherapist().getId());
        hash.put("date", "" + appointment.getDate().getTime());

        String redisAptId = UID_Prefix + appointment.getId();
        Transaction t = jedis.multi();
        t.hmset(redisAptId, hash);
        t.zadd("apts:daily", appointment.getDate().getTime(), redisAptId);
        return (t.exec().size() > 0);
    }
}

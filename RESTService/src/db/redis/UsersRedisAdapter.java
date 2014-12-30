package db.redis;

import com.owlike.genson.Genson;
import models.User;
import redis.clients.jedis.Transaction;

/**
 * Created by arielpollack on 12/27/14.
 */

public class UsersRedisAdapter extends BaseRedisAdapter {

    private static UsersRedisAdapter sharedInstance;

    protected static final String UID_Prefix = "user:";

    static {
        sharedInstance = new UsersRedisAdapter();
    }

    public static UsersRedisAdapter getInstance() {
        return sharedInstance;
    }

    private UsersRedisAdapter() {
        super();
    }

    public User getUserWithID(String id) {
        String json = jedis.get(UID_Prefix + id);
        if (json == null || json.length() == 0) {
            return null;
        }

        User user = new Genson().deserialize(json, User.class);

        return user;
    }

    public Boolean insert(User user, String password) {
        String redisUserId = UID_Prefix + user.getId();

        Transaction t = jedis.multi();

        t.set(redisUserId, new Genson().serialize(user));

        // password caching
        if (password != null && password.length() > 0) {
            t.set("user:id_number:" + user.getIdNumber() + ":password", password);
        }

        return (t.exec().size() > 0);
    }


}

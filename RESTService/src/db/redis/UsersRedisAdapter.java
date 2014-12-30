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
    protected static final String ID_NUMBER_Prefix = "user:id_number:";

    static {
        sharedInstance = new UsersRedisAdapter();
    }

    public static UsersRedisAdapter getInstance() {
        return sharedInstance;
    }

    private UsersRedisAdapter() {
        super();
    }

    public User getUserById(String id) {
        String json = jedis.get(UID_Prefix + id);
        if (json == null || json.length() == 0) {
            return null;
        }

        return new Genson().deserialize(json, User.class);
    }

    public Boolean insert(User user, String password) {
        String redisUserId = UID_Prefix + user.getId();

        Transaction t = jedis.multi();

        t.set(redisUserId, new Genson().serialize(user));
        t.set(ID_NUMBER_Prefix + user.getIdNumber() + ":uid", user.getId());

        // password caching
        if (password != null && password.length() > 0) {
            t.set(ID_NUMBER_Prefix + user.getIdNumber() + ":password", password);
        }

        return (t.exec().size() > 0);
    }


    public User getUserByIdNumberAndPassword(String idNumber, String password) {

        // get and check password
        String realPassword = jedis.get(ID_NUMBER_Prefix + idNumber + ":password");
        if (realPassword == null || realPassword.length() == 0 || !realPassword.equals(password)) {
            return null;
        }

        // get user id
        String id = jedis.get(ID_NUMBER_Prefix + idNumber + ":uid");
        if (id == null || id.length() == 0) {
            return null;
        }

        // get user's data
        String json = jedis.get(UID_Prefix + id);
        if (json == null || json.length() == 0) {
            return null;
        }

        return new Genson().deserialize(json, User.class);
    }
}

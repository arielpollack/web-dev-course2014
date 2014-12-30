package db;

import db.jdbc.UsersJDBCAdapter;
import db.redis.UsersRedisAdapter;
import models.User;

import java.util.List;

/**
 * Created by arielpollack on 12/30/14.
 */

public class UsersRepository {

    static private UsersRepository sharedInstance;
    static private UsersJDBCAdapter jdbsAdapter;
    static private UsersRedisAdapter redisAdapter;

    static {
        sharedInstance = new UsersRepository();
    }

    private UsersRepository() {
        jdbsAdapter = UsersJDBCAdapter.getInstance();
        redisAdapter = UsersRedisAdapter.getInstance();
    }

    static public User getById(String id) {
        User user = redisAdapter.getUserById(id);
        if (user == null) {
            user = jdbsAdapter.getUserById(id);
            redisAdapter.insert(user, null);
        }

        return user;
    }

    static public User getForIdNumberAndPassword(String idNumber, String password) {
        User user = redisAdapter.getUserByIdNumberAndPassword(idNumber, password);
        if (user == null) {
            user = jdbsAdapter.getUserByIdA(idNumber);
            redisAdapter.insert(user, null);
        }

        return user;
    }

    static public List<User> getByQuery(String query) {

    }

    static public Boolean insert(User user, String password) {

    }

    static public Boolean update(User user) {

    }

    static public Boolean delete(User user) {

    }

    public static User getByIdNumber(String idNumber) {

    }
}

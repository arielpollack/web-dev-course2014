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
    private UsersJDBCAdapter jdbsAdapter;
    private UsersRedisAdapter redisAdapter;

    static {
        sharedInstance = new UsersRepository();
    }

    private UsersRepository() {
        jdbsAdapter = UsersJDBCAdapter.getInstance();
        redisAdapter = UsersRedisAdapter.getInstance();
    }

    static public User getById(String id) {

    }

    static public User getForIdAndPassword(String id, String password) {

    }

    static public List<User> getByQuery(String query) {

    }

    static public Boolean insert(User user, String password) {

    }

    static public Boolean update(User user) {

    }

    static public Boolean delete(User user) {

    }

}

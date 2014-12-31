package db;

import db.jdbc.UsersJDBCAdapter;
import db.redis.UsersRedisAdapter;
import models.User;

import java.util.List;

/**
 * Created by arielpollack on 12/30/14.
 */

public class UsersRepository {


    static private UsersJDBCAdapter jdbsAdapter;
    static private UsersRedisAdapter redisAdapter;

    static {
        jdbsAdapter = UsersJDBCAdapter.getInstance();
        redisAdapter = UsersRedisAdapter.getInstance();
    }

    static public User getById(String id) {
        User user = redisAdapter.getUserById(id);
        if (user != null) {
            return user;
        }

        user = jdbsAdapter.getUserById(id);
        if (user != null) {
            redisAdapter.insert(user, null);
        }

        return user;
    }

    static public User getByIdNumber(String idNumber) {
        User user = redisAdapter.getUserByIdNumber(idNumber);
        if (user != null) {
            return user;
        }

        user = jdbsAdapter.getUser(idNumber);
        if (user != null) {
            redisAdapter.insert(user, null);
        }

        return user;
    }

    static public User getByIdNumberAndPassword(String idNumber, String password) {
        User user = redisAdapter.getUserByIdNumberAndPassword(idNumber, password);
        if (user != null) {
            return user;
        }

        user = jdbsAdapter.getUser(idNumber, password);
        if (user != null) {
            redisAdapter.insert(user, password);
        }

        return user;
    }

    static public List<User> getByQuery(String query) {
        List<User> users = jdbsAdapter.getByQuery(query);
        return users;
    }

    static public Boolean insert(User user, String password) {
        if (!jdbsAdapter.insert(user, password)) {
            return false;
        }

        try {
            redisAdapter.insert(user, password);
        } catch (Exception ex) {
            System.err.println("Exception inserting user to Redis: " + ex.getMessage());
        }

        return true;
    }

    static public Boolean update(User user) {
        if (!jdbsAdapter.update(user)) {
            return false;
        }

        try {
            redisAdapter.insert(user, null);
        } catch (Exception ex) {
            System.err.println("Exception inserting user to Redis: " + ex.getMessage());
        }

        return true;
    }

    static public Boolean delete(User user) {
        if (!jdbsAdapter.delete(user)) {
            return false;
        }

        try {
            redisAdapter.delete(user);
        } catch (Exception ex) {
            System.err.println("Exception deleting user from Redis: " + ex.getMessage());
        }

        return true;
    }


}

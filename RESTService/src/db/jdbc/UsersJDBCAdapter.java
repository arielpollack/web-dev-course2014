package db.jdbc;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import db.DBManager;
import db.Utils;
import models.*;

public class UsersJDBCAdapter extends BaseJDBCAdepter {
    static private UsersJDBCAdapter sharedInstance;

    static public final String TABLE_NAME = "tbl_user";

    static {
        sharedInstance = new UsersJDBCAdapter();
        sharedInstance.createTable();
        System.out.println("------ Users table created ------");
    }

    private UsersJDBCAdapter() {
        super();
    }

    public static UsersJDBCAdapter getInstance() {
        return sharedInstance;
    }

    @Override
    public void createTable() {
        try {
            Statement st = conn.createStatement();
            st.executeUpdate("create database if not exists " + DBManager.DB_NAME);
            st.executeUpdate("use " + DBManager.DB_NAME);

            String SQL = "create table if not exists " + TABLE_NAME + " ("
                    + "`id` int(11) not null auto_increment, "
                    + "`fname` varchar(15) not null, "
                    + "`lname` varchar(15) not null, "
                    + "`email` varchar(255) not null, "
                    + "`id_number` varchar(11) not null, "
                    + "`phone` varchar(11), "
                    + "`password` varchar(255) not null,"
                    + "primary key(`id`, `id_number`)"
                    + ") ENGINE = InnoDB CHARACTER SET utf8 COLLATE utf8_bin;";
            st.executeUpdate(SQL);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public Boolean insert(User user, String pwd) {
        String SQL = String.format("insert into `%s` (`fname`, `lname`, `id_number`, `password`, `email`, `phone`) values(?,?,?,?,?,?);", TABLE_NAME);
        try {
            PreparedStatement prSt = conn.prepareStatement(SQL);
            prSt.setString(1, user.getFirstName());
            prSt.setString(2, user.getLastName());
            prSt.setString(3, user.getIdNumber());
            prSt.setString(4, Utils.sha1(pwd));
            prSt.setString(5, user.getEmail());
            prSt.setString(6, user.getPhone());
            prSt.executeUpdate();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        } catch (NoSuchAlgorithmException ex) {
            System.err.println(ex.getMessage());
            return false;
        }

        return true;
    }

    public Boolean update(User user) {
        String SQL = String.format("update `%s` set `fname`=?, `lname`=?, `id_number`=?, `email`=?, `phone`=? where `id`=?;", TABLE_NAME);
        try {
            PreparedStatement prSt = conn.prepareStatement(SQL);
            prSt.setString(1, user.getFirstName());
            prSt.setString(2, user.getLastName());
            prSt.setString(3, user.getIdNumber());
            prSt.setString(4, user.getEmail());
            prSt.setString(5, user.getPhone());
            prSt.setString(6, user.getId());
            prSt.executeUpdate();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }

        return true;
    }

    public User getUser(String idNumber, String pwd) {
        String SQL = String.format("select * from `%s` where (`id_number` = ? AND `password` = ?);", TABLE_NAME);
        try {
            PreparedStatement prSt = conn.prepareStatement(SQL);
            prSt.setString(1, idNumber);
            prSt.setString(2, Utils.sha1(pwd));
            ResultSet st = prSt.executeQuery();
            if (st.next()) {
                return new User("", st);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            System.err.println(ex.getMessage());
        }

        return null;
    }

    public User getUser(String idNumber) {
        String SQL = String.format("select * from `%s` where (`id_number` = ?);", TABLE_NAME);
        try {
            PreparedStatement prSt = conn.prepareStatement(SQL);
            prSt.setString(1, idNumber);
            ResultSet st = prSt.executeQuery();
            if (st.next()) {
                return new User("", st);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return null;
    }

    /*
     * search users by name
     * @param name The required first OR last name
     */
    public List<User> searchUsers(String name) {
        List<User> users = new ArrayList<User>();

        String SQL = String.format("select * from `%s` where (`fname` like %%?%% or `lname` like %%?%%);", TABLE_NAME);
        try {
            PreparedStatement prSt = conn.prepareStatement(SQL);
            prSt.setString(1, name);
            prSt.setString(2, name);

            for (ResultSet st = prSt.executeQuery(); st.next(); ) {
                users.add(new User("", st));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return users;
    }
}

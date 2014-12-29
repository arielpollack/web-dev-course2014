package adapters;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.util.List;
import java.util.ArrayList;

import models.*;

public class AppointmentsJDBCAdapter extends BaseJDBCAdepter {
    static public final String TABLE_NAME = "tbl_appointment";

    public AppointmentsJDBCAdapter() {
        super();
    }

    @Override
    public void createTable() {
        try {
            Statement st = conn.createStatement();
            st.executeUpdate("create database if not exists " + DBManager.DB_NAME);
            st.executeUpdate("use " + DBManager.DB_NAME);

            String SQL = "create table if not exists " + TABLE_NAME + " ("
                    + "`id` int(11) not null auto_increment, "
                    + "`date` datetime not null, "
                    + "`user_id` int(11) not null, "
                    + "`therapist_id` int(11) not null, "
                    + "primary key(`id`)"
                    + ") ENGINE = InnoDB CHARACTER SET utf8 COLLATE utf8_bin;";
            st.executeUpdate(SQL);

            // user and therapist constraints
            String constraints = "alter table `" + TABLE_NAME + "` "
                    + "add constraint `fk_user` foreign key(`user_id`) references `" + UsersJDBCAdapter.TABLE_NAME + "`(`id`) on delete cascade, "
                    + "add constraint `fk_therapist` foreign key(`therapist_id`) references `" + UsersJDBCAdapter.TABLE_NAME + "`(`id`) on delete cascade;";
            st.executeUpdate(constraints);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void insert(User user, User therapist, Date date) {
        String SQL = "insert into `" + TABLE_NAME + "` (`date`, `user_id`, `therapist_id`) values(?,?,?);";
        try {
            PreparedStatement prSt = conn.prepareStatement(SQL);
            prSt.setDate(1, date);
            prSt.setString(2, user.getId());
            prSt.setString(3, therapist.getId());
            prSt.executeUpdate();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public List<Appointment> getAppointments(User user) {
        List<Appointment> appointments = new ArrayList<Appointment>();

        String SQL = "select a.*, u.id as u_id, u.fname as u_fname, u.lname as u_lname, u.email as u_email, u.phone as u_phone, u.id_number as u_id_number, "
                + "t.id as t_id, t.fname as t_fname, t.lname as t_lname, t.email as t_email, t.phone as t_phone, t.id_number as t_id_number "
                + "from `" + TABLE_NAME + "` a "
                + "left join `" + UsersJDBCAdapter.TABLE_NAME + "` u "
                + "on u.id = a.user_id "
                + "left join `" + UsersJDBCAdapter.TABLE_NAME + "` t "
                + "on t.id = a.therapist_id "
                + "where (`user_id` = ? and `date` > NOW());";

        System.out.println("SQL: " + SQL);
        try {
            PreparedStatement prSt = conn.prepareStatement(SQL);
            prSt.setString(1, user.getId());
            for (ResultSet st = prSt.executeQuery(); st.next(); ) {
                appointments.add(new Appointment("", st));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return appointments;
    }
}
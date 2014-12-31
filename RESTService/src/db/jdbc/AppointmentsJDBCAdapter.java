package db.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.ArrayList;

import db.DBManager;
import models.*;

public class AppointmentsJDBCAdapter extends BaseJDBCAdepter {
    static private AppointmentsJDBCAdapter sharedInstance;

    static public final String TABLE_NAME = "tbl_appointment";

    static {
        sharedInstance = new AppointmentsJDBCAdapter();

        sharedInstance.createTable();
        System.out.println("------ Appointments table created ------");
    }

    private AppointmentsJDBCAdapter() {
        super();
    }

    public static AppointmentsJDBCAdapter getInstance() {
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
                    + "`date` timestamp not null, "
                    + "`user_id` int(11) not null, "
                    + "`therapist_id` int(11) not null default 1, "
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

    public Appointment insert(Appointment appointment) {
        String SQL = String.format("insert into `%s` (`date`, `user_id`, `therapist_id`) values(?,?,?);", TABLE_NAME);
        try {
            User therapist = appointment.getTherapist();
            User user = appointment.getUser();
            PreparedStatement prSt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            prSt.setTimestamp(1, new java.sql.Timestamp(appointment.getDate().getTime()));
            prSt.setString(2, user != null ? user.getId() : null);
            prSt.setString(3, therapist != null ? therapist.getId() : "1");
            prSt.executeUpdate();
            ResultSet rs = prSt.getGeneratedKeys();
            if (rs != null && rs.next()) {
                return getById(rs.getInt(1));
            }

            return null;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return null;
    }

    public Appointment getById(Integer id) {
        String SQL = String.format("select a.*, u.id as u_id, u.fname as u_fname, u.lname as u_lname, u.email as u_email, u.phone as u_phone, u.id_number as u_id_number, u.is_admin as u_is_admin, t.is_admin as t_is_admin, t.id as t_id, t.fname as t_fname, t.lname as t_lname, t.email as t_email, t.phone as t_phone, t.id_number as t_id_number from `%s` as a left join `%s` as u on u.id = a.user_id left join `%s` as t on t.id = a.therapist_id where a.id = %d;", TABLE_NAME, UsersJDBCAdapter.TABLE_NAME, UsersJDBCAdapter.TABLE_NAME, id);

        System.out.println("SQL: " + SQL);
        try {
            PreparedStatement prSt = conn.prepareStatement(SQL);
            ResultSet rs = prSt.executeQuery();
            if (rs.next()) {
                return new Appointment("", rs);
            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return null;
    }

    public List<Appointment> getBetweenDates(long start, long end) {
        List<Appointment> appointments = new ArrayList<Appointment>();
        String SQL = String.format("select a.*, u.id as u_id, u.fname as u_fname, u.lname as u_lname, u.email as u_email, u.phone as u_phone, u.id_number as u_id_number, u.is_admin as u_is_admin, t.is_admin as t_is_admin, t.id as t_id, t.fname as t_fname, t.lname as t_lname, t.email as t_email, t.phone as t_phone, t.id_number as t_id_number from `%s` as a left join `%s` as u on u.id = a.user_id left join `%s` as t on t.id = a.therapist_id where `date` between ? and ? group by a.id order by `date`;", TABLE_NAME, UsersJDBCAdapter.TABLE_NAME, UsersJDBCAdapter.TABLE_NAME);

        System.out.println("SQL: " + SQL);
        try {
            PreparedStatement prSt = conn.prepareStatement(SQL);
            prSt.setTimestamp(1, new java.sql.Timestamp(start));
            prSt.setTimestamp(2, new java.sql.Timestamp(end));
            for (ResultSet st = prSt.executeQuery(); st.next(); ) {
                appointments.add(new Appointment("", st));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return appointments;
    }

    public List<Appointment> getForUser(User user, long start, long end) {
        List<Appointment> appointments = new ArrayList<Appointment>();
        String SQL = String.format("select a.*, u.id as u_id, u.fname as u_fname, u.lname as u_lname, u.email as u_email, u.phone as u_phone, u.id_number as u_id_number, u.is_admin as u_is_admin, t.is_admin as t_is_admin, t.id as t_id, t.fname as t_fname, t.lname as t_lname, t.email as t_email, t.phone as t_phone, t.id_number as t_id_number from `%s` as a left join `%s` as u on u.id = a.user_id left join `%s` as t on t.id = a.therapist_id where `user_id` = %s", TABLE_NAME, UsersJDBCAdapter.TABLE_NAME, UsersJDBCAdapter.TABLE_NAME, user.getId());
        if (start > 0) {
            SQL += " and `date` >= ?";
        }
        if (end > 0) {
            SQL += " and `date` <= ?";
        }
        SQL += " group by a.id order by `date`;";

        System.out.println("SQL: " + SQL);
        try {
            PreparedStatement prSt = conn.prepareStatement(SQL);
            if (start > 0) {
                prSt.setTimestamp(1, new java.sql.Timestamp(start));
                if (end > 0) {
                    prSt.setTimestamp(2, new java.sql.Timestamp(end));
                }
            } else if (end > 0) {
                prSt.setTimestamp(1, new java.sql.Timestamp(end));
            }

            for (ResultSet st = prSt.executeQuery(); st.next(); ) {
                appointments.add(new Appointment("", st));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return appointments;
    }

    public Boolean update(Appointment appointment) {

        String SQL = String.format("update `%s` set `date` = ?, `therapist_id` = %s where `id` = %d;", TABLE_NAME, appointment.getTherapist().getId(), appointment.getId());
        try {
            PreparedStatement st = conn.prepareStatement(SQL);
            st.setTimestamp(1, new java.sql.Timestamp(appointment.getDate().getTime()));
            st.executeUpdate();

            return true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return false;
    }

    public Boolean delete(String appointmentId) {
        String SQL = "delete from `" + TABLE_NAME + "` where `id` = " + appointmentId;
        try {
            Statement st = conn.prepareStatement(SQL);
            st.executeUpdate(SQL);

            return true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return false;
    }
}
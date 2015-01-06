package api;

import com.owlike.genson.Genson;
import db.AppointmentsRepository;
import db.UsersRepository;
import exceptions.InvalidParameterException;
import exceptions.UserNotFoundException;
import models.Appointment;
import models.JSONResponse;
import models.TimeBlock;
import models.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;

@Path("admin")
public class admin {

    //=========================================================================================
    // Users
    //=========================================================================================
    @GET
    @Path("user/{user_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse getUserById(@PathParam("user_id") String userId) {
        try {
            User user = UsersRepository.getById(userId);
            if (user == null) {
                throw new UserNotFoundException();
            }

            return JSONResponse.success(user);
        } catch (Exception ex) {
            System.err.println(ex);
            return JSONResponse.error(ex.getMessage());
        }
    }

    @GET
    @Path("user")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse getUserByQuery(@QueryParam("query") String query,
                                       @QueryParam("id_number") String idNumber) {
        try {
            if (query != null && query.length() != 0) {
                List<User> users = UsersRepository.getByQuery(query);
                return JSONResponse.success(users);
            }

            if (idNumber != null && idNumber.length() != 0) {
                User user = UsersRepository.getByIdNumber(idNumber);
                if (user == null) {
                    throw new UserNotFoundException();
                }

                return JSONResponse.success(user);
            }

            throw new InvalidParameterException();
        } catch (Exception ex) {
            System.err.println(ex);
            return JSONResponse.error(ex.getMessage());
        }
    }

    @POST
    @Path("user")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse createUser(HashMap<String, Object> obj) {
        try {
            Genson genson = new Genson();
            HashMap<String, String> userObject = (HashMap<String, String>)obj.get("user");
            String json = genson.serialize(userObject);
            User user = genson.deserialize(json, User.class);
            String password = (String) obj.get("password");

            if (UsersRepository.insert(user, password)) {
                return JSONResponse.success(user).code(HttpServletResponse.SC_CREATED);
            }

            return JSONResponse.error("Insertion error");
        } catch (Exception ex) {
            System.err.println(ex);
            return JSONResponse.error("Exception: " + ex.getMessage());
        }
    }

    //=========================================================================================
    // Appointments
    //=========================================================================================
    @GET @Path("appointment/available")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse getFreeTimes(@Context HttpServletRequest request, @QueryParam("start") Long start, @QueryParam("end") Long end) {
        try {
            User user = (User)request.getSession().getAttribute("user");
            if (user == null) {
                return JSONResponse.noCredentials();
            }

            List<TimeBlock> timeBlocks = AppointmentsRepository.getFreeTimeBlocks(start, end);

            return JSONResponse.success(timeBlocks);
        } catch (Exception ex) {
            System.err.println(ex);
            return JSONResponse.error(ex.getMessage());
        }
    }

    @GET
    @Path("appointment")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse getBetweenDates(@QueryParam("start") Long start, @QueryParam("end") Long end) {
        try {
            List<Appointment> appointments = AppointmentsRepository.getBetweenDates(start != null ? start : 0, end != null ? end : 0);

            return JSONResponse.success(appointments);
        } catch (Exception ex) {
            System.err.println(ex);
            return JSONResponse.error(ex.getMessage());
        }
    }

    @GET
    @Path("appointment/user/{user_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse getUserAppointments(@PathParam("user_id") String userId, @QueryParam("start") Long start, @QueryParam("end") Long end) {
        try {
            User user = UsersRepository.getById(userId);
            if (user == null) {
                throw new Exception("User not found");
            }

            List<Appointment> appointments = AppointmentsRepository.getForUser(user, start != null ? start : 0, end != null ? end : 0);

            return JSONResponse.success(appointments);
        } catch (Exception ex) {
            System.err.println(ex);
            return JSONResponse.error(ex.getMessage());
        }
    }

    @POST
    @Path("appointment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse createAppointment(Appointment appointment) {
        try {
            Appointment newAppointment;
            if ((newAppointment = AppointmentsRepository.insert(appointment)) == null) {
                return JSONResponse.error("Insertion error");
            }

            return JSONResponse.success(newAppointment).code(HttpServletResponse.SC_CREATED);
        } catch (Exception ex) {
            System.err.println(ex);
            return JSONResponse.error(ex.getMessage());
        }
    }

    @PUT
    @Path("appointment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse updateAppointment(Appointment appointment) {
        try {
            if (!AppointmentsRepository.update(appointment)) {
                return JSONResponse.error("Update error");
            }

            return JSONResponse.success(null).code(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception ex) {
            System.err.println(ex);
            return JSONResponse.error(ex.getMessage());
        }
    }

    @DELETE
    @Path("appointment/{appointment_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse deleteAppointment(@PathParam("appointment_id") String appointmentId) {
        try {
            if (!AppointmentsRepository.delete(appointmentId)) {
                return JSONResponse.error("Delete error");
            }

            return JSONResponse.success(null).code(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception ex) {
            System.err.println(ex);
            ex.printStackTrace();
            return JSONResponse.error(ex.getMessage());
        }
    }
}

import com.owlike.genson.Genson;
import db.AppointmentsRepository;
import db.UsersRepository;
import db.jdbc.AppointmentsJDBCAdapter;
import db.jdbc.UsersJDBCAdapter;
import models.Appointment;
import models.JSONResponse;
import models.User;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Path("admin")
public class admin {
    UsersJDBCAdapter usersAdapter = null;
    AppointmentsJDBCAdapter appointmentsAdapter = null;

    //=========================================================================================
    // Users
    //=========================================================================================
    @GET
    @Path("user/{user_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse getUserById(@PathParam("user_id") String userId) {

    }

    @GET
    @Path("user")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse getUserByQuery(@QueryParam("query") String query,
                                       @QueryParam("id_number") String idNumber) {

    }

    @POST
    @Path("user")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse createUser(HashMap<String, Object> obj) {
        try {
            HashMap<String, String> userObject = (HashMap<String, String>) obj.get("user");
            User user = new Genson().deserialize(new JSONObject(userObject).toString(), User.class);
            String password = (String) obj.get("password");

            if (UsersRepository.insert(user, password)) {
                return JSONResponse.success(user);
            }

            return JSONResponse.error("SQL insertion failed");
        } catch (Exception exception) {
            System.err.println("Exception: " + exception.getMessage() + "\n for object: " + obj);
            return JSONResponse.error("Exception: " + exception.getMessage());
        }
    }

    //=========================================================================================
    // Appointments
    //=========================================================================================
    @GET
    @Path("appointment")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse getAllAppointments() {
        try {
            List<Appointment> appointments = AppointmentsRepository.getForDate(Calendar.getInstance().getTime());

            return JSONResponse.success(appointments);
        } catch (Exception ex) {
            return JSONResponse.error(ex.getMessage());
        }
    }

    @GET
    @Path("appointment/user/{user_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse getUserAppointments(@PathParam("user_id") String userId) {
        try {
            User user = UsersRepository.getById(userId);
            if (user == null) {
                throw new Exception("User not found");
            }

            List<Appointment> appointments = AppointmentsRepository.getForUserAndDate(user, Calendar.getInstance().getTime());

            return JSONResponse.success(appointments);
        } catch (Exception ex) {
            return JSONResponse.error(ex.getMessage());
        }
    }

    @GET
    @Path("appointment/date/{date_in_millies}")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse getDateAppointments(@PathParam("date_in_millies") String dateMillies) {
        try {
            List<Appointment> appointments = AppointmentsRepository.getForDate(new Date(Integer.parseInt(dateMillies)));

            return JSONResponse.success(appointments);
        } catch (Exception ex) {
            return JSONResponse.error(ex.getMessage());
        }
    }

    @POST
    @Path("appointment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse createAppointment(Appointment appointment) {
        try {
            if (!AppointmentsRepository.insert(appointment)) {
                throw new Exception()
            }

            return JSONResponse.success(appointments);
        } catch (Exception ex) {
            return JSONResponse.error(ex.getMessage());
        }
    }

    @PUT
    @Path("appointment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse updateAppointment(Appointment appointment) {

    }

    @DELETE
    @Path("appointment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse deleteAppointment(Appointment appointment) {

    }
}

import db.UsersRepository;
import db.jdbc.AppointmentsJDBCAdapter;
import db.jdbc.UsersJDBCAdapter;
import db.redis.UsersRedisAdapter;
import com.owlike.genson.Genson;
import models.Appointment;
import models.JSONResponse;
import models.User;
import org.codehaus.jettison.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;

@Path("users")
public class users {

    //=========================================================================================
    // Auth
    //=========================================================================================
    @POST @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public JSONResponse postLogin(@Context HttpServletRequest request, HashMap<String, String> login) {
        User user = UsersRepository.getInstance().getForIdAndPassword(login.get("id_number"), login.get("password"));
        if (user != null) {
            request.getSession().setAttribute("user", user);

            List<Appointment> appointments = Appo.getAppointments(user);

            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("appointments", appointments);
            data.put("user", user);
            return JSONResponse.success(data);
        } else {
            return JSONResponse.error("ID or password doesn't match");
        }
    }

    //=========================================================================================
    // Profile
    //=========================================================================================
    @PUT @Path("update")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public JSONResponse updateUser(@Context HttpServletRequest request, User user) {
        UsersJDBCAdapter usersAdapter = new UsersJDBCAdapter();

        HttpSession session = request.getSession();
        if (session == null) {
            return JSONResponse.noCredentials();
        }

        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !currentUser.getId().equals(user.getId())) {
            return JSONResponse.noCredentials();
        }

        if (usersAdapter.update(user)) {
            if (new UsersRedisAdapter().insert(user, null)) {
                System.out.println("User " + user.getId() + " was updated on Redis");
            } else {
                System.err.println("User " + user.getId() + " was updated on Redis");
            }

            return JSONResponse.success(user);
        }

        return JSONResponse.error("SQL update error");
    }

    //=========================================================================================
    // Appointments
    //=========================================================================================
    @GET @Path("appointment")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse getAllAppointments() {

    }

    @GET @Path("appointment/date/{date_in_millies}")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse getDateAppointments(@PathParam("date_in_millies") String dateMillies) {

    }

    @POST @Path("appointment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse createAppointment(Appointment appointment) {

    }

    @PUT @Path("appointment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse updateAppointment(Appointment appointment) {

    }
}

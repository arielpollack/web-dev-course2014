import adapters.AppointmentsJDBCAdapter;
import adapters.UsersJDBCAdapter;
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
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public JSONResponse postLogin(@Context HttpServletRequest request, HashMap<String, String> login) {
        UsersJDBCAdapter usersAdapter = new UsersJDBCAdapter();

        User user = usersAdapter.getUser(login.get("id_number"), login.get("password"));
        if (user != null) {
            request.getSession().setAttribute("user", user);

            List<Appointment> appointments = new AppointmentsJDBCAdapter().getAppointments(user);

            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("appointments", appointments);
            data.put("user", user);
            return JSONResponse.success(data);
        } else {
            return JSONResponse.error("ID or password doesn't match");
        }
    }

    @PUT
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
            return JSONResponse.success(user);
        }

        return JSONResponse.error("SQL update error");
    }

    @POST
    @Path("signup")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse postSignup(@Context HttpServletRequest request, HashMap<String, Object> obj) {
        try {
            User user = new Genson().deserialize(new JSONObject((HashMap<String, String>) obj.get("user")).toString(), User.class);
            String password = (String) obj.get("password");

            if (new UsersJDBCAdapter().insert(user, password)) {
                request.getSession().setAttribute("user", user);
                return JSONResponse.success(user);
            }

            return JSONResponse.error("SQL insertion failed");
        } catch (Exception exception) {
            System.out.println(obj);
            return JSONResponse.error("Exception: " + exception.getMessage());
        }
    }
}

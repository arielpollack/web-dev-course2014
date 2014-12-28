import adapters.AppointmentsRedisAdapter;
import adapters.UsersRedisAdapter;
import models.Appointment;
import models.JSONResponse;
import models.User;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by arielpollack on 12/26/14.
 */

@Path("appointments")
public class appointments
{
    @GET
    @Path("today")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse getToday()
    {
        try {
            List<Appointment> appointments = new AppointmentsRedisAdapter().getToday();

            return JSONResponse.success(appointments);
        }
        catch (Exception ex) {
            return JSONResponse.error("Exception: " + ex.getMessage());
        }
    }

    @GET
    @Path("{user_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse getUserAppointments(@PathParam("user_id") String userId)
    {
        try {
            User user = new UsersRedisAdapter().getUserWithID(userId);
            if (user == null) {
                return JSONResponse.error("User not exist");
            }

            List<Appointment> appointments = new AppointmentsRedisAdapter().getForUser(user, null, null);

            return JSONResponse.success(appointments);
        }
        catch (Exception ex) {
            return JSONResponse.error("Exception: " + ex.getMessage());
        }
    }
}

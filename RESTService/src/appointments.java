import adapters.AppointmentsRedisAdapter;
import models.Appointment;
import models.JSONResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
}

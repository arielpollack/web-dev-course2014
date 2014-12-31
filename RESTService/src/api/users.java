package api;

import db.AppointmentsRepository;
import db.UsersRepository;
import exceptions.NoCredentialsException;
import models.Appointment;
import models.JSONResponse;
import models.TimeBlock;
import models.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.Date;
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
        try {
            User user = UsersRepository.getByIdNumberAndPassword(login.get("id_number"), login.get("password"));
            if (user != null) {
                request.getSession().setAttribute("user", user);

                List<Appointment> appointments = AppointmentsRepository.getForUser(user, 0, 0);

                HashMap<String, Object> data = new HashMap<String, Object>();
                data.put("appointments", appointments);
                data.put("user", user);
                return JSONResponse.success(data);
            } else {
                throw new NoCredentialsException();
            }
        } catch (Exception ex) {
            System.err.println(ex);
            ex.printStackTrace();
            return JSONResponse.error(ex.getMessage());
        }
    }

    //=========================================================================================
    // Profile
    //=========================================================================================
    @PUT @Path("update")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public JSONResponse updateUser(@Context HttpServletRequest request, User user) {
        try {
            User currentUser = checkCredentials(request);
            if (!currentUser.getId().equals(user.getId())) {
                throw new NoCredentialsException();
            }

            if (!UsersRepository.update(user)) {
                return JSONResponse.error("Update error");
            }

            return JSONResponse.success(user);
        } catch (Exception ex) {
            System.err.println(ex);
            return JSONResponse.error(ex.getMessage());
        }
    }

    //=========================================================================================
    // Appointments
    //=========================================================================================
    @GET @Path("appointment/available")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse getFreeTimes(@Context HttpServletRequest request) {
        try {
            User user = (User)request.getSession().getAttribute("user");
            if (user == null) {
                return JSONResponse.noCredentials();
            }

            List<TimeBlock> timeBlocks = AppointmentsRepository.getFreeTimeBlocks();

            return JSONResponse.success(timeBlocks);
        } catch (Exception ex) {
            System.err.println(ex);
            return JSONResponse.error(ex.getMessage());
        }
    }

    @GET @Path("appointment")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse getAllAppointments(@Context HttpServletRequest request, @QueryParam("start") Long start, @QueryParam("end") Long end) {
        try {
            User user = checkCredentials(request);
            List<Appointment> appointments = AppointmentsRepository.getForUser(user, start != null ? start : 0, end != null ? end : 0);

            return JSONResponse.success(appointments);
        } catch (Exception ex) {
            System.err.println(ex);
            return JSONResponse.error(ex.getMessage());
        }
    }

    @POST @Path("appointment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse createAppointment(@Context HttpServletRequest request, Appointment appointment) {
        try {
            User user = checkCredentials(request);
            appointment.setUser(user); // appointment is received only with date, the user can't choose the therapist
            if (appointment.getTherapist() == null) {
                appointment.setTherapist(UsersRepository.getById("3")); // set the temporary therapist
            }

            Appointment newAppointment;
            if ((newAppointment = AppointmentsRepository.insert(appointment)) == null) {
                return JSONResponse.error("Insert error");
            }

            return JSONResponse.success(newAppointment);
        } catch (Exception ex) {
            System.err.println(ex);
            ex.printStackTrace();
            return JSONResponse.error(ex.getMessage());
        }
    }

    @PUT @Path("appointment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JSONResponse updateAppointment(@Context HttpServletRequest request, Appointment appointment) {
        try {
            User user = checkCredentials(request);
            appointment.setUser(user); // appointment is received only with date, the user can't choose the therapist
            if (!AppointmentsRepository.update(appointment)) {
                return JSONResponse.error("Update error");
            }

            return JSONResponse.success(appointment);
        } catch (Exception ex) {
            System.err.println(ex);
            return JSONResponse.error(ex.getMessage());
        }
    }

    private User checkCredentials(HttpServletRequest request) throws NoCredentialsException {
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("user");
        if (user == null) {
            throw new NoCredentialsException();
        }

        return user;
    }
}

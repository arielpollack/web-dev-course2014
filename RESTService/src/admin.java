

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import adapters.AppointmentsJDBCAdapter;
import adapters.UsersJDBCAdapter;

@Path("admin")
public class admin 
{
	UsersJDBCAdapter usersAdapter = null;
	AppointmentsJDBCAdapter appointmentsAdapter = null;
	
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getInit(@QueryParam("create_tables") String createTables)
    {
    	if (createTables != null)
    	{
	    	usersAdapter = new UsersJDBCAdapter();
	    	usersAdapter.createTable();
	    	System.out.println("------ Users table created ------");
	    	
	    	appointmentsAdapter = new AppointmentsJDBCAdapter();
	    	appointmentsAdapter.createTable();
	    	System.out.println("------ Appointments table created ------");
    	}
    	
    	return "OK";
    }
}

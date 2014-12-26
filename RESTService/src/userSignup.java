import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import adapters.UsersJDBCAdapter;
import com.owlike.genson.Genson;
import models.JSONResponse;
import models.User;
import org.codehaus.jettison.json.JSONObject;

import java.util.HashMap;

@Path("signup")
public class userSignup {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONResponse postSignup(@Context HttpServletRequest request, HashMap<String, Object> obj)
	{
		try
		{
			User user = new Genson().deserialize(new JSONObject((HashMap<String, String>)obj.get("user")).toString(), User.class);
			String password = (String)obj.get("password");

			if (new UsersJDBCAdapter().insert(user, password))
			{
				request.getSession().setAttribute("user", user);
				return JSONResponse.success(user);
			}

			return JSONResponse.error("SQL insertion failed");
		}
		catch (Exception exception)
		{
			System.out.println(obj);
			return JSONResponse.error("Exception: " + exception.getMessage());
		}
	}
}

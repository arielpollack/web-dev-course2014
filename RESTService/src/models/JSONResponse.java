package models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JSONResponse 
{
	String status, error;
	
	@XmlElement
	Object data;
	
	public JSONResponse() {}

	static public JSONResponse success(Object data)
	{
		return new JSONResponse("success", null, data);
	}

	static public JSONResponse error(String message)
	{
		return new JSONResponse("error", message, null);
	}

	static public JSONResponse noCredentials()
	{
		return JSONResponse.error("No credentials");
	}
	
	public JSONResponse(String status, String error, Object data)
	{
		this.status = status;
		this.error = error;
		this.data = data;
	}
}

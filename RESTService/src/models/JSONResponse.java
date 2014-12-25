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
	
	public JSONResponse(String status, String error, Object data)
	{
		this.status = status;
		this.error = error;
		this.data = data;
	}
}

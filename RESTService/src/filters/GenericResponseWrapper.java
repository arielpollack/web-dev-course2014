package filters;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class GenericResponseWrapper extends HttpServletResponseWrapper
{
	private ByteArrayOutputStream output;
	private int contentLength;
	private String contentType;
	private int status;

	public GenericResponseWrapper(HttpServletResponse response) 
	{ 
		super(response);
		output=new ByteArrayOutputStream();
	} 

	public byte[] getData() { 
		return output.toByteArray(); 
	} 

	public ServletOutputStream getOutputStream() { 
		return new FilterServletOutputStream(output); 
	} 

	public PrintWriter getWriter() { 
		return new PrintWriter(getOutputStream(),true); 
	} 

	public void setContentLength(int length) { 
		this.contentLength = length;
		super.setContentLength(length); 
	} 

	public int getContentLength() { 
		return contentLength; 
	} 

	public void setContentType(String type) { 
		this.contentType = type;
		super.setContentType(type); 
	} 

	public String getContentType() { 
		return contentType; 
	} 
	
	public void setStatus(int st)
	{
		this.status = st;
		super.setStatus(st);
	}
	
	public int getStatus()
	{
		return status;
	}

	@Override
	public String toString() {
		return output.toString();
	}
}

class FilterServletOutputStream extends ServletOutputStream 
{

	private DataOutputStream stream; 

	public FilterServletOutputStream(OutputStream output) { 
		stream = new DataOutputStream(output); 
	}

	public void write(int b) throws IOException  { 
		stream.write(b); 
	}

	public void write(byte[] b) throws IOException  { 
		stream.write(b); 
	}

	public void write(byte[] b, int off, int len) throws IOException  { 
		stream.write(b,off,len); 
	} 

}
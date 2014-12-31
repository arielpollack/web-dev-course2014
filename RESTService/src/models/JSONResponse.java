package models;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JSONResponse {
    String status;
    String error;

    int code;

    @XmlElement
    Object data;

    public JSONResponse() {
    }

    static public JSONResponse success(Object data) {
        return new JSONResponse("success", HttpServletResponse.SC_OK, null, data);
    }

    static public JSONResponse error(String message) {
        return new JSONResponse("error", HttpServletResponse.SC_BAD_REQUEST, message, null);
    }

    static public JSONResponse noCredentials() {
        return JSONResponse.error("No credentials").code(HttpServletResponse.SC_UNAUTHORIZED);
    }

    public JSONResponse(String status, int code, String error, Object data) {
        this.status = status;
        this.error = error;
        this.data = data;
        this.code = code;
    }

    public JSONResponse code(int code) {
        this.code = code;
        return this;
    }

    public int getCode() {
        return code;
    }
}

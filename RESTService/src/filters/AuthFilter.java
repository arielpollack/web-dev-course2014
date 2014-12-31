package filters;

import com.owlike.genson.Genson;
import filters.GenericResponseWrapper;
import models.JSONResponse;
import models.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Servlet Filter implementation class filters.AuthFilter
 */
@WebFilter("/filters.AuthFilter")
public class AuthFilter implements Filter {

    /**
     * Default constructor.
     */
    public AuthFilter() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @see Filter#destroy()
     */
    public void destroy() {
        // TODO Auto-generated method stub
    }

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        GenericResponseWrapper wrapper = new GenericResponseWrapper(res);
        OutputStream out = res.getOutputStream();

        String URI = req.getRequestURI();

        long t1 = System.currentTimeMillis();
        chain.doFilter(request, wrapper);
        long t2 = System.currentTimeMillis();

        HttpSession session = req.getSession(false);

        try {
            JSONResponse responseBody = new Genson().deserialize(wrapper.toString(), JSONResponse.class);
            wrapper.setStatus(responseBody.getCode());
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }

        if (session == null) {
            wrapper.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                wrapper.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }

        System.out.println(req.getMethod() + " " + URI);
        System.out.println("  STATUS " + wrapper.getStatus());
        System.out.println("  TIME " + (t2 - t1) + " ms");

        out.write(wrapper.getData());
    }

    /**
     * @see Filter#init(FilterConfig)
     */
    public void init(FilterConfig fConfig) throws ServletException {
        // TODO Auto-generated method stub
    }

}

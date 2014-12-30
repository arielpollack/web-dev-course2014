import filters.GenericResponseWrapper;
import models.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by arielpollack on 12/29/14.
 */
@WebFilter("/AdminFilter")
public class AdminFilter implements Filter {

    public void init(FilterConfig fConfig) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpSession session = ((HttpServletRequest)request).getSession();
        User user;
        if (session == null || (user = (User)session.getAttribute("user")) == null || !user.getIsAdmin()) {
            GenericResponseWrapper wrapper = new GenericResponseWrapper((HttpServletResponse)response);
            wrapper.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            OutputStream out = response.getOutputStream();
            out.write(wrapper.getData());
            System.out.println("An attempt to access admin action without permission");
            Logger.getLogger("Admin").log(Level.WARNING, "An attempt to access admin action without permission");
            return;
        }

        chain.doFilter(request, response);
    }

    public void destroy() {

    }
}

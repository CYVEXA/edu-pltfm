package com.cyvexa.config;

import com.cyvexa.model.User;
import com.cyvexa.service.SessionService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class SessionFilter implements Filter {

    @Autowired
    private SessionService sessionService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getRequestURI();
        String method = req.getMethod();

        // Allow all non-API paths (static files: HTML, CSS, JS, images, etc.)
        if (!path.startsWith("/api/")) {
            chain.doFilter(request, response);
            return;
        }

        // Public API paths - no authentication required
        boolean isPublic = path.startsWith("/api/auth/")
            || (path.startsWith("/api/courses") && "GET".equals(method))
            || (path.startsWith("/api/uploads/") && "GET".equals(method))
            || (path.startsWith("/api/enrollments/course/") && "GET".equals(method));

        if (isPublic) {
            chain.doFilter(request, response);
            return;
        }

        String token = req.getHeader("X-Session-Token");
        if (token == null || !sessionService.isValid(token)) {
            res.setStatus(401);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\":\"Authentication required\"}");
            return;
        }

        User user = sessionService.getUser(token);
        boolean isAdminOp = (path.startsWith("/api/courses") && !"GET".equals(method))
            || (path.startsWith("/api/uploads/") && !"GET".equals(method));

        if (isAdminOp && !"ADMIN".equals(user.getRole())) {
            res.setStatus(403);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\":\"Admin access required\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}

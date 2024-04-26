package com.example.hodlhub.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;
import java.io.PrintWriter;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json");

        if (authentication != null && authentication.isAuthenticated()) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String jsonResponse = "{\"status\": \"error\", " +
                    "\"message\": \"Logout failed\"}";
            PrintWriter out = response.getWriter();
            out.print(jsonResponse);
            out.flush();
        } else {
            response.setStatus(HttpServletResponse.SC_OK); // OK
            String jsonResponse = "{\"status\": \"success\", " +
                    "\"message\": \"Logout successful\"}";
            PrintWriter out = response.getWriter();
            out.print(jsonResponse);
            out.flush();
        }
    }

}

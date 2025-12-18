package com.codingrecipe.member.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Disabled LoginCheckInterceptor as Spring Security now handles authentication
        // registry.addInterceptor(new LoginCheckInterceptor()) ...
    }

    // Inner class or separate file for Interceptor
    public static class LoginCheckInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("loginEmail") == null) {
                // Not logged in -> Redirect to login page
                response.sendRedirect("/member/login");
                return false;
            }
            return true;
        }
    }
}

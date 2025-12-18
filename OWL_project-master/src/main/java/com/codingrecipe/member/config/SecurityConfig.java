package com.codingrecipe.member.config;

import com.codingrecipe.member.config.auth.PrincipalDetails;
import com.codingrecipe.member.config.auth.PrincipalOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PrincipalOAuth2UserService principalOAuth2UserService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Using NoOp for plain text compatibility as per current DB state
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable(); // Disable CSRF for simplicity
        http.headers().frameOptions().disable(); // Allow H2 console and iframes

        http.authorizeRequests()
                // Public Paths
                .antMatchers("/", "/css/**", "/images/**", "/js/**", "/member/save", "/member/login", "/member/email-check", "/mail/**", "/member/save").permitAll()
                // Admin Path
                .antMatchers("/admin/**").hasRole("ADMIN")
                // Protected Paths (Secure Everything Else)
                .anyRequest().authenticated()
                .and()
                // Form Login
                .formLogin()
                .loginPage("/member/login") // Custom Login Page
                .loginProcessingUrl("/member/login") // POST URL to intercept
                .usernameParameter("memberEmail") // Field name in form
                .passwordParameter("memberPassword") // Field name in form
                .successHandler(new CustomSuccessHandler()); // Populate Session
//                .and()
//                // OAuth2 Login
//                .oauth2Login()
//                .loginPage("/member/login")
//                .userInfoEndpoint()
//                .userService(principalOAuth2UserService) // Custom User Service
//                .and()
//                .successHandler(new CustomSuccessHandler()); // Populate Session
    }

    // Custom Handler to maintain session compatibility with legacy code
    public static class CustomSuccessHandler implements AuthenticationSuccessHandler {
        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            HttpSession session = request.getSession();
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            
            // Set Legacy Session Attributes
            session.setAttribute("loginEmail", principalDetails.getMemberEntity().getMemberEmail());
            session.setAttribute("loginName", principalDetails.getMemberEntity().getMemberName());

            response.sendRedirect("/"); // Redirect to Home
        }
    }
}

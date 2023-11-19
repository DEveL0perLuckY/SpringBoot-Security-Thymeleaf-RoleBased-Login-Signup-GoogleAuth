package com.example.DevLucky.demo.Security;

import com.example.DevLucky.demo.Entity.Role;
import com.example.DevLucky.demo.Entity.User;
import com.example.DevLucky.demo.Repo.RoleRepository;
import com.example.DevLucky.demo.Repo.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class GoogleAuth2SuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        String email = token.getPrincipal().getAttributes().get("email").toString();
        if (userRepository.findByEmail(email).isEmpty()) {
            User temp = new User();
            temp.setEmail(email);
            temp.setUserName(token.getPrincipal().getAttributes().get("given_name").toString() + " "
                    + token.getPrincipal().getAttributes().get("family_name").toString());
            Set<Role> list = new HashSet<>();
            Optional<Role> a = roleRepository.findById(TbConstants.Roles.USER);
            if (a.isPresent()) {
                list.add(a.get());
            } else {
                throw new IllegalStateException("role not found");
            }
            temp.setRoles(list);
            userRepository.save(temp);
        }
        redirectStrategy.sendRedirect(request, response, "/");

    }
}

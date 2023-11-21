package com.example.DevLucky.demo.Security;

import com.example.DevLucky.demo.Entity.Role;
import com.example.DevLucky.demo.Entity.User;
import com.example.DevLucky.demo.Repo.RoleRepository;
import com.example.DevLucky.demo.Repo.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
        Set<Role> list = new HashSet<>();

        if (userRepository.findByEmail(email).isEmpty()) {
            User temp = new User();
            temp.setEmail(email);
            temp.setUserName(token.getPrincipal().getAttributes().get("given_name").toString() + " "
                    + token.getPrincipal().getAttributes().get("family_name").toString());
            Optional<Role> a = roleRepository.findById(TbConstants.Roles.USER);
            if (a.isPresent()) {
                list.add(a.get());
            } else {
                throw new IllegalStateException("role not found");
            }
            temp.setRoles(list);
            userRepository.save(temp);
        }

        // Load user details to create a UserDetails object
        UserDetails userDetails = loadUserByUsername(email);

        // Create a new authentication object with the updated authorities
        Authentication updatedAuthentication = new UsernamePasswordAuthenticationToken(
                userDetails, authentication.getCredentials(), userDetails.getAuthorities());

        // Set the updated authentication object in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(updatedAuthentication);

        redirectStrategy.sendRedirect(request, response, "/");
    }

    private UserDetails loadUserByUsername(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toList());

            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    "", // You might want to provide a password or leave it as an empty string
                    authorities
            );
        } else {
            // Handle user not found case
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
    }
}
//
//        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
//                email,
//                "",
//                // You might want to provide a password, or it can be an empty string
//
//        );
//
//////        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
////        UserDetails userDetails = loadUserByUsername(email);
////        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
////
//
//
//        // Create a new authentication object with the updated authorities
//        Authentication updatedAuthentication = new UsernamePasswordAuthenticationToken(
//                userDetails, authentication.getCredentials(), authorities);
//
//        // Set the updated authentication object in the SecurityContext
//        SecurityContextHolder.getContext().setAuthentication(updatedAuthentication);
//
//
//        redirectStrategy.sendRedirect(request, response, "/");
//
//    }
//}

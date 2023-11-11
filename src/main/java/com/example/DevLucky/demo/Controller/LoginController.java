package com.example.DevLucky.demo.Controller;

import com.example.DevLucky.demo.Entity.Role;
import com.example.DevLucky.demo.Entity.User;
import com.example.DevLucky.demo.Entity.UserDto;
import com.example.DevLucky.demo.Repo.RoleRepository;
import com.example.DevLucky.demo.Repo.UserRepository;
import com.example.DevLucky.demo.Repo.UserServiceImpl;
import com.example.DevLucky.demo.Security.TbConstants;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Controller
public class LoginController {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;

    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/accessDenied")
    public String getAccessDen() {
        return "accessDenied";
    }

    @RequestMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/registration")
    public String registrationForm(Model model) {
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto, BindingResult result, Model model) {
        Optional<User> existingUser = userService.findUserByEmail(userDto.getEmail());

        if (existingUser.isPresent()) {
            result.rejectValue("email", null, "User already registered !!!");
            return "/registration";
        }

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "/registration";
        }

        Optional<Role> role = roleRepository.findById(TbConstants.Roles.USER);
        User temp = new User();

        if (role.isPresent() && role != null) {
            Set<Role> s = new HashSet<Role>();
            s.add(role.get());
            temp.setRoles(s);
        } else {
            return "redirect:/registration?error=ROLES are not Found";

        }
        temp.setUserName(userDto.getName());
        temp.setEmail(userDto.getEmail());
        temp.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(temp);
        return "redirect:/registration?success";
    }

}
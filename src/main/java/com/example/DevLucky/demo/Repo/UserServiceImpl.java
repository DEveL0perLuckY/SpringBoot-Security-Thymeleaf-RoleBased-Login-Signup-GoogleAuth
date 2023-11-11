package com.example.DevLucky.demo.Repo;

import com.example.DevLucky.demo.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

// import java.util.Arrays;

@Service
public class UserServiceImpl {

    @Autowired
    private UserRepository userRepository;

    // @Autowired
    // private RoleRepository roleRepository;
    //
    // @Autowired
    // private PasswordEncoder passwordEncoder;

    // public void saveUser(UserDto userDto) {
    // Role role = roleRepository.findByName(TbConstants.Roles.USER);
    //
    // if (role == null) {
    // role = roleRepository.save(new Role(TbConstants.Roles.USER));
    // }
    //
    // User temp = new User();
    //
    // temp.setEmail(userDto.getEmail());
    // temp.setPassword(passwordEncoder.encode(userDto.getPassword()));
    // temp.setName(userDto.getName());
    // temp.setRoles(Arrays.asList(role));
    // userRepository.save(temp);
    // }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
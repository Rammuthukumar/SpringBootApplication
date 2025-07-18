package com.example.demo.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.config.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    
    @Autowired private UserService service;
    @Autowired private JwtService jwtService;
    @Autowired AuthenticationManager authenticationManager;

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/register")
    public User saveUser(@Valid @RequestBody User user){
        logger.info("Controller method called");
        return service.saveUser(user);
    }

    
    @PostMapping("/login")
    public UserDTO login(@Valid @RequestBody User user, HttpServletRequest request){
        
        Authentication authentication = authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if(authentication.isAuthenticated()) {
            String token = jwtService.createToken(user.getUsername());

            UserDTO userDTO = new UserDTO();
            userDTO.setJwtToken(token);

          /*   List<Link> links = new ArrayList<>();

            if(user.getRole().equalsIgnoreCase("ADMIN")){
                links.add(Link.of("/order").withRel("order").withType("GET"));
            }
            else{
                links.add(Link.of("/order/users").withRel("order").withType("GET"));
            }

            userDTO.add(links); */
            return userDTO;
        }
        else return new UserDTO();
    }
    
}

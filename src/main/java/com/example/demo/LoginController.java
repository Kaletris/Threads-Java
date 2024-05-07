package com.example.demo;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.security.NoSuchAlgorithmException;

@Controller
public class LoginController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HttpServletResponse httpServletResponse;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("login", new LoginDTO());
        return modelAndView;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity processLogin(@ModelAttribute LoginDTO login, Model model) throws NoSuchAlgorithmException {
        // Check password
        // If password ok, issue cookie, redirect to /
        User user = userRepository.findByName(login.username);

        System.out.println(login.getUsername());
        System.out.println(login.getPassword());
        if (user == null) {
            return ResponseEntity
                    .status(302)
                    .header(HttpHeaders.LOCATION, "/login")
                    .build();
        }
        if (!user.checkPassword(login.password)) {
            return ResponseEntity
                    .status(302)
                    .header(HttpHeaders.LOCATION, "/login")
                    .build();
        }

        ResponseCookie springCookie = ResponseCookie.from("user-id", user.getId().toString())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .build();

        return ResponseEntity
                .status(302)
                .header(HttpHeaders.SET_COOKIE, springCookie.toString())
                .header(HttpHeaders.LOCATION, "/threads")
                .build();
    }


    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView register() {
        ModelAndView modelAndView = new ModelAndView("register");
        modelAndView.addObject("register", new RegisterDTO());
        return modelAndView;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity processRegister(@ModelAttribute RegisterDTO register, Model model) throws NoSuchAlgorithmException {
        // Create user
        // Issue cookie, redirect to /
        System.out.println(register.getUsername());
        System.out.println(register.getPassword());
        System.out.println(register.getConfirmPassword());

        if (!register.getPassword().equals(register.getConfirmPassword())) {
            return ResponseEntity
                    .status(302)
                    .header(HttpHeaders.LOCATION, "/register")
                    .build();
        }

        User user = userRepository.findByName(register.getUsername());

        if(user != null){
            return ResponseEntity
                    .status(302)
                    .header(HttpHeaders.LOCATION, "/register")
                    .build();
        }

        user = new User(register.getUsername(), register.getPassword());

        userRepository.save(user);

        ResponseCookie springCookie = ResponseCookie.from("user-id", user.getId().toString())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .build();

        return ResponseEntity
                .status(302)
                .header(HttpHeaders.SET_COOKIE, springCookie.toString())
                .header(HttpHeaders.LOCATION, "/threads")
                .build();
    }

    public class LoginDTO {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public class RegisterDTO{
        private String username;
        private String password;
        private String confirmPassword;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String password) {
            this.confirmPassword = password;
        }
    }
}
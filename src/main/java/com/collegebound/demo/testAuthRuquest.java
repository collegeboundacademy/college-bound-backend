package com.collegebound.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.collegebound.demo.user.User;
import com.collegebound.demo.user.UserRepository;


@RestController
@RequestMapping("/api/test")
public class testAuthRuquest {

    @Autowired
    private UserRepository userRepo;
    
    @GetMapping()
    public String test(Authentication auth) {
        System.out.println(auth.getName());

        User user = userRepo.findByGithubId(Integer.valueOf(auth.getName())).orElseThrow(() -> new RuntimeException("User not found"));

        return "Hello " + user.getUsername();
    }
}
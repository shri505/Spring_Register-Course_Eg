package com.example.Spring_Login_Eg3.controller;

import com.example.Spring_Login_Eg3.entity.Course;
import com.example.Spring_Login_Eg3.entity.UserInfo;
import com.example.Spring_Login_Eg3.repository.CourseRepository;
import com.example.Spring_Login_Eg3.repository.UserInfoRepository;
import com.example.Spring_Login_Eg3.config.AuthenticationRequest;
import com.example.Spring_Login_Eg3.service.JwtService;
import com.example.Spring_Login_Eg3.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    @Qualifier("userDetailsService")
    private UserInfoService userService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @PostMapping("/addNewUser")
    public String addNewUser(@RequestBody UserInfo userInfo)
    {
        return userService.addUser(userInfo);
    }

    @PostMapping("/generateToken")
    public String generateToken(@RequestBody AuthenticationRequest authRequest)
    {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtService.generateToken(userDetails.getUsername());
    }

    @PostMapping("/createUser")
    public UserInfo createUser(@RequestBody UserInfo userInfo)
    {
        return userService.createUser(userInfo);
    }

    @GetMapping("/getUser/{id}")
    public UserInfo getUserById(@PathVariable int id)
    {
        return userService.getUserById(id);
    }


    @GetMapping("/getAllUsers")
    public List<UserInfo> getAllUsers()
    {
        return userService.getAllUsers();
    }

    @PutMapping("/updateUser/{id}")
    public UserInfo updateUser(@PathVariable int id, @RequestBody UserInfo userInfo)
    {
        return userService.updateUser(id, userInfo);
    }

    @DeleteMapping("/deleteUser/{id}")
    public void deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
    }

    @PostMapping("/enroll")
    public String enrollInCourse(@RequestBody Course course,@RequestHeader("Authorization") String authorizationHeader)
    {
        courseRepository.save(course);
        return "redirect:/courses";
    }

//    @GetMapping("/courses")
//    public List<Course> listCourses() {
//        return courseRepository.findAll();
//    }




    @GetMapping("/getCourse")
    public List<Course> getCourseById(@RequestBody Course course,@RequestHeader("Authorization") String authorizationHeader)
    {
        return courseRepository.findAll();
//        String jwt = token.substring(7); String username = jwtUtil.extractUsername(jwt);
//        return userDetailsService.loadUserByUsername(userName);
    }

    @GetMapping("/getCourse/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable int id,
                                                @RequestHeader("Authorization") String authorizationHeader)
    {
        // Validate the authorization header if needed
        // Example validation (to be implemented as per your security requirements)

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return ResponseEntity.ok(course);
    }

    @DeleteMapping("/deleteCourse/{id}")
    public ResponseEntity<Void> deleteCourseById(@PathVariable int id)
    {
        Course course = courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course not found"));
        courseRepository.delete(course);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/updateCourse/{id}")
    public Course updateCourse(@PathVariable int id, @RequestBody Course courseDetails)
    {
        Course course = courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course not found"));
        course.setCourseName(courseDetails.getCourseName());
        return courseRepository.save(course);
    }

}

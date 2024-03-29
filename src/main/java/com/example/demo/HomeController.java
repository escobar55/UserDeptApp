package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
//import org.springframework.util.ObjectUtils;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;
//import java.util.Properties;
import java.util.Set;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @Autowired
    SendEmail sendEmail;

    //****Add user
    @GetMapping("/register")
    public String showRegistrationPage(Model model){
        model.addAttribute("user", new User());
        model.addAttribute("departments", departmentRepository.findAll());
        return "registration";
    }

    @PostMapping("/register")
    public String processRegistrationPage(@Valid @ModelAttribute("user") User user, BindingResult result, Model model,
                                          @RequestParam("departmentid") long departmentid,@RequestParam("file") MultipartFile file ){
        model.addAttribute("user", user);
        //***connection***
        user.setDepartment(departmentRepository.findById(departmentid).get());
        userRepository.save(user);
        Department department = departmentRepository.findById(departmentid).get();
        Set<User> users = department.getUsers();

        users.add(user);
        department.setUsers(users);
        departmentRepository.save(department);
        //***************

        if(result.hasErrors()){
            return "registration";
        }

        if (file.isEmpty()) {
            return "redirect:/";
        }
        else {

            //userService.saveUser(user);
            model.addAttribute("message", "User Account Created");
        }
        try {
            Map uploadResults = cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype", "auto"));
            user.setHeadshot(uploadResults.get("url").toString());
            userRepository.save(user);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/register";
        }

        //userRepository.save(user);
        return "redirect:/";
    }

    //****Index
    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("deparments", departmentRepository.findAll());
        model.addAttribute("users", userRepository.findAll());
        //****
        User user = new User();

        return "index";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/secure")
    public String secure(Principal principal, Model model) {
        String username = principal.getName();
        model.addAttribute("user", userRepository.findByUsername(username));
        return "secure";
    }

    //********after security
    //**department
    @GetMapping("/addDepartment")
    public String departmentForm(Model model){
        model.addAttribute("department", new Department());
        return "departmentform";
    }

    @PostMapping("/processDepartment")
    public String processDept(@Valid @ModelAttribute Department department, BindingResult result
                              ){
        //*****
        /*
        User user = new User();
        user.setDepartment(departmentRepository.findById(departmentid).get());
        userRepository.save(user);

         */
        //***
        if(result.hasErrors()){
            return "departmentform";
        }
        departmentRepository.save(department);
        return "redirect:/";
    }
    //*******
    @PostMapping("/search")
    public String search(Model model, @RequestParam("search") String search){
        model.addAttribute("departments", departmentRepository.findByDeptNameContainingIgnoreCase(search));
        return "search";
    }

    //Implementing email
    /*
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("supersupervisor89@gmail.com");
        mailSender.setPassword("superpassword");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

     */
      /*
    @RequestMapping("/sendemail/{id}")
    public String sendEmail(@PathVariable("id") long userId,
            Model model){
        //model.addAttribute("emails", userRepository.findById(userId).get());
        //User empEmail = userRepository.findById(userId).get();
        model = sendEmail.SendSimpleEmail(model, userId);
        //sendEmail.SendSimpleEmail();
        return "confirmemail";
    }

       */

    @RequestMapping("/sendemail/{id}")
    public String sendEmail(@PathVariable("id") long userId,
                            Model model){
        //model.addAttribute("emails", userRepository.findById(userId).get());
        //User empEmail = userRepository.findById(userId).get();
        model = sendEmail.SendSimpleEmail(model, userId);
        //sendEmail.SendSimpleEmail();
        return "confirmemail";
    }

    @GetMapping("/sendemail")
    public  String sendEmail(Model model){
        model.addAttribute("emailtext", new EmailText());
        return "emailform";
    }

}

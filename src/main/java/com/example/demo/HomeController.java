package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Set;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    UserRepository userRepository;

    //****Add user
    @GetMapping("/register")
    public String showRegistrationPage(Model model){
        model.addAttribute("user", new User());
        model.addAttribute("departments", departmentRepository.findAll());
        return "registration";
    }

    @PostMapping("/register")
    public String processRegistrationPage(@Valid @ModelAttribute("user") User user, BindingResult result, Model model,
                                          @RequestParam("departmentid") long departmentid){
        //*******

        //Department department = departmentRepository.findById(departmentid)

        user.setDepartment(departmentRepository.findById(departmentid).get());
        userRepository.save(user);
        Department department = departmentRepository.findById(departmentid).get();
        Set<User> users = department.getUsers();

        users.add(user);
        department.setUsers(users);
        departmentRepository.save(department);

        //*******
        model.addAttribute("user", user);
        if(result.hasErrors()){
            return "registration";
        }
        else {
            //userService.saveUser(user);
            model.addAttribute("message", "User Account Created");
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

}

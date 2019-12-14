package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment; //enviroment
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
//import java.net.Authenticator;
//import java.net.PasswordAuthentication;
//import org.springframework.boot.web.servlet.server.Session;

@Service
class SendEmail {

    private TemplateEngine templateEngine; //import

    @Autowired
    Environment environment; //imported

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired //constructor
    public SendEmail(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    private Properties GetProperties() {  //properties class imported

        Properties properties = new Properties();
        properties.put("mail.smtp.starttls.enable", environment.getProperty("mail.smtp.starttls.enable"));
        properties.put("mail.smtp.auth", environment.getProperty("mail.smtp.auth"));
        properties.put("mail.smtp.host", environment.getProperty("mail.smtp.host"));
        properties.put("mail.smtp.port", environment.getProperty("mail.smtp.port"));
        properties.put("mail.smtp.ssl.trust", environment.getProperty("mail.smtp.ssl.trust"));

        return properties;

    }

    private Session GetSession() {

        final String username = "supersupervisor89@gmail.com";
        final String password = "superpassword";

        //Create session
        Session session = Session.getInstance(GetProperties(), new Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        return session;
    }

    public String buildTemplateWithContent(String message) {
        Context context = new Context();
        context.setVariable("message", message);
        return templateEngine.process("mailtemplate", context);
    }

    public Model SendSimpleEmail(Model model, @PathVariable("id") long userId) {
        try {
            Message message = new MimeMessage(GetSession());

            //Get current user email
            User currentUser = userService.getAuthenticatedUser();
            String currentUserEmail = currentUser.getEmail();
            model.addAttribute("email", currentUserEmail);

            //Send email to the currentUser's email 
            message.setFrom(new InternetAddress(currentUserEmail));
            
            //Find employee's email 
            //model.addAttribute("emails", userRepository.findById(userId).get());
            //model.addAttribute("emails", userRepository.findAll());
            //Find employee's email
            User empEmail = userRepository.findById(userId).get();
            String email = empEmail.getEmail();
            model.addAttribute("emails", email);
            
            
            //Set the employee email
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));

            //email Subject

            message.setSubject("Timesheet Status");

            //Email content
            message.setText("Your timesheet has been rejected");

            Transport.send(message);

            return model;

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}




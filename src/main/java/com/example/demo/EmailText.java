package com.example.demo;

public class EmailText {

    private String emailText;

    public EmailText() { //constructor
    }

    public EmailText(String emailText) { //overloaded constructor
        this.emailText = emailText;
    }

    public String getEmailText() {
        return emailText;
    }

    public void setEmailText(String emailText) {
        this.emailText = emailText;
    }
}

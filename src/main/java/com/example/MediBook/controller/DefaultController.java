package com.example.MediBook.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {
    @GetMapping("/")
    public String home(){
        return "redirect:/MediBook/home.html";
    }
}

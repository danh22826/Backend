package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Bất cứ ai vào đường dẫn gốc (/) sẽ bị tự động đẩy sang /html/main.html
    @GetMapping("/")
    public String home() {
        return "redirect:/html/main.html";
    }
}
package com.campus.jobboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmployerController {

    @GetMapping("/employer/dashboard")
    public String employerDashboard() {
        return "employer/dashboard";
    }
}

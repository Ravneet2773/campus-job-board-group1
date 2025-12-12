package com.campus.jobboard.controller;

import com.campus.jobboard.model.Job;
import com.campus.jobboard.service.JobService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class StudentJobController {

    private final JobService jobService;

    public StudentJobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/student/jobs")
    public String listJobsForStudent(Model model) {
        // Show all jobs for now; admin filtering is not used
        List<Job> jobs = jobService.findAll();
        model.addAttribute("jobs", jobs);
        return "student/jobs";
    }
}

package com.campus.jobboard.controller;

import com.campus.jobboard.model.Job;
import com.campus.jobboard.service.JobService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class AdminJobController {

    private final JobService jobService;

    public AdminJobController(JobService jobService) {
        this.jobService = jobService;
    }

    // show only pending jobs for approval
    @GetMapping("/admin/jobs")
    public String listPendingJobs(Model model) {
        List<Job> jobs = jobService.findPendingJobs();
        model.addAttribute("jobs", jobs);
        return "admin/jobs";
    }

    @PostMapping("/admin/jobs/{id}/approve")
    public String approveJob(@PathVariable Long id) {
        jobService.approveJob(id);
        return "redirect:/admin/jobs?success=approved";
    }

    @PostMapping("/admin/jobs/{id}/reject")
    public String rejectJob(@PathVariable Long id) {
        jobService.rejectJob(id);
        return "redirect:/admin/jobs?success=rejected";
    }
}

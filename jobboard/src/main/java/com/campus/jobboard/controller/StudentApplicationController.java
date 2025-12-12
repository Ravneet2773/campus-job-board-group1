package com.campus.jobboard.controller;

import com.campus.jobboard.model.Job;
import com.campus.jobboard.model.JobApplication;
import com.campus.jobboard.model.User;
import com.campus.jobboard.service.ApplicationService;
import com.campus.jobboard.service.JobService;
import com.campus.jobboard.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class StudentApplicationController {

    private final JobService jobService;
    private final UserService userService;
    private final ApplicationService applicationService;

    public StudentApplicationController(JobService jobService,
                                        UserService userService,
                                        ApplicationService applicationService) {
        this.jobService = jobService;
        this.userService = userService;
        this.applicationService = applicationService;
    }

    // handle Apply click
    @GetMapping("/student/jobs/{id}/apply")
    public String applyToJob(@PathVariable Long id,
                             Authentication auth) {
        User student = userService.findByEmail(auth.getName());
        Job job = jobService.findByIdOrNull(id);
        if (job == null) {
            // job not found -> back to list with error flag
            return "redirect:/student/jobs?error=notfound";
        }

        try {
            applicationService.applyToJob(job, student);
            // success -> go to applications page with success flag
            return "redirect:/student/applications?success=applied";
        } catch (IllegalStateException e) {
            // already applied -> applications page with error flag
            return "redirect:/student/applications?error=duplicate";
        }
    }

    // list student's applications
    @GetMapping("/student/applications")
    public String listStudentApplications(Authentication auth,
                                          Model model) {
        User student = userService.findByEmail(auth.getName());
        List<JobApplication> apps = applicationService.getApplicationsForStudent(student);
        model.addAttribute("applications", apps);
        return "student/applications";
    }
}

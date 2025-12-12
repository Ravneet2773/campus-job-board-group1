package com.campus.jobboard.controller;

import com.campus.jobboard.model.Job;
import com.campus.jobboard.model.JobApplication;
import com.campus.jobboard.model.User;
import com.campus.jobboard.service.ApplicationService;
import com.campus.jobboard.service.JobService;
import com.campus.jobboard.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
public class EmployerJobController {

    private final JobService jobService;
    private final UserService userService;
    private final ApplicationService applicationService;

    public EmployerJobController(JobService jobService,
                                 UserService userService,
                                 ApplicationService applicationService) {
        this.jobService = jobService;
        this.userService = userService;
        this.applicationService = applicationService;
    }

    // list + create form
    @GetMapping("/employer/jobs")
    public String listJobs(Authentication auth, Model model) {
        User employer = userService.findByEmail(auth.getName());
        model.addAttribute("jobs", jobService.findJobsByEmployer(employer));

        if (!model.containsAttribute("jobForm")) {
            model.addAttribute("jobForm", new JobForm());
        }

        return "employer/jobs";
    }

    @PostMapping("/employer/jobs")
    public String createJob(Authentication auth,
                            @Valid @ModelAttribute("jobForm") JobForm form,
                            BindingResult bindingResult,
                            Model model) {

        User employer = userService.findByEmail(auth.getName());

        BigDecimal salary = null;
        if (form.getSalary() != null && !form.getSalary().isBlank()) {
            try {
                salary = new BigDecimal(form.getSalary());
                if (salary.compareTo(BigDecimal.ZERO) <= 0) {
                    bindingResult.rejectValue("salary", "positive", "Salary must be positive");
                }
            } catch (NumberFormatException ex) {
                bindingResult.rejectValue("salary", "number", "Salary must be a valid number");
            }
        }

        LocalDate deadline = null;
        if (form.getDeadline() != null && !form.getDeadline().isBlank()) {
            try {
                deadline = LocalDate.parse(form.getDeadline());
            } catch (Exception ex) {
                bindingResult.rejectValue("deadline", "date", "Deadline must be a valid date");
            }
        } else {
            bindingResult.rejectValue("deadline", "required", "Deadline is required");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("jobs", jobService.findJobsByEmployer(employer));
            return "employer/jobs";
        }

        jobService.createJob(
                employer,
                form.getTitle(),
                form.getDescription(),
                form.getLocation(),
                salary,
                deadline
        );

        return "redirect:/employer/jobs?success=created";
    }

    // show edit form
    @GetMapping("/employer/jobs/{id}/edit")
    public String editJob(@PathVariable Long id,
                          Authentication auth,
                          Model model) {
        User employer = userService.findByEmail(auth.getName());
        Job job = jobService.findByIdOrNull(id);
        if (job == null || !job.getEmployer().getId().equals(employer.getId())) {
            return "redirect:/employer/jobs";
        }

        JobForm form = new JobForm();
        form.setTitle(job.getTitle());
        form.setDescription(job.getDescription());
        form.setLocation(job.getLocation());
        form.setSalary(job.getSalary() != null ? job.getSalary().toString() : "");
        form.setDeadline(job.getDeadline() != null ? job.getDeadline().toString() : "");

        model.addAttribute("jobForm", form);
        model.addAttribute("jobId", id);
        return "employer/edit-job";
    }

    // handle edit submit
    @PostMapping("/employer/jobs/{id}/edit")
    public String updateJob(@PathVariable Long id,
                            Authentication auth,
                            @Valid @ModelAttribute("jobForm") JobForm form,
                            BindingResult bindingResult,
                            Model model) {

        User employer = userService.findByEmail(auth.getName());
        Job job = jobService.findByIdOrNull(id);
        if (job == null || !job.getEmployer().getId().equals(employer.getId())) {
            return "redirect:/employer/jobs";
        }

        BigDecimal salary = null;
        if (form.getSalary() != null && !form.getSalary().isBlank()) {
            try {
                salary = new BigDecimal(form.getSalary());
                if (salary.compareTo(BigDecimal.ZERO) <= 0) {
                    bindingResult.rejectValue("salary", "positive", "Salary must be positive");
                }
            } catch (NumberFormatException ex) {
                bindingResult.rejectValue("salary", "number", "Salary must be a valid number");
            }
        }

        LocalDate deadline = null;
        if (form.getDeadline() != null && !form.getDeadline().isBlank()) {
            try {
                deadline = LocalDate.parse(form.getDeadline());
            } catch (Exception ex) {
                bindingResult.rejectValue("deadline", "date", "Deadline must be a valid date");
            }
        } else {
            bindingResult.rejectValue("deadline", "required", "Deadline is required");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("jobId", id);
            return "employer/edit-job";
        }

        jobService.updateJob(
                id,
                form.getTitle(),
                form.getDescription(),
                form.getLocation(),
                salary,
                deadline
        );

        return "redirect:/employer/jobs?success=updated";
    }

    // delete job
    @GetMapping("/employer/jobs/{id}/delete")
    public String deleteJob(@PathVariable Long id,
                            Authentication auth) {
        User employer = userService.findByEmail(auth.getName());
        Job job = jobService.findByIdOrNull(id);
        if (job != null && job.getEmployer().getId().equals(employer.getId())) {
            jobService.deleteJob(id);
        }
        return "redirect:/employer/jobs?success=deleted";
    }

    // view applicants for a job
    @GetMapping("/employer/jobs/{id}/applications")
    public String viewApplicants(@PathVariable Long id,
                                 Authentication auth,
                                 Model model) {

        User employer = userService.findByEmail(auth.getName());
        Job job = jobService.findByIdOrNull(id);
        if (job == null || !job.getEmployer().getId().equals(employer.getId())) {
            return "redirect:/employer/jobs";
        }

        model.addAttribute("job", job);
        model.addAttribute("applications", applicationService.getApplicationsForJob(job));
        return "employer/job-applicants";
    }

    // form DTO with validation
    public static class JobForm {

        @NotBlank
        private String title;

        @NotBlank
        private String description;

        @NotBlank
        private String location;

        // string input, validated manually
        private String salary;

        @NotBlank
        private String deadline;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getSalary() {
            return salary;
        }

        public void setSalary(String salary) {
            this.salary = salary;
        }

        public String getDeadline() {
            return deadline;
        }

        public void setDeadline(String deadline) {
            this.deadline = deadline;
        }
    }
}

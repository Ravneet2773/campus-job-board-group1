package com.campus.jobboard.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JobNotFoundException.class)
    public String handleJobNotFound(JobNotFoundException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error/job-not-found";
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFound(UserNotFoundException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error/user-not-found";
    }

    @ExceptionHandler(DuplicateApplicationException.class)
    public String handleDuplicateApplication(DuplicateApplicationException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error/duplicate-application";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneric(Exception ex, Model model) {
        model.addAttribute("message", "Unexpected error occurred. Please try again.");
        return "error/general-error";
    }
}

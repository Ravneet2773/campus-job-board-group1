package com.campus.jobboard.service;

import com.campus.jobboard.exception.DuplicateApplicationException;
import com.campus.jobboard.model.Job;
import com.campus.jobboard.model.JobApplication;
import com.campus.jobboard.model.User;
import com.campus.jobboard.repository.JobApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ApplicationService {

    private final JobApplicationRepository jobApplicationRepository;

    public ApplicationService(JobApplicationRepository jobApplicationRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
    }

    public JobApplication applyToJob(Job job, User student) {
        // prevent duplicate applications
        jobApplicationRepository.findByJobAndStudent(job, student)
                .ifPresent(existing -> {
                    throw new DuplicateApplicationException("You have already applied for this job.");
                });

        JobApplication app = new JobApplication();
        app.setJob(job);
        app.setStudent(student);
        app.setStatus(JobApplication.Status.SUBMITTED);
        return jobApplicationRepository.save(app);
    }

    public List<JobApplication> getApplicationsForJob(Job job) {
        return jobApplicationRepository.findByJob(job);
    }

    public List<JobApplication> getApplicationsForStudent(User student) {
        return jobApplicationRepository.findByStudent(student);
    }

    public void updateStatus(Long applicationId, JobApplication.Status status) {
        jobApplicationRepository.findById(applicationId)
                .ifPresent(app -> app.setStatus(status));
    }
}

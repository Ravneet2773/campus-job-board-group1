package com.campus.jobboard.service;

import com.campus.jobboard.exception.JobNotFoundException;
import com.campus.jobboard.model.Job;
import com.campus.jobboard.model.User;
import com.campus.jobboard.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Job createJob(User employer,
                         String title,
                         String description,
                         String location,
                         BigDecimal salary,
                         LocalDate deadline) {

        Job job = new Job();
        job.setEmployer(employer);
        job.setTitle(title);
        job.setDescription(description);
        job.setLocation(location);
        job.setSalary(salary);
        job.setDeadline(deadline);
        job.setStatus(Job.Status.PENDING);

        return jobRepository.save(job);
    }

    public Job updateJob(Long jobId,
                         String title,
                         String description,
                         String location,
                         BigDecimal salary,
                         LocalDate deadline) {

        Optional<Job> optionalJob = jobRepository.findById(jobId);
        if (optionalJob.isEmpty()) {
            throw new JobNotFoundException(jobId);
        }

        Job job = optionalJob.get();
        job.setTitle(title);
        job.setDescription(description);
        job.setLocation(location);
        job.setSalary(salary);
        job.setDeadline(deadline);
        return job;
    }

    public List<Job> findAll() {
        return jobRepository.findAll();
    }

    public Job findByIdOrNull(Long id) {
        return jobRepository.findById(id).orElse(null);
    }

    // new helper that throws custom exception
    public Job findByIdOrThrow(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException(id));
    }

    public List<Job> findJobsByEmployer(User employer) {
        return jobRepository.findByEmployer(employer);
    }

    public List<Job> findPendingJobs() {
        return jobRepository.findByStatus(Job.Status.PENDING);
    }

    public void updateStatus(Long jobId, Job.Status status) {
        jobRepository.findById(jobId)
                .ifPresent(job -> job.setStatus(status));
    }

    public void approveJob(Long jobId) {
        Job job = findByIdOrThrow(jobId);
        job.setStatus(Job.Status.APPROVED);
    }

    public void rejectJob(Long jobId) {
        Job job = findByIdOrThrow(jobId);
        job.setStatus(Job.Status.REJECTED);
    }

    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }
}

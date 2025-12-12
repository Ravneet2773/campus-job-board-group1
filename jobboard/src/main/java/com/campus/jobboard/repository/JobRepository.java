package com.campus.jobboard.repository;

import com.campus.jobboard.model.Job;
import com.campus.jobboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByEmployer(User employer);

    List<Job> findByStatus(Job.Status status);
}

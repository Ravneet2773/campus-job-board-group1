package com.campus.jobboard.service;

import com.campus.jobboard.exception.JobNotFoundException;
import com.campus.jobboard.model.Job;
import com.campus.jobboard.model.User;
import com.campus.jobboard.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JobServiceTest {

    private final JobRepository jobRepository = Mockito.mock(JobRepository.class);
    private final JobService jobService = new JobService(jobRepository);

    @Test
    void createJob_savesJobWithPendingStatus() {
        User employer = new User();
        employer.setId(1L);

        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Job saved = jobService.createJob(
                employer,
                "Test Job",
                "Description",
                "Location",
                BigDecimal.TEN,
                LocalDate.now().plusDays(7)
        );

        assertEquals("Test Job", saved.getTitle());
        assertEquals(Job.Status.PENDING, saved.getStatus());
        assertEquals(employer, saved.getEmployer());
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    void updateJob_throwsWhenNotFound() {
        when(jobRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(JobNotFoundException.class,
                () -> jobService.updateJob(99L, "t", "d", "l", BigDecimal.ONE, LocalDate.now()));
    }
}
// Integrated and reviewed by Arshdeep.
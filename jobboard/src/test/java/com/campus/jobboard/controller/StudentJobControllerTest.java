package com.campus.jobboard.controller;

import com.campus.jobboard.model.Job;
import com.campus.jobboard.service.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StudentJobControllerTest {

    private JobService jobService;
    private StudentJobController controller;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        jobService = Mockito.mock(JobService.class);
        controller = new StudentJobController(jobService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void listJobsForStudent_returnsOk() throws Exception {
        when(jobService.findAll()).thenReturn(Collections.singletonList(new Job()));

        mockMvc.perform(get("/student/jobs"))
                .andExpect(status().isOk());
    }

    @Test
    void listJobsForStudent_addsJobsToModel() {
        when(jobService.findAll()).thenReturn(Collections.singletonList(new Job()));
        Model model = new ConcurrentModel();

        String viewName = controller.listJobsForStudent(model);

        assertEquals("student/jobs", viewName);
        assertEquals(1, ((Iterable<?>) model.getAttribute("jobs")).spliterator().getExactSizeIfKnown());
    }
}

package com.fn42.assessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fn42.assessment.dto.StudentDTO;
import com.fn42.assessment.dto.SubjectDTO;
import com.fn42.assessment.exception_handling.ServiceException;
import com.fn42.assessment.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link StudentController}.
 * Uses Spring's {@link WebMvcTest} for testing only the web layer.
 */
@WebMvcTest(controllers = {StudentController.class, ServiceException.class})
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    private StudentDTO mockStudentDTO;

    /**
     * Sets up a mock student DTO before each test execution.
     */
    @BeforeEach
    void setUp() {
        List<SubjectDTO> subjects = Arrays.asList(
                new SubjectDTO("Maths", 85),
                new SubjectDTO("Physics", 92)
        );

        mockStudentDTO = new StudentDTO("student1", 20, "Pune, India", subjects);
    }

    /**
     * Test: Verifies that a valid student name returns the correct student data.
     * Also verifies async behavior, content type, and response fields.
     */
    @Test
    void getStudentData_WhenStudentExists_ShouldReturnStudentData() throws Exception {
        String studentName = "student1";
        when(studentService.getStudentDetailsAsync(studentName))
                .thenReturn(CompletableFuture.completedFuture(mockStudentDTO));

        // Start async request
        MvcResult mvcResult = mockMvc.perform(get("/StudentDetails/getData.htm")
                        .param("studentName", studentName))
                .andExpect(request().asyncStarted())
                .andReturn();

        // Complete async request
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("student1"))
                .andExpect(jsonPath("$.age").value(20))
                .andExpect(jsonPath("$.address").value("Pune, India"))
                .andExpect(jsonPath("$.subjects").isArray())
                .andExpect(jsonPath("$.subjects[0].name").value("Maths"))
                .andExpect(jsonPath("$.subjects[0].marks").value(85))
                .andExpect(jsonPath("$.subjects[1].name").value("Physics"))
                .andExpect(jsonPath("$.subjects[1].marks").value(92));

        verify(studentService, times(1)).getStudentDetailsAsync(studentName);
    }

    /**
     * Test: Verifies that if no 'studentName' parameter is provided,
     * the endpoint responds with HTTP 400 Bad Request.
     */
    @Test
    void getStudentData_WhenMissingStudentNameParameter_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/StudentDetails/getData.htm"))
                .andExpect(status().isBadRequest());

        verify(studentService, never()).getStudentDetailsAsync(anyString());
    }

    /**
     * Test: Verifies that if a student has no subjects, the response
     * includes an empty subjects list.
     */
    @Test
    void getStudentData_WithStudentHavingNoSubjects_ShouldReturnEmptySubjectsList() throws Exception {
        String studentName = "student1";
        StudentDTO studentWithNoSubjects = new StudentDTO("student1", 19, "Mumbai, India", Arrays.asList());
        when(studentService.getStudentDetailsAsync(studentName))
                .thenReturn(CompletableFuture.completedFuture(studentWithNoSubjects));

        // Start async request
        MvcResult mvcResult = mockMvc.perform(get("/StudentDetails/getData.htm")
                        .param("studentName", studentName))
                .andExpect(request().asyncStarted())
                .andReturn();

        // Complete async request
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("student1"))
                .andExpect(jsonPath("$.age").value(19))
                .andExpect(jsonPath("$.address").value("Mumbai, India"))
                .andExpect(jsonPath("$.subjects").isArray())
                .andExpect(jsonPath("$.subjects.length()").value(0));

        verify(studentService, times(1)).getStudentDetailsAsync(studentName);
    }
}
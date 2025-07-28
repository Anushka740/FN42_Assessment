package com.fn42.assessment.controller;

import com.fn42.assessment.dto.StudentDTO;
import com.fn42.assessment.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

/**
 * REST controller for handling student data requests.
 * Provides endpoints to fetch student details asynchronously based on student name.
 */
@RestController
@RequestMapping("/StudentDetails")
public class StudentController {
    @Autowired
    private StudentService studentService;

    /**
     * Endpoint to fetch student details by student name.
     * This method returns a {@link CompletableFuture} wrapping a {@link ResponseEntity}
     * containing {@link StudentDTO} with basic student details and associated subject data.
     *
     * @param studentName the name of the student to retrieve data for.
     * @return a CompletableFuture containing ResponseEntity with student details.
     */
    @GetMapping("/getData.htm")
    public CompletableFuture<ResponseEntity<StudentDTO>> getStudentData(@RequestParam String studentName) {
        return studentService.getStudentDetailsAsync(studentName)
                .thenApply(ResponseEntity::ok);
    }
}

package com.fn42.assessment.service;

import com.fn42.assessment.dto.StudentDTO;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public interface StudentService {
    CompletableFuture<StudentDTO> getStudentDetailsAsync(String studentName);
}

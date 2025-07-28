package com.fn42.assessment.service;

import com.fn42.assessment.dto.StudentDTO;
import com.fn42.assessment.dto.SubjectDTO;
import com.fn42.assessment.entity.Student;
import com.fn42.assessment.exception_handling.ServiceException;
import com.fn42.assessment.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service implementation for handling student-related operations.
 * Fetches student data from the database and maps it to DTOs asynchronously.
 */
@Service
public class StudentServiceImp implements StudentService{

    private final StudentRepository studentRepository;

    /**
     * Constructs a new StudentServiceImp with the given StudentRepository.
     *
     * @param studentRepository the repository used to access student data.
     */
    @Autowired
    public StudentServiceImp(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * Asynchronously fetches the details of a student by name.
     * Maps the student entity and associated subjects to a {@link StudentDTO}.
     *
     * @param studentName the name of the student to fetch.
     * @return a CompletableFuture containing the student details as a {@link StudentDTO}.
     * @throws ServiceException if the student is not found in the database.
     */
    public CompletableFuture<StudentDTO> getStudentDetailsAsync(String studentName) {
        return CompletableFuture.supplyAsync(() -> {
            Student student = studentRepository.findFirstByName(studentName)
                    .orElseThrow(() -> new ServiceException("Student not found with name: " + studentName, HttpStatus.NOT_FOUND));

            List<SubjectDTO> subjectDTOList = student.getSubjects().stream()
                    .map(s -> new SubjectDTO(s.getName(), s.getMarks()))
                    .toList();

            return new StudentDTO(student.getName(), student.getAge(), student.getAddress(), subjectDTOList);
        });
    }
}

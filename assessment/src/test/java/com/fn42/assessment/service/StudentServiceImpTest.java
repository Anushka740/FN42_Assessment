// StudentServiceImpTest.java
package com.fn42.assessment.service;

import com.fn42.assessment.dto.StudentDTO;
import com.fn42.assessment.dto.SubjectDTO;
import com.fn42.assessment.entity.Student;
import com.fn42.assessment.entity.Subject;
import com.fn42.assessment.exception_handling.ServiceException;
import com.fn42.assessment.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link StudentServiceImp} class.
 * Tests different scenarios for the getStudentDetailsAsync method including valid, not found, and no subjects.
 */
@ExtendWith(MockitoExtension.class)
class StudentServiceImpTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentServiceImp studentService;

    private Student mockStudent;
    private List<Subject> mockSubjects;

    /**
     * Setup mock data before each test.
     */
    @BeforeEach
    void setUp() {
        // Create mock subjects
        Subject subject1 = new Subject();
        subject1.setName("Maths");
        subject1.setMarks(85);

        Subject subject2 = new Subject();
        subject2.setName("Physics");
        subject2.setMarks(92);

        mockSubjects = Arrays.asList(subject1, subject2);

        // Create mock student
        mockStudent = new Student();
        mockStudent.setName("student1");
        mockStudent.setAge(20);
        mockStudent.setAddress("Pune, India");
        mockStudent.setSubjects(mockSubjects);
    }

    /**
     * Test getStudentDetailsAsync() with a valid student name.
     * Expects the correct DTO to be returned with valid subject data.
     */
    @Test
    void getStudentDetailsAsync_WhenStudentExists() throws ExecutionException, InterruptedException {
        String studentName = "student1";
        when(studentRepository.findFirstByName(studentName)).thenReturn(Optional.of(mockStudent));

        CompletableFuture<StudentDTO> result = studentService.getStudentDetailsAsync(studentName);
        StudentDTO studentDTO = result.get();

        assertNotNull(studentDTO);
        assertEquals("student1", studentDTO.getName());
        assertEquals(20, studentDTO.getAge());
        assertEquals("Pune, India", studentDTO.getAddress());
        assertEquals(2, studentDTO.getSubjects().size());

        List<SubjectDTO> subjects = studentDTO.getSubjects();
        assertEquals("Maths", subjects.get(0).getName());
        assertEquals(85, subjects.get(0).getMarks());
        assertEquals("Physics", subjects.get(1).getName());
        assertEquals(92, subjects.get(1).getMarks());

        verify(studentRepository, times(1)).findFirstByName(studentName);
    }

    /**
     * Test getStudentDetailsAsync() when student is not found.
     * Expects ServiceException to be thrown with NOT_FOUND status.
     */
    @Test
    void getStudentDetailsAsync_WhenStudentNotFound() {
        // Arrange
        String studentName = "Non Existent Student";
        when(studentRepository.findFirstByName(studentName)).thenReturn(Optional.empty());

        // Act & Assert
        CompletableFuture<StudentDTO> result = studentService.getStudentDetailsAsync(studentName);

        ExecutionException exception = assertThrows(ExecutionException.class, result::get);
        assertTrue(exception.getCause() instanceof ServiceException);

        ServiceException serviceException = (ServiceException) exception.getCause();
        assertEquals("Student not found with name: " + studentName, serviceException.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, serviceException.getHttpStatus());

        verify(studentRepository, times(1)).findFirstByName(studentName);
    }

    /**
     * Test getStudentDetailsAsync() when student has no subjects.
     * Expects an empty subject list in the DTO.
     */
    @Test
    void getStudentDetailsAsync_WhenStudentHasNoSubjects() throws ExecutionException, InterruptedException {
        // Arrange
        String studentName = "student1";
        Student studentWithNoSubjects = new Student();
        studentWithNoSubjects.setName("student1");
        studentWithNoSubjects.setAge(19);
        studentWithNoSubjects.setAddress("Mumbai, India");
        studentWithNoSubjects.setSubjects(Arrays.asList());

        when(studentRepository.findFirstByName(studentName)).thenReturn(Optional.of(studentWithNoSubjects));

        // Act
        CompletableFuture<StudentDTO> result = studentService.getStudentDetailsAsync(studentName);
        StudentDTO studentDTO = result.get();

        // Assert
        assertNotNull(studentDTO);
        assertEquals("student1", studentDTO.getName());
        assertEquals(19, studentDTO.getAge());
        assertEquals("Mumbai, India", studentDTO.getAddress());
        assertTrue(studentDTO.getSubjects().isEmpty());

        verify(studentRepository, times(1)).findFirstByName(studentName);
    }

    /**
     * Test whether getStudentDetailsAsync() returns a CompletableFuture object asynchronously.
     */
    @Test
    void getStudentDetailsAsync_ShouldExecuteAsynchronously() {
        // Arrange
        String studentName = "student1";
        when(studentRepository.findFirstByName(studentName)).thenReturn(Optional.of(mockStudent));

        // Act
        CompletableFuture<StudentDTO> result = studentService.getStudentDetailsAsync(studentName);

        // Assert
        assertNotNull(result);
        assertFalse(result.isDone()); // Should not be completed immediately in most cases
        assertTrue(result instanceof CompletableFuture);
    }
}

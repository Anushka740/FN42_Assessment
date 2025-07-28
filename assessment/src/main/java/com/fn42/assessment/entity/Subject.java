package com.fn42.assessment.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "subject_details", schema = "fn42")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int marks;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
}

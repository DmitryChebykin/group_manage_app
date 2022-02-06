package com.example.demo.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "_students")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student extends BaseEntity<Long> {
    @NotBlank
    @Column(name = "full_name", unique = true)
    private String fullName;

    @OneToMany(mappedBy = "student", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<StudentGroup> studentGroup = new ArrayList<>();
}



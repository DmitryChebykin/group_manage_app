package com.example.demo.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "_student_group_link", uniqueConstraints = {@UniqueConstraint(name = "one_student_one_group", columnNames = {"student_id "})}, indexes = @Index(name = "group_index", columnList = "group_id"))
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentGroup extends BaseEntity<Long> {
    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;
}
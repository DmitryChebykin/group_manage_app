package com.example.demo.repository;

import com.example.demo.entity.Group;
import com.example.demo.entity.Student;
import com.example.demo.entity.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentGroupRepository extends JpaRepository<StudentGroup, Long> {
    Optional<StudentGroup> findByGroupNotAndStudent(Group group, Student student);

    Optional<StudentGroup> findByGroupAndStudent(Group group, Student student);
}
package com.example.demo.repository;

import com.example.demo.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query(value = "SELECT _students.created_time as createdAt, _students.full_name as fullName FROM _students WHERE _students.id IN( SELECT _student_group_link.student_id FROM _student_group_link WHERE _student_group_link.group_id = ( SELECT _groups.id FROM _groups WHERE _groups.number = :groupNumber)) ORDER BY fullName COLLATE utf8mb4_unicode_ci;", nativeQuery = true)
    Optional<List<StudentInfo>> getStudentsByGroupNumber(String groupNumber);

    boolean existsByFullName(String s);

    Optional<Student> findByFullName(String fullName);
}
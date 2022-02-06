package com.example.demo.service;

import com.example.demo.dto.StudentDto;
import com.example.demo.entity.Group;
import com.example.demo.entity.Student;
import com.example.demo.entity.StudentGroup;
import com.example.demo.repository.GroupRepository;
import com.example.demo.repository.StudentGroupRepository;
import com.example.demo.repository.StudentInfo;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.exception.GroupException;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    private final StudentGroupRepository studentGroupRepository;

    @CacheEvict(cacheNames = {"studentCache"}, allEntries = true)
    @Transactional
    public void addStudentToGroup(String fullName, String groupNumber) {
        Optional<Group> optionalGroup = groupRepository.findByGroupNumber(groupNumber);
        Group group = optionalGroup.orElseThrow(() -> new GroupException(String.format("Group with number %s not exist", groupNumber)));

        Student student;
        Optional<Student> optionalStudent = studentRepository.findByFullName(fullName);

        if (optionalStudent.isPresent()) {
            student = optionalStudent.get();
        } else {
            student = Student.builder().fullName(fullName).build();
            studentRepository.save(student);
        }

        Optional<StudentGroup> optionalStudentGroups = studentGroupRepository.findByGroupNotAndStudent(group, student);

        optionalStudentGroups.ifPresent(studentGroup -> studentGroup.setStudent(null));

        if (studentGroupRepository.findByGroupAndStudent(group, student).isPresent()) {
            return;
        }

        StudentGroup studentGroup = StudentGroup.builder().group(group).student(student).build();
        studentGroupRepository.save(studentGroup);
    }

    @Cacheable(cacheNames = {"studentCache"})
    @Transactional(readOnly = true)
    public List<StudentDto> getStudentsByGroupNumber(String groupNumber) {
        if (!groupRepository.existsByGroupNumber(groupNumber)) {
            throw new GroupException(String.format("Group with number %s not exist", groupNumber));
        }
        Optional<List<StudentInfo>> optionalStudentInfos = studentRepository.getStudentsByGroupNumber(groupNumber);
        return optionalStudentInfos.map(studentInfos -> studentInfos.stream()
                .map(e -> {
                    Timestamp createdAt = e.getCreatedAt();
                    int micros = createdAt.getNanos() / 1000;
                    String format = String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%2$d", createdAt, micros);
                    return StudentDto.builder()
                            .fullName(e.getfullName())
                            .createdAt(format)
                            .build();
                })
                .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @CacheEvict(cacheNames = {"studentCache"}, allEntries = true)
    @Transactional
    public void deleteStudentByFullName(String fullName) {
        Optional<Student> studentOptional = studentRepository.findByFullName(fullName);
        studentOptional.ifPresent(studentRepository::delete);
    }
}

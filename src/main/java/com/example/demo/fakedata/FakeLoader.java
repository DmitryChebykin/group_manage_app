package com.example.demo.fakedata;

import com.example.demo.entity.Group;
import com.example.demo.entity.Student;
import com.example.demo.entity.StudentGroup;
import com.example.demo.repository.GroupRepository;
import com.example.demo.repository.StudentRepository;
import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class FakeLoader {
    private final StudentRepository studentRepository;

    private final GroupRepository groupRepository;

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    @CacheEvict(cacheNames = {"group"}, allEntries = true)
    public void loadFakeData() {
        Faker faker = new Faker(new Locale("ru-RU"));
        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("ru-RU"), new RandomService());
        Set<String> groupNumbers = new HashSet<>();

        for (int i = 0; i < 1000; i++) {
            groupNumbers.add(fakeValuesService.regexify("[1-9][1-9][1-9]-[1-9][1-9][1-9]"));
        }

        List<Group> groupList = groupNumbers.stream().map(g -> Group.builder().groupNumber(g).build()).collect(Collectors.toList());

        String sql = "INSERT INTO `_groups` (number, created_time, last_modified_time)"
                + " VALUES(?,?,?)";

        List<Group> finalGroupList = groupList;
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Group group = finalGroupList.get(i);
                ps.setString(1, group.getGroupNumber());
                Timestamp x = getTimestamp();
                ps.setTimestamp(2, x);
                ps.setTimestamp(3, x);
            }

            @Override
            public int getBatchSize() {
                return finalGroupList.size();
            }
        });

        Set<String> students = new HashSet<>();

        for (int i = 0; i < 10000; i++) {
            students.add(faker.name().fullName());
        }

        List<Student> studentList = students.stream().map(e -> Student.builder().fullName(e).build()).collect(Collectors.toList());

        sql = "INSERT INTO `_students` (full_name, created_time, last_modified_time)"
                + " VALUES(?,?,?)";

        List<Student> finalStudentList = studentList;

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Student student = finalStudentList.get(i);
                ps.setString(1, student.getFullName());
                Timestamp x = getTimestamp();
                ps.setTimestamp(2, x);
                ps.setTimestamp(3, x);
            }

            @Override
            public int getBatchSize() {
                return finalStudentList.size();
            }
        });

        studentList = studentRepository.findAll();
        long studentsCount = studentList.size();

        groupList = groupRepository.findAll();
        long groupsCount = groupList.size();

        long studentQuantityInGroup = studentsCount / groupsCount;

        List<StudentGroup> studentGroups = new ArrayList<>();

        if (studentQuantityInGroup > 2) {
            for (Group g : groupList) {
                List<Student> savedStudents = studentList.stream().limit(studentQuantityInGroup + faker.number().numberBetween(-2, 2)).collect(Collectors.toList());

                for (Student s : savedStudents) {
                    StudentGroup studentGroup = StudentGroup.builder().group(g).student(s).build();
                    studentGroups.add(studentGroup);
                }

                studentList.removeAll(savedStudents);
            }
        }

        sql = "INSERT INTO `_student_group_link` (group_id, student_id, created_time, last_modified_time)"
                + " VALUES(?,?,?,?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                StudentGroup studentGroup = studentGroups.get(i);
                ps.setString(1, String.valueOf(studentGroup.getGroup().getId()));
                ps.setString(2, String.valueOf(studentGroup.getStudent().getId()));
                Timestamp x = getTimestamp();
                ps.setTimestamp(3, x);
                ps.setTimestamp(4, x);
            }

            @Override
            public int getBatchSize() {
                return studentGroups.size();
            }
        });
    }

    private Timestamp getTimestamp() {
        Timestamp x = new Timestamp(Instant.now().toEpochMilli());
        x.setNanos((int) (System.nanoTime() % 1000000000));
        return x;
    }
}

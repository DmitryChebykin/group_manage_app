package com.example.demo.controller.api;

import com.example.demo.dto.GroupFormDto;
import com.example.demo.dto.StudentDto;
import com.example.demo.dto.StudentFormDto;
import com.example.demo.service.GroupService;
import com.example.demo.service.StudentService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api
@Validated
@RequestMapping("/api")
@RestController
@AllArgsConstructor
public class SimpleRestController {
    private final StudentService studentService;
    private final GroupService groupService;

    @GetMapping("/student/byGroup")
    public ResponseEntity<List<StudentDto>> getStudentsInGroup(@RequestParam String groupNumber) {
        List<StudentDto> studentsByGroupNumber = studentService.getStudentsByGroupNumber(groupNumber);
        return ResponseEntity.ok(studentsByGroupNumber);
    }

    @PostMapping("/student")
    public ResponseEntity<Void> addStudentToGroup(@RequestBody @Valid StudentFormDto studentFormDto) {
        studentService.addStudentToGroup(studentFormDto.getFullName(), studentFormDto.getGroupNumber());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/student/{fullName}")
    public ResponseEntity<Void> deleteStudentByFullName(@PathVariable String fullName) {
        studentService.deleteStudentByFullName(fullName);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/group")
    public ResponseEntity<Void> addGroup(@RequestBody @Valid GroupFormDto groupFormDto) {
        groupService.addGroup(groupFormDto.getGroupNumber());
        return ResponseEntity.ok().build();
    }
}
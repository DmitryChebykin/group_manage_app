package com.example.demo.controller;

import com.example.demo.dto.StudentDto;
import com.example.demo.dto.StudentFormDto;
import com.example.demo.service.GroupService;
import com.example.demo.service.StudentService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class StudentController {
    private final StudentService studentService;
    private final GroupService groupService;

    @PostMapping(value = "/student")
    public String addStudentToGroup(@Valid @ModelAttribute("studentFormDto") StudentFormDto studentFormDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        String groupNumber = studentFormDto.getGroupNumber();

        if (bindingResult.hasErrors()) {
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            redirectAttributes.addFlashAttribute("message", allErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(", ")));
        } else {
            if (!groupService.existsByGroupNumber(groupNumber)) {
                redirectAttributes.addFlashAttribute("message", "Указан неверный номер группы, студент не добавлен!");
            } else {
                studentService.addStudentToGroup(studentFormDto.getFullName(), groupNumber);
            }
        }

        return "redirect:/groups/" + groupNumber;
    }

    @GetMapping(value = "/student/{fullName}")
    public String deleteStudent(@PathVariable String fullName, HttpServletRequest request) {
        studentService.deleteStudentByFullName(fullName);
        String referer = request.getHeader("referer");
        String groupNumber = StringUtils.substringAfterLast(referer, "/groups/");

        return "redirect:/groups/" + groupNumber;
    }

    @GetMapping(value = "/student", produces = "text/html;charset=UTF-8")
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String deleteStudentFromGroup(
            @RequestParam(name = "name") String fullName,
            @RequestParam(name = "group") String groupNumber, HttpServletResponse response) {

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Content-Language", "ru-RU");
        response.setCharacterEncoding("UTF-8");

        studentService.deleteStudentByFullName(fullName);
        List<StudentDto> studentsByGroupNumber = studentService.getStudentsByGroupNumber(groupNumber);
        Map<String, Object> map = new HashMap<>();

        return getTableBodyString(groupNumber, studentsByGroupNumber, map);
    }

    private String getTableBodyString(String groupNumber, List<StudentDto> studentsByGroupNumber, Map<String, Object> map) {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
        configuration.setClassForTemplateLoading(StudentController.class, "/templates");
        Template template;
        Writer out = new StringWriter();

        try {
            template = configuration.getTemplate("partialTable.ftlh");

            map.put("students", studentsByGroupNumber);
            map.put("groupNumber", groupNumber);

            template.process(map, out);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }

        String text = out.toString();

        return text;
    }

    @PostMapping(value = "/add-student", produces = "text/html;charset=UTF-8", consumes = "application/x-www-form-urlencoded")
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String addStudent(@Valid @ModelAttribute("studentFormDto") StudentFormDto studentFormDto, BindingResult bindingResult, HttpServletResponse response) {

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Content-Language", "ru-RU");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> map = new HashMap<>();

        String fullName = studentFormDto.getFullName();

        String groupNumber = studentFormDto.getGroupNumber();

        if (bindingResult.hasErrors()) {
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            map.put("message", allErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(", ")));
        } else {
            if (!groupService.existsByGroupNumber(groupNumber)) {
                map.put("message", "Указан неверный номер группы, студент не добавлен!");
            } else {
                studentService.addStudentToGroup(fullName, groupNumber);
            }
        }

        List<StudentDto> studentsByGroupNumber = studentService.getStudentsByGroupNumber(groupNumber);

        String tableBodyString = getTableBodyString(groupNumber, studentsByGroupNumber, map);
        return tableBodyString;
    }
}

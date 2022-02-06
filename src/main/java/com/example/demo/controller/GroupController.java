package com.example.demo.controller;

import com.example.demo.dto.GroupDto;
import com.example.demo.dto.GroupFormDto;
import com.example.demo.dto.StudentDto;
import com.example.demo.service.GroupService;
import com.example.demo.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@Controller
@AllArgsConstructor
public class GroupController {
    public static final String GROUP_ALREADY_EXIST_TEMPLATE = "Группа с номером \"%s\" уже есть в базе";
    public static final String REDIRECT_TO_GROUPS = "redirect:/groups";
    public static final String MESSAGE = "message";
    private final GroupService groupService;
    private final StudentService studentService;

    @GetMapping(value = "/groups/{number}")
    public ModelAndView showGroup(Model model, @PathVariable String number) {
        HashMap<String, Object> map = new HashMap<>();

        if (!groupService.existsByGroupNumber(number)) {
            map.put("message", "Группы не существеут");
        } else {
            List<StudentDto> studentsByGroupNumber = studentService.getStudentsByGroupNumber(number);
            map.put("students", studentsByGroupNumber);
            map.put("groupNumber", number);
        }

        return new ModelAndView("studentList", map);
    }

    @Cacheable("groups")
    @GetMapping(value = "/groups")
    public String showPagedGroups(Model model, @PageableDefault(sort = {"createdAt"}) Pageable pageable) {
        Page<GroupDto> studentsByGroupNumberPageable = groupService.getGroupDtosByPagingCondition(pageable);

        HashMap<String, Object> map = new HashMap<>();
        map.put("groups", studentsByGroupNumberPageable.getContent());
        map.put("total_page", studentsByGroupNumberPageable.getTotalPages());
        map.put("pageNo", studentsByGroupNumberPageable.getNumber());
        map.put("lastPageNo", studentsByGroupNumberPageable.getTotalPages() - 1);

        model.addAllAttributes(map);

        return "groupList";
    }

    @PostMapping(value = "/groups")
    public String addGroup(@Valid @ModelAttribute("groupFormDto") GroupFormDto groupFormDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(MESSAGE, bindingResult.getFieldError("groupNumber"));
        } else {
            String groupNumber = groupFormDto.getGroupNumber();
            if (groupService.existsByGroupNumber(groupNumber)) {
                redirectAttributes.addFlashAttribute(MESSAGE, String.format(GROUP_ALREADY_EXIST_TEMPLATE, groupNumber));
                redirectAttributes.addFlashAttribute("messageLink", String.format("groups/%s", groupNumber));
            } else {
                groupService.addGroup(groupNumber);
                return "redirect:/groups/" + groupNumber;
            }
        }
        return REDIRECT_TO_GROUPS;
    }
}

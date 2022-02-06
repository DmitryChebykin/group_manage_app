package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class StudentFormDto {
    @NotBlank(message = "Имя студента не может быть пустым")
    private String fullName;
    @NotBlank(message = "Номер группы не может быть пустым")
    private String groupNumber;
}

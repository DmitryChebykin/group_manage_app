package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class GroupFormDto {
    @NotBlank(message = "Номер группы не может быть пустым")
    private String groupNumber;
}

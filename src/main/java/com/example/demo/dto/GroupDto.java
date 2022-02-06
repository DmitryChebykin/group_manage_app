package com.example.demo.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupDto {
    private String groupNumber;
    private Integer studentsQuantity;
}

package com.example.todoservice.dto;
import lombok.Data;

@Data
public class TodoRequest {

    private String title;

    private String description;

    private Long userId;
}

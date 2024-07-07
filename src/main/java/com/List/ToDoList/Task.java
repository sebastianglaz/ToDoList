package com.List.ToDoList;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class Task {
    private Long id;

    @NotBlank(message = "Opis jest obowiązkowy")
    private String description;

    private boolean completed;
    private Date createdAt;
}

package com.todolist.entity;


import com.todolist.model.NamedEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Group extends NamedEntity {

    // Attributes -------------------------------------------------------------
    @Size(max = 500, message = "The description is too long.")
    @NotBlank(message = "The description is required.")
    private String description;

    private LocalDate createdDate;

    private String workSpaceId;
}

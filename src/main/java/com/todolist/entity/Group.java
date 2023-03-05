package com.todolist.entity;


import com.todolist.dtos.ShowTask;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"idGroup"})
public class Group {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long idGroup;

    @Size(max = 50, message = "The name is too long.")
    @NotNull(message = "The name is required.")
    @NotBlank(message = "The name is required.")
    private String name;

    @Size(max = 500, message = "The description is too long.")
    @NotNull(message = "The description is required.")
    @NotBlank(message = "The description is required.")
    private String description;

    private LocalDate createdDate;

    public Group() {
        this.idGroup = 0L;
    }

    public static Group of(String name, String description, LocalDate createdDate) {
        Group group = new Group();
        group.setName(name);
        group.setDescription(description);
        group.setCreatedDate(createdDate);
        return group;
    }


}

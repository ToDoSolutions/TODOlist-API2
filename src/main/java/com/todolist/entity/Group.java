package com.todolist.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Table(name = "group")
@Entity
@Getter
@Setter
public class Group {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id_group")
    private Long idGroup;

    @Column(name = "name")
    @Size(max = 50, message = "The name is too long.")
    private String name;

    @Column(name = "description")
    @Size(max = 500, message = "The description is too long.")
    private String description;

    @Column(name = "created_date")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The createdDate is invalid.")
    private String createdDate;

    public Group() {
        this.idGroup = 0L;
    }

    public static Group of(String name, String description, String createdDate) {
        Group group = new Group();
        group.setName(name);
        group.setDescription(description);
        group.setCreatedDate(createdDate);
        return group;
    }
}

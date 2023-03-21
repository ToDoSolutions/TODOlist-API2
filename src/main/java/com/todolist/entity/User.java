package com.todolist.entity;

import com.todolist.model.NamedEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends NamedEntity {

    // Attributes -------------------------------------------------------------
    @Size(max = 50, message = "The surname is too long.")
    @NotBlank(message = "The surname is required.")
    private String surname;

    @Size(max = 50, message = "The username is too long.")
    @NotBlank(message = "The username is required.")
    private String username;

    @Email(message = "The email is invalid.")
    @NotBlank(message = "The email is required.")
    private String email;

    @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z\\d+&@#/%?=~_|!:,.;]*[-a-zA-Z\\d+&@#/%=~_|]", message = "The avatar is invalid.")
    @NotBlank(message = "The avatar is required.")
    private String avatar;

    @Size(max = 500, message = "The bio is too long.")
    private String bio;

    @Size(max = 50, message = "The location is too long.")
    private String location;

    @Size(max = 50, message = "The password is too long.")
    @NotBlank(message = "The password is required.")
    private String password;

    @Size(max = 50, message = "The token is too long.")
    private String token;

    private String clockifyId;

    // Derived attributes -----------------------------------------------------
    @Transient
    public String getFullName() {
        return getName() + " " + surname;
    }

    // Relationships ----------------------------------------------------------
    @OneToMany(mappedBy = "user")
    private List<Task> tasks;

}

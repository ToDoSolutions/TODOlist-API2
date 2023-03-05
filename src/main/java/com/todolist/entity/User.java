package com.todolist.entity;

import com.todolist.dtos.Status;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;


@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"idUser"})
public class User implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @NotBlank
    @NotNull
    private Long idUser;

    @Size(max = 50, message = "The name is too long.")
    @NotBlank(message = "The name is required.")
    @NotNull(message = "The name is required.")
    private String name;

    @Size(max = 50, message = "The surname is too long.")
    @NotNull(message = "The surname is required.")
    @NotBlank(message = "The surname is required.")
    private String surname;

    @Size(max = 50, message = "The username is too long.")
    @NotNull(message = "The username is required.")
    @NotBlank(message = "The username is required.")
    private String username;

    @Email(message = "The email is invalid.")
    @NotNull(message = "The email is required.")
    @NotBlank(message = "The email is required.")
    private String email;

    @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z\\d+&@#/%?=~_|!:,.;]*[-a-zA-Z\\d+&@#/%=~_|]", message = "The avatar is invalid.")
    @NotNull(message = "The avatar is required.")
    @NotBlank(message = "The avatar is required.")
    private String avatar;

    @Size(max = 500, message = "The bio is too long.")
    private String bio;

    @Size(max = 50, message = "The location is too long.")
    private String location;

    @Size(max = 50, message = "The password is too long.")
    @NotBlank(message = "The password is required.")
    @NotNull(message = "The password is required.")
    private String password;

    @Size(max = 50, message = "The token is too long.")
    @Pattern(regexp = "ghp_[a-zA-Z0-9]{36}", message = "The token is invalid.")
    private String token;

    private String clockifyId;

    public User() {
        this.idUser = 0L;
    }

    public static User of(String name, String surname, String username, String email, String avatar, String bio, String location, String password) {
        User user = new User();
        user.setName(name);
        user.setSurname(surname);
        user.setUsername(username);
        user.setEmail(email);
        user.setAvatar(avatar);
        user.setBio(bio);
        user.setLocation(location);
        user.setPassword(password);
        return user;
    }

    public String getFullName() {
        return name + " " + surname;
    }


}

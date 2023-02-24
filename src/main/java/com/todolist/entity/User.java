package com.todolist.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Table(name = "user")
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"idUser"})
public class User implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id_user")
    private Long idUser;

    @Column(name = "name")
    @Size(max = 50, message = "The name is too long.")
    private String name;

    @Column(name = "surname")
    @Size(max = 50, message = "The surname is too long.")
    private String surname;

    @Column(name = "username")
    @Size(max = 50, message = "The username is too long.")
    private String username;

    @Column(name = "email")
    @Email(message = "The email is invalid.")
    private String email;

    @Column(name = "avatar")
    @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z\\d+&@#/%?=~_|!:,.;]*[-a-zA-Z\\d+&@#/%=~_|]", message = "The avatar is invalid.")
    private String avatar;

    @Column(name = "bio")
    @Size(max = 500, message = "The bio is too long.")
    private String bio;

    @Column(name = "location")
    @Size(max = 50, message = "The location is too long.")
    private String location;

    @Column(name = "password")
    @Size(max = 50, message = "The password is too long.")
    @NotBlank(message = "The password is required.")
    private String password;

    @Column(name = "token")
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

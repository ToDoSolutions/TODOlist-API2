package com.todolist.entity;

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
public class User extends Group implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id_user")
    private Long idUser;

    @Column(name = "name")
    @Size(max = 50, message = "The name is too long.")
    // @NotNull(message = "The name is required.")
    @NotBlank(message = "The name is required.")
    private String name;

    @Column(name = "surname")
    @Size(max = 50, message = "The surname is too long.")
    // @NotNull(message = "The surname is required.")
    @NotBlank(message = "The surname is required.")
    private String surname;

    @Column(name = "username")
    @Size(max = 50, message = "The username is too long.")
    // @NotNull(message = "The username is required.")
    @NotBlank(message = "The username is required.")
    private String username;

    @Column(name = "email")
    @Email(message = "The email is invalid.")
    // @NotNull(message = "The email is required.")
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
    // @NotNull(message = "The password is required.")
    @NotBlank(message = "The password is required.")
    private String password;

    @Column(name = "token")
    @Size(max = 50, message = "The token is too long.")
    private String token;

    public User() {
        this.idUser = 0L;
    }

    public static User of(String name, String surname, String username, String email, String avatar, String bio, String location, String password, String token) {
        User user = new User();
        user.setName(name);
        user.setSurname(surname);
        user.setUsername(username);
        user.setEmail(email);
        user.setAvatar(avatar);
        user.setBio(bio);
        user.setLocation(location);
        user.setPassword(password);
        user.setToken(token);
        return user;
    }
}

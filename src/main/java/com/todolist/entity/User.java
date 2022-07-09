package com.todolist.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;

@Table(name = "user")
@Entity
public class User implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id_user")
    private long idUser;

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

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

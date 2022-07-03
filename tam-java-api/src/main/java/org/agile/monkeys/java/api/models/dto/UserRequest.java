package org.agile.monkeys.java.api.models.dto;


import javax.validation.constraints.*;

public class UserRequest {

    @NotBlank
    @Size(max = 40)
    @Email
    private String email;

    private Boolean isAdmin;

    @NotBlank
    @Size(min = 6)
    @Size(max = 255)
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

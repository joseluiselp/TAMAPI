package org.agile.monkeys.msvc.java.api.users.msvcjavaapiusers.models.dto;

public class AuthenticationResponse {

    private String jwt;

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public AuthenticationResponse(String jwt) {
        this.jwt = jwt;
    }
}
package org.agile.monkeys.java.api.models.dto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

public class CustomerUpdateRequest {

    private String name;

    private String surname;

    private String photoUrl;

    @NotNull
    @Digits(integer = 11, fraction = 0)
    private Long updatedBy;

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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }
}

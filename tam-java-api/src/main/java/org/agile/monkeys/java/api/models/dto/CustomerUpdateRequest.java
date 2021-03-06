package org.agile.monkeys.java.api.models.dto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

public class CustomerUpdateRequest {

    private String name;

    private String surname;

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
}

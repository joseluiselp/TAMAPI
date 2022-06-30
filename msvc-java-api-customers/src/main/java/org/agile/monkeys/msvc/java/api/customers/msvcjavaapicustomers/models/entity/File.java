package org.agile.monkeys.msvc.java.api.customers.msvcjavaapicustomers.models.entity;

import java.io.Serializable;

public class File implements Serializable {
    private String contentType;
    private String name;
    private String bytes;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBytes() {
        return bytes;
    }

    public void setBytes(String bytes) {
        this.bytes = bytes;
    }
}

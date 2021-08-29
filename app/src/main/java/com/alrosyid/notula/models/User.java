package com.alrosyid.notula.models;

public class User {
    private int id;
    private String name, email , name_organization,address_organization;

    public String getName_organization() {
        return name_organization;
    }

    public void setName_organization(String name_organization) {
        this.name_organization = name_organization;
    }

    public String getAddress_organization() {
        return address_organization;
    }

    public void setAddress_organization(String address_organization) {
        this.address_organization = address_organization;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

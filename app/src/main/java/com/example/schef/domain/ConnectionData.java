package com.example.schef.domain;


import java.io.Serializable;

public class ConnectionData implements Serializable {
    private int id;
    private String name;
    private String uri;
    private String password;
    private String customermail;

    public ConnectionData(int id, String name, String uri) {
        this.id = id;
        this.name = name;
        this.uri = uri;
    }

    public ConnectionData(int id, String name, String uri, String password, String customermail) {
        this(id, name, uri);
        this.password = password;
        this.customermail = customermail;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCustomermail() {
        return customermail;
    }

    public void setCustomermail(String customermail) {
        this.customermail = customermail;
    }
}

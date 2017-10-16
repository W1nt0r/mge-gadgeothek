package com.example.schef.domain;


import java.io.Serializable;

public class ConnectionData implements Serializable {
    private int id;
    private String name;
    private String uri;
    private String token;
    private String customerid;
    private String password;
    private String customermail;

    public ConnectionData(int id, String name, String uri) {
        this.id = id;
        this.name = name;
        this.uri = uri;
    }

    public ConnectionData(int id, String name, String uri, String token, String customerid, String password, String customermail) {
        this(id, name, uri);
        this.token = token;
        this.customerid = customerid;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCustomerid() {
        return customerid;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
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

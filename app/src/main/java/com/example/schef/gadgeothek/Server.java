package com.example.schef.gadgeothek;

import java.util.Comparator;

/**
 * Created by reiem on 12.10.2017.
 */

public class Server {

    private int id;
    private String name;
    private String uri;

    public Server(int id, String name, String uri) {
        this.id = id;
        this.name = name;
        this.uri = uri;
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
}

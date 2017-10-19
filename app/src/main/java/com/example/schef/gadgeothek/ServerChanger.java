package com.example.schef.gadgeothek;

import com.example.schef.domain.ConnectionData;

public interface ServerChanger {
    void changeServer(ConnectionData connectionData);
    void addServer();
    void addNewServer();
}

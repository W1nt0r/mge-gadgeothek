package com.example.schef.gadgeothek;

import com.example.schef.domain.ConnectionData;

public interface LoginHandler {
    void registerClick(ConnectionData connectionData);
    void login(ConnectionData connectionData);
    void register(ConnectionData connectionData);
}

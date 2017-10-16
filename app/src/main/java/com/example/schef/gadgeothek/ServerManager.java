package com.example.schef.gadgeothek;

import com.example.schef.domain.ConnectionData;

interface ServerManager {
    void deleteServer(ConnectionData server);
    void chooseServer(ConnectionData server);
}

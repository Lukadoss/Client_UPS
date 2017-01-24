//
// Created by Lukado on 28/10/16.
//

#ifndef UPS_SERVER_PLAYERS_H
#define UPS_SERVER_PLAYERS_H

#include <string>
#include <vector>
#include <thread>

class players {
public:
    struct User {
        //id uživatele
        int uId;
        //jméno uživatele
        std::string name;
        //id herní místnosti
        int roomId;
        //je připraven
        bool isReady;
        //je online
        bool isOnline;
        //vlastněné karty
        std::vector<std::string> cards;
        //poslední čas spojení
        struct timespec lastPing;
        //pozice v socketsetu
        int socketPos;
    };
};

#endif //UPS_SERVER_PLAYERS_H

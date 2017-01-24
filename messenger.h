//
// Created by Lukado on 27/10/16.
//

#ifndef UPS_SERVER_MESSENGER_H
#define UPS_SERVER_MESSENGER_H

#include <string>
#include <sys/socket.h>
#include "players.h"
#include <thread>

class messenger {

public:
    static const char MSG_DELIMITER = ':';

    /**
     * Pošle hráči danou zprávu
     * @param socket id soketu
     * @param message zpráva
     */
    static void sendMsg(int socket, std::string message);

    /**
     * Pošle všem hráčům v místnosti stejnou zprávu
     * @param players pole hráčů
     * @param msg zpáva
     */
    static void sendMsgAll(std::vector<players::User> players, std::string msg);

    /**
     * Pošle zprávu všem hráčům kromě jednoho
     * @param uid id vynechaného soketu
     * @param players pole hráčů
     * @param msg zpráva
     */
    static void sendMsgAllOthers(int uid, std::vector<players::User> players, std::string msg);

    /**
     * Rozdělení příchozí zprávy od klienta do stringů. Rozdělení podle rozdělovacího znaku.
     * @param msg zpráva
     * @return pole stringů
     */
    static std::vector<std::string> splitMsg(std::string msg);

    static std::vector<std::string> splitMsg(std::string msg, char delimiter);

    static void consoleOut(std::string msg);
};


#endif //UPS_SERVER_MESSENGER_H

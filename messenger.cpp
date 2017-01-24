//
// Created by Lukado on 27/10/16.
//
#include <iostream>
#include "messenger.h"

void messenger::sendMsg(int socket, std::string msg) {
    const char *msgChar = msg.c_str();
    std::this_thread::sleep_for(std::chrono::milliseconds(10));
    send(socket, (void *) msgChar, msg.length(), MSG_NOSIGNAL);
    consoleOut("Poslána zpráva "+std::to_string(socket)+": "+msg.replace(msg.find("\n"),1,"\0"));
}

void messenger::sendMsgAll(std::vector<players::User> players, std::string msg) {
    for (int i = 0; i < players.size(); i++) {
        if (players.at(i).uId != 0 && players.at(i).isOnline) {
            sendMsg(players.at(i).uId, msg);
        }
    }
}

void messenger::sendMsgAllOthers(int uid, std::vector<players::User> players, std::string msg) {
    for (int i = 0; i < players.size(); i++) {
        if (players.at(i).uId != 0 && players.at(i).uId != uid && players.at(i).isOnline) {
            sendMsg(players.at(i).uId, msg);
        }
    }
}
std::vector<std::string> messenger::splitMsg(std::string msg){
    return splitMsg(msg, MSG_DELIMITER);
}

std::vector<std::string> messenger::splitMsg(std::string msg, char delimiter) {
    std::string next;
    std::vector<std::string> result;

    for (std::string::const_iterator it = msg.begin(); it != msg.end(); it++) {
        if (*it != delimiter) {
            next += *it;
        } else {
            if (!next.empty()) {
                result.push_back(next);
                next.clear();
            }
        }
    }
    if (!next.empty())
        result.push_back(next);
    return result;
}

void messenger::consoleOut(std::string msg) {
    time_t rawtime;
    struct tm *timeinfo;
    char buffer[80];

    time(&rawtime);
    timeinfo = localtime(&rawtime);

    strftime(buffer, 80, "[%d-%m-%Y %H:%M:%S] ", timeinfo);
    std::string str(buffer);

    std::cout << str << msg << std::endl;
}
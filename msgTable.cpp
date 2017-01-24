//
// Created by Lukado on 22/10/16.
//
#include "msgTable.h"

msgtable::msgTypes msgtable::getType(std::string msg) {
    if (!msg.compare("C_LOGIN")) {
        return msgtable::C_LOGIN;
    } else if (!msg.compare("C_ROOM_INFO")) {
        return msgtable::C_ROOM_INFO;
    } else if (!msg.compare("C_USR_READY")) {
        return msgtable::C_USR_READY;
    } else if (!msg.compare("C_PUT_CARD")) {
        return msgtable::C_PUT_CARD;
    } else if (!msg.compare("C_CHECK_CHEAT")) {
        return msgtable::C_CHECK_CHEAT;
    } else if (!msg.compare("EOS")) {
        return msgtable::EOS;
    } else if (!msg.compare("ERR")) {
        return msgtable::ERR;
    } else if (!msg.compare("PING")){
        return msgtable::PING;
    } else {
        return msgtable::NO_CODE;
    }
}

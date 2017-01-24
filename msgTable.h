//
// Created by Lukado on 22/10/16.
//

#ifndef UPS_SERVER_MSGTABLE_H
#define UPS_SERVER_MSGTABLE_H

#include <string>

class msgtable {
public:
    enum msgTypes {
        C_LOGIN,
        C_USR_READY,
        C_PUT_CARD,
        C_CHECK_CHEAT,
        C_ROOM_INFO,
        EOS,
        ERR,
        NO_CODE,
        PING
    };

    /**
     * Navrací typ zprávy od klienta podle stringu
     * @param msg zpráva
     * @return enum typ
     */
    static msgTypes getType(std::string msg);
};

#endif //UPS_SERVER_MSGTABLE_H

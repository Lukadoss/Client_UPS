//
// Created by Lukado on 27/10/16.
//

#include "gameRoom.h"

gameRoom::gameRoom() {
    roomStatus = RoomStatus::ROOM_WAIT;
    info.isOver = false;
}

int gameRoom::addPlayer(players::User player) {
    if (!playerInOtherRoom(player)) {
        if (!playerAlreadyJoined(player.uId)) {
            if (isRoomWaiting()) {
                player.roomId = roomId;
                player.isOnline = true;
                clock_gettime(CLOCK_MONOTONIC, &player.lastPing);
                users.push_back(player);

                numPlaying++;
                if (numPlaying == maxPlaying) {
                    isFull = true;
                }
                messenger::sendMsg(player.uId, ("S_LOGGED:" + player.name + "#" += '\n'));
                messenger::sendMsgAllOthers(player.uId, users, "S_USR_JOINED:"+ player.name + "#\n");

                return roomId;
            } else return -1;
        } else return -1;
    } else return -1;
}

bool gameRoom::removePlayer(int uId) {
    for (unsigned int i = 0; i < users.size(); i++) {
        if (users.at(i).uId == uId) {
            info.isOver = true;
            std::string name = users.at(i).name;
            users.erase(users.begin() + i);
            numPlaying--;
            isFull = false;
            if (roomStatus == RoomStatus::ROOM_WAIT) {
                messenger::sendMsgAll(users, "S_USR_LEFT:" + name + "#\n");
                allPlayersReady();
            }
            return true;
        }
    }
    return false;
}

bool gameRoom::isRoomWaiting() {
    return roomStatus == RoomStatus::ROOM_WAIT;
}

bool gameRoom::playerInOtherRoom(players::User player) {
    return player.roomId > -1;
}

bool gameRoom::playerAlreadyJoined(int id) {
    for (int i = 0; i < numPlaying; i++) {
        if (id == users.at(i).uId && users.at(i).isOnline) return true;
    }
    return false;
}

void gameRoom::setPlayerReady(int playerId, bool ready) {
    if (roomStatus == RoomStatus::ROOM_WAIT) {
        for (int i = 0; i < numPlaying; i++) {
            if (users.at(i).uId == playerId) {
                if (users.at(i).isReady == ready) {
                    messenger::sendMsg(playerId, "S_CONSOLE_INFO:Uživatel již připraven#\n");
                    break;
                } else {
                    users.at(i).isReady = ready;
                    messenger::sendMsgAllOthers(playerId, users, "S_USR_READY:" + users.at(i).name + "#\n");
                    messenger::sendMsg(playerId, "S_USR_READY_ACK#\n");
                    break;
                }
            }
        }
        allPlayersReady();
    } else {
        messenger::sendMsg(playerId, "S_MSG_NOT_VALID#\n");
    }
}

void gameRoom::setPlayerDc(int playerId) {
    for (int i = 0; i < users.size(); ++i) {
        if (users.at(i).uId == playerId) users.at(i).isOnline = false;
    }
}

void gameRoom::allPlayersReady() {
    int numReady = 0;
    if (numPlaying >= 2) {
        for (int i = 0; i < numPlaying; i++) {
            if (users.at(i).isReady) numReady++;
        }
        if (numReady == numPlaying) {
            consoleOut("Všichni hráči připraveni, hra v místnosti[" + std::to_string(roomId) + "] brzy začne");
            createNewGame();
            roomStatus = RoomStatus::GAME_IN_PROGRESS;
        }
    }
}

void gameRoom::createNewGame() {
    init();
    info.onTurnId = 0;
    info.lastTurnId = 0;
    info.isOver = false;
    gameThread = std::thread(loop, this);
    gameThread.detach();
}

void gameRoom::init() {
    std::string card = "";
    info.cards = std::vector<std::string>();

    for (int i = 0; i < 4; i++) {
        switch (i) {
            case 0:
                card = "R";
                break;
            case 1:
                card = "B";
                break;
            case 2:
                card = "G";
                break;
            case 3:
                card = "K";
                break;
            default:
                break;
        }
        for (int j = 0; j < 7; ++j) {
            info.cards.push_back(card);
        }
    }
    unsigned seed = std::chrono::system_clock::now().time_since_epoch().count();
    shuffle(info.cards.begin(), info.cards.end(), std::default_random_engine(seed));
}

void gameRoom::loop(gameRoom *r) {
    int previousStackNum;
    r->giveCardsToPlayers();
    messenger::sendMsgAll(r->users, "S_CONSOLE_INFO:---GAME_STARTED---#\n");
    previousStackNum = r->info.cards.size();
    std::this_thread::sleep_for(std::chrono::milliseconds(50));
    messenger::sendMsgAll(r->users, "S_ON_TURN:" + r->users.at(r->info.onTurnId).name + ":" +
                                    r->users.at(r->info.lastTurnId).name + ":" +
                                    std::to_string(r->users.at(r->info.lastTurnId).cards.size()) + "#\n");
    while (!r->info.isOver) {
        std::this_thread::sleep_for(std::chrono::milliseconds(20));
        if (previousStackNum < r->info.cards.size()) {
            r->nextPlayer();
            messenger::sendMsgAll(r->users, "S_ON_TURN:" + r->users.at(r->info.onTurnId).name + ":" +
                                            r->users.at(r->info.lastTurnId).name + ":" +
                                            std::to_string(r->users.at(r->info.lastTurnId).cards.size()) + "#\n");
            previousStackNum = r->info.cards.size();
            std::string firstStackCard = "X";
            if(r->info.cards.size()>0){
                firstStackCard = r->info.cards.at(0).c_str();
            }
            messenger::sendMsgAll(r->users, "S_STACK_CARDS:" + std::to_string(r->info.cards.size()) + ":"+ firstStackCard +"#\n");
        }else if(r->info.cards.size() == 0 && previousStackNum != 0) previousStackNum = 0;

        r->checkOnlinePlayers();

    }
    r->consoleOut("Hra v místnosti["+std::to_string(r->roomId)+"] skončila!");
    r->roomStatus = RoomStatus::GAME_END;
    r->clearRoom(r);
}

void gameRoom::clearRoom(gameRoom *r) {
    r->isFull = false;
    r->info.isOver = false;
    r->info.onTurnId = 0;
    r->info.lastTurnId = 0;
    for (int i = 0; i < users.size(); ++i) {
        users.at(i).isReady = false;
        users.at(i).cards.clear();
    }
    while(r->info.cards.size()>0) r->info.cards.pop_back();
    r->roomStatus = RoomStatus::ROOM_WAIT;
}

void gameRoom::giveCardsToPlayers() {
    double cardsforplayer = floor(info.cards.size() / numPlaying);
    for (int i = 0; i < numPlaying; ++i) {
        users.at(i).cards = std::vector<std::string>();
        for (int j = 0; j < cardsforplayer; ++j) {
            std::string karta = info.cards.back();
            users.at(i).cards.push_back(karta);
            info.cards.pop_back();
        }
    }

    for (int i = 0; i < users.size(); ++i) {
        messenger::sendMsg(users.at(i).uId, "S_CARDS_OWNED:" + getPlayerCards(i));
    }
    std::string firstStackCard = "X";
    if(info.cards.size()>0){
        firstStackCard = info.cards.at(0).c_str();
    }
    messenger::sendMsgAll(users, "S_STACK_CARDS:" + std::to_string(info.cards.size()) + ":"+ firstStackCard +"#\n");
}

void gameRoom::placeCard(int id, std::string card) {
    if (roomStatus == RoomStatus::GAME_WAITING) {
        messenger::sendMsg(id, "S_CONSOLE_INFO:Čeká se na reconnect hráče#\n");
        return;
    } else if (roomStatus == RoomStatus::ROOM_WAIT || roomStatus == RoomStatus::GAME_END) {
        messenger::sendMsg(id, "S_MSG_NOT_VALID#\n");
        return;
    }
    for (int i = 0; i < users.size(); ++i) {
        if (users.at(i).uId == id && users.at(i).cards.size() == 0) {
            info.isOver = true;
            info.winner = id;
            messenger::sendMsgAll(users, "S_GAME_WINNER:" + users.at(info.winner).name + "#\n");
            return;
        } else if (users.at(i).uId == id && info.onTurnId == i) {
            for (int j = 0; j < users.at(i).cards.size(); ++j) {
                if (users.at(i).cards.at(j) == card) {
                    info.cards.push_back(users.at(i).cards.at(j));
                    users.at(i).cards.erase(users.at(i).cards.begin() + j);

                    if (users.at(info.lastTurnId).cards.size() == 0) {
                        info.isOver = true;
                        info.winner = info.lastTurnId;
                        messenger::sendMsgAll(users, "S_GAME_WINNER:" + users.at(info.winner).name + "#\n");
                        return;
                    } else if (users.at(i).cards.size() == 0 && info.cards.size() == 1) {
                        info.isOver = true;
                        info.winner = id;
                        messenger::sendMsgAll(users, "S_GAME_WINNER:" + users.at(info.winner).name + "#\n");
                        return;
                    }
                    info.lastTurnId = i;
                    std::this_thread::sleep_for(std::chrono::milliseconds(50));
                    messenger::sendMsg(users.at(info.onTurnId).uId, "S_CARD_ACK:" + info.cards.back() + "#\n");
                    return;
                }
            }
            messenger::sendMsg(users.at(info.onTurnId).uId, "S_NOT_VALID_CARD#\n");
            return;
        }
    }
    messenger::sendMsg(id, "S_MSG_NOT_VALID#\n");
}

void gameRoom::checkTopCard(int id) {
    if (roomStatus == RoomStatus::GAME_IN_PROGRESS) {
        if (info.cards.size() < 2) {
            messenger::sendMsg(id, "S_CONSOLE_INFO:Balíček je prázdný nebo pouze s počáteční kartou#\n");
            return;
        } else {
            for (int i = 0; i < users.size(); ++i) {
                if (users.at(i).uId == id) {
                    messenger::sendMsgAll(users, "S_CHEATED_CARD:"+info.cards.back()+"#\n");
                    if (info.cards.front() == info.cards.back()) takePack(i);
                    else givePackToLast(i);
                    break;
                }
            }
        }
    } else {
        messenger::sendMsg(id, "S_MSG_NOT_VALID#\n");
    }
}

void gameRoom::givePackToLast(int pos) {
    int size = info.cards.size();
    for (int i = 0; i < size; ++i) {
        users.at(info.lastTurnId).cards.push_back(info.cards.back());
        info.cards.pop_back();
    }
    messenger::sendMsg(users.at(info.lastTurnId).uId, "S_CARDS_OWNED:" + getPlayerCards(info.lastTurnId));
    messenger::sendMsgAllOthers(users.at(info.lastTurnId).uId, users,
                                "S_CARDS_NUM_CHANGE:" + users.at(info.lastTurnId).name + ":" +
                                std::to_string(users.at(info.lastTurnId).cards.size()) + "#\n");
    messenger::sendMsgAll(users, "S_CONSOLE_INFO:Hráč " + users.at(info.lastTurnId).name +
                                 " podváděl a bere balíček. Na tahu je hráč " + users.at(pos).name + "#\n");
    info.onTurnId = pos;
    info.lastTurnId = pos;
    if(users.at(info.onTurnId).cards.size()==0){
        info.isOver = true;
        info.winner = info.onTurnId;
        messenger::sendMsgAll(users, "S_GAME_WINNER:" + users.at(info.winner).name + "#\n");
        return;
    }
    messenger::sendMsgAll(users,
                          "S_ON_TURN:" + users.at(info.onTurnId).name + ":" + users.at(info.lastTurnId).name + ":" +
                          std::to_string(users.at(info.lastTurnId).cards.size()) + "#\n");
}

void gameRoom::takePack(int pos) {
    int size = info.cards.size();
    for (int i = 0; i < size; ++i) {
        users.at(pos).cards.push_back(info.cards.back());
        info.cards.pop_back();
    }
    messenger::sendMsg(users.at(pos).uId, "S_CARDS_OWNED:" + getPlayerCards(pos));
    messenger::sendMsgAllOthers(users.at(pos).uId, users, "S_CARDS_NUM_CHANGE:" + users.at(pos).name + ":" +
                                                          std::to_string(users.at(pos).cards.size()) + "#\n");
    info.onTurnId = info.lastTurnId;
    messenger::sendMsgAll(users, "S_CONSOLE_INFO:Hráč " + users.at(info.lastTurnId).name +
                                 " nepodváděl a je na tahu. Balíček bere hráč " + users.at(pos).name + "#\n");

    if(users.at(info.onTurnId).cards.size()==0){
        info.isOver = true;
        info.winner = info.onTurnId;
        messenger::sendMsgAll(users, "S_GAME_WINNER:" + users.at(info.winner).name + "#\n");
        return;
    }
    messenger::sendMsgAll(users,
                          "S_ON_TURN:" + users.at(info.onTurnId).name + ":" + users.at(info.lastTurnId).name + ":" +
                          std::to_string(users.at(info.lastTurnId).cards.size()) + "#\n");
}

void gameRoom::nextPlayer() {
    std::this_thread::sleep_for(std::chrono::milliseconds(100));
    if (users.size() - 1 != info.onTurnId) info.onTurnId++;
    else info.onTurnId = 0;
}

void gameRoom::checkOnlinePlayers() {
    const int MAX_DISC_TIME = 45;

    int dcPlayer = getDcPlayer();

    if (dcPlayer != -1) {
        if (roomStatus != RoomStatus::ROOM_WAIT) {
            roomStatus = RoomStatus::GAME_WAITING;
            startTimer();
            messenger::sendMsgAllOthers(users.at(dcPlayer).uId, users,
                                        "S_DISCONNECT:" + users.at(dcPlayer).name + "#\n");
            while (elapsedTime() < MAX_DISC_TIME) {
                std::this_thread::sleep_for(std::chrono::milliseconds(1));
                if (roomStatus == RoomStatus::GAME_IN_PROGRESS) {
                    messenger::sendMsgAllOthers(users.at(dcPlayer).uId, users,
                                                "S_RECONNECT:" + users.at(dcPlayer).name + "#\n");
                    return;
                }
            }
        }
        for (int i = 0; i < users.size(); ++i) {
            if (!users.at(i).isOnline) {
                removePlayer(users.at(i).uId);
                i--;
            }
        }
        messenger::sendMsgAll(users, "S_GAME_END#\n");
    }
}

int gameRoom::getDcPlayer() {
    for (int i = 0; i < users.size(); ++i)
        if (!users.at(i).isOnline) return i;
    return -1;
}

void gameRoom::reconnect(int socket, int pos) {
    users.at(pos).isOnline = true;
    users.at(pos).uId = socket;
    clock_gettime(CLOCK_MONOTONIC, &users.at(pos).lastPing);
    messenger::sendMsg(socket, "S_LOGGED:" + users.at(pos).name + "#\n");
}

void gameRoom::sendReconnectInfo(int socket, int pos) {
    if (roomStatus == RoomStatus::GAME_WAITING) {
        roomStatus = RoomStatus::GAME_IN_PROGRESS;
        messenger::sendMsg(socket, "S_CARDS_OWNED:" + getPlayerCards(pos));
        std::string firstStackCard = "X";
        if(info.cards.size()>0){
            firstStackCard = info.cards.at(0).c_str();
        }
        messenger::sendMsg(socket, "S_STACK_CARDS:" + std::to_string(info.cards.size()) +":"+firstStackCard+"#\n");
        messenger::sendMsg(socket, "S_ON_TURN:" + users.at(info.onTurnId).name + ":" +
                                   users.at(info.lastTurnId).name + ":" +
                                   std::to_string(users.at(info.lastTurnId).cards.size()) + "#\n");
    }
}

std::string gameRoom::getPlayerCards(int i) {
    std::string cards = "";
    for (int j = 0; j < users.at(i).cards.size(); ++j) {
        cards += (users.at(i).cards.at(j) + ":");
    }
    cards.pop_back();
    return cards + "#\n";
}

void gameRoom::consoleOut(std::string msg) {
    time_t rawtime;
    struct tm *timeinfo;
    char buffer[80];

    time(&rawtime);
    timeinfo = localtime(&rawtime);

    strftime(buffer, 80, "[%d-%m-%Y %H:%M:%S] ", timeinfo);
    std::string str(buffer);

    std::cout << str << msg << std::endl;
}

void gameRoom::startTimer() {
    clock_gettime(CLOCK_MONOTONIC, &this->startTime);
}

double gameRoom::elapsedTime() {
    clock_gettime(CLOCK_MONOTONIC, &this->finishTime);
    elapsed = (finishTime.tv_sec - startTime.tv_sec);
    elapsed += (finishTime.tv_nsec - startTime.tv_nsec) / 1000000000.0;
    return elapsed;
}
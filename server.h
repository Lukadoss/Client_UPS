//
// Created by Lukado on 20. 10. 2016.
//
#ifndef UPS_SERVER_SERVER_H
#define UPS_SERVER_SERVER_H

#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <iostream>
#include <string.h>
#include <unistd.h>
#include <stdlib.h>
#include "gameRoom.h"
#include "server.h"
#include "msgTable.h"

#define QUEUE 5

class server {
    //Port serveru
    int serverPort;
    //Plny server
    bool serverFull;

    //Soket serveru
    int sockfd;
    //Pocet prihlasenych uzivatelu
    int connectedUsers;
    //Nevyssi cislo soketu v soketsetu
    int max_socketDesc;
    //Soket klienta
    int sd;
    //Pole klientskych soketu
    int *clientSockets;
    //Pomocna promenna pro select
    int activity;

    //Momentalni pozice v socketSetu
    int curPos;

    //Soket set
    fd_set socketSet;

    //Vlakno pro spamovani hracu pingem
    std::thread pingThread;

    //IP adresa
    struct sockaddr_in sockAddr;

    //Pole herních místností
    std::vector<gameRoom *> gameRooms;
public:
    /**
     * Nastavení nového serveru se základním nastavením. Localhost - port 2222 - 2 herní místnosti - 7 hráčů v místnosti
     */
    server();

    /**
     * Nastavení nového serveru se zadanými parametry
     * @param ipAddr IP adresa
     * @param port port serveru
     * @param maxrooms počet herních místností
     * @param maxconn počet hráčů v místnosti
     */
    server(std::string ipAddr, int port, int maxrooms, int maxconn);

    /**
     *  Spouští server a hlavní cyklus pro zpracovávání zpráv
     */
    void start();

    /**
     * Výpis zpráv do konzole, používané pro debug.
     * @param msg zpráva
     */
    static void consoleOut(std::string msg);

    /**
     * Zpracování příchozí zprávy od klienta
     * @param socket id soketu
     * @return zpráva
     */
    std::vector<std::string> receiveMsg(int socket);

    /**
     * Přihlášení nového uživatele
     * @param socket id soketu
     * @param name jméno klienta
     * @return ano/ne
     */
    bool loginUsr(int socket, std::string name);

    /**
     * Test existence jména na serveru
     * @param name jméno
     * @return ano/ne
     */
    bool nameAvailable(std::string name);

    /**
     * Odhlášení uživatele
     * @param socket id soketu
     * @param i index v soketsetu
     */
    void logoutUsr(int socket);

    /**
     * Vyslání zpráv o uživatelích v herní místnosti
     * @param socket id soketu
     */
    void sendRoomInfo(int socket);

    /**
     * Nastaví uživatele jako připraveného na hru
     * @param playerId id soketu
     */
    void setUsrReady(int playerId);

    /**
     * Přidá uživatele do volné místnosti
     * @param player struktura uživatele
     * @return ano/ne
     */
    bool assignUsrToRoom(players::User player);

    /**
     * Validace hráče, zda je na tahu, jinak pošle nevalidní zprávu.
     * @param sd id soketu
     * @param card barva karty
     */
    void isOnTurn(int sd, std::string card);

    /**
     * Validace existence hráče pro možnost zkontrolování podvodu
     * @param sd id soketu
     */
    void checkCheat(int sd);

    /**
     * Navrací strukturu online hráče podle id soketu
     * @param id id soketu
     * @return hráč
     */
    players::User getUserById(int id);

    /**
     * Validace podle jména zda je hráč online
     * @param name jméno
     * @return ano/ne
     */
    bool userIsDced(std::string name);

    /**
     * Validace existence hráče v herních místnostech
     * @param sd id soketu
     * @return ano/ne
     */
    bool checkPlayer(int sd);

    /**
     * Odpovídá na ping od hráčů, udržuje spojení. Přeruší spojení v případě timeoutu.
     * @param sd id soketu
     */
    void pingBack(int sd);

    /**
     * Loop v novým vlákně pro odchytávání nereagujících hráčů
     * @param srv instance serveru
     */
    static void startPinging(server* srv);
};

#endif //UPS_SERVER_SERVER_H

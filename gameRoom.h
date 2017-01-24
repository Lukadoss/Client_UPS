//
// Created by Lukado on 27/10/16.
//

#ifndef UPS_SERVER_GAMEROOM_H
#define UPS_SERVER_GAMEROOM_H

#include <vector>
#include <string>
#include <thread>
#include <algorithm>
#include <chrono>
#include "players.h"
#include "messenger.h"
#include <iostream>

class server;

class gameRoom {
public:
    /**
     * Nastavení konfigurace počáteční herní místnosti
     */
    gameRoom();

    /**
     * Informace o probíhajícím stavu hry
     */
    struct gameInfo {
        int onTurnId;
        int lastTurnId;
        int winner;
        bool isOver;
        std::vector<std::string> cards;
    } info;

    //Unikátní id herní místnosti
    int roomId;
    //Jméno herní místnosti
    std::string roomName;
    //Pole hráčů v místnosti
    std::vector<players::User> users;
    //Počet aktuálních hráčů v místnosti
    unsigned long numPlaying;
    //Maximální počet hráčů v místnosti
    unsigned long maxPlaying;
    //Plná místnost
    bool isFull;

    /**
     * Momentální status místnosti
     */
    enum RoomStatus {
        ROOM_WAIT,
        GAME_IN_PROGRESS,
        GAME_END,
        GAME_WAITING
    } roomStatus;

    /**
     * Přidá hráče do místnosti pokud je volná
     * @param player hráč
     * @return id místnosti
     */
    int addPlayer(players::User player);

    /**
     * Odstraní hráče z místnosti
     * @param uId id hráče
     * @return ano/ne
     */
    bool removePlayer(int uId);

    /**
     * Test zda-li je hra spuštěná
     * @return ano/ne
     */
    bool isRoomWaiting();

    /**
     * Test zda-li neexistuje hráč v jiné místnosti
     * @param player hráč
     * @return ano/ne
     */
    bool playerInOtherRoom(players::User player);

    /**
     * Test zda-li je hráč již v místnosti připojen
     * @param id id hráče
     * @return ano/ne
     */
    bool playerAlreadyJoined(int id);

    /**
     * Hráč připraven na hru
     * @param playerId id hráče
     * @param ready
     */
    void setPlayerReady(int playerId, bool ready);

    /**
     * Nastavení hráči ztrátu spojení
     * @param playerId id hráče
     */
    void setPlayerDc(int playerId);

    /**
     * Test připravenosti všech hráčů v místnosti. Spouští hru pokud jsou všichni připraveni.
     */
    void allPlayersReady();

    /**
     * Nastavení konfigurace nové hry, zamíchání karet.
     */
    void createNewGame();

    /**
     * Vyčištění a reset parametrů herní místnosti pro novou hru.
     * @param r herní místnost
     */
    void clearRoom(gameRoom *r);

    /**
     * Vložení nové karty do balíčku od hráče na tahu.
     * @param id id hráče
     * @param string barva karty
     */
    void placeCard(int id, std::string string);

    /**
     * Prohlídnutí vrchní karty hráčem. Testování podvodu.
     * @param id id hráče
     */
    void checkTopCard(int id);

    /**
     * Dává všechny karty z balíčku hráči, který byl poslední na tahu.
     * @param pos pozice hráče v poli hráčů místnosti, který prohlíží vršek balíčku
     */
    void givePackToLast(int pos);

    /**
     * Dává všechny karty z balíčku hráči, který prohlížel vršek balíčku
     * @param pos pozice hráče v poli hráčů místnosti, který prohlíží vršek balíčku
     */
    void takePack(int pos);

    /**
     * Nastaví status znovu přihlášného uživatele a herní místnosti. Vyšle zprávu o přihlášení do místnosti.
     * @param socket id hráče
     * @param pos pozice hráče v poli hráčů místnosti
     */
    void reconnect(int socket, int pos);

    /**
     * Vyšle informace o herní místnosti a aktuálním stavu hry znovupřipojenému hráči
     * @param socket id hráče
     * @param pos pozice hráče v poli hráčů místnosti
     */
    void sendReconnectInfo(int socket, int pos);

private:
    //Timery
    struct timespec startTime, finishTime;
    double elapsed;

    //Vlákno pro probíhající hru
    std::thread gameThread;

    /**
     * Nekonečná smyčka pro probíhající hru v novém vlákně
     * @param r herní místnost
     */
    static void loop(gameRoom *r);

    /**
     * Zamíchání karet a vložení do balíčku
     */
    void init();

    /**
     * Rozházení karet rovnoměrně všem hráčům na začátku hry
     */
    void giveCardsToPlayers();

    /**
     * Přepnutí hráče na tahu na dalšího
     */
    void nextPlayer();

    /**
     * Metoda testující zda jsou hráči online. Obsahuje časovou smyčku v případě ztráty spojení
     */
    void checkOnlinePlayers();

    /**
     * Navrací id hráče, který není online
     * @return id hráče
     */
    int getDcPlayer();

    /**
     * Výpis do konzole. Používané pro debug.
     * @param msg zpráva
     */
    void consoleOut(std::string msg);

    /**
     * String všech karet, který daný hráč vlastní
     * @param i pozice hráče v poli hráčů místnosti
     * @return
     */
    std::string getPlayerCards(int i);

    /**
     * Zapíná časovač pro disconnect
     */
    void startTimer();

    /**
     * Čas, který uběhl od doby spuštění časovače
     * @return double ča
     */
    double elapsedTime();
};

#endif //UPS_SERVER_GAMEROOM_H

package Trick.TCPClient;

import Trick.Controller.LoginController;
import Trick.Controller.GameController;
import Trick.Main;
import javafx.application.Platform;

import java.io.IOException;

public class ClientListener implements Runnable {
    private TCP tcpInfo;
    private LoginController loginController;
    private GameController gameController;
    private boolean ClientListenerRunning = true;

    public ClientListener(TCP tcpInfo) {
        this.tcpInfo = tcpInfo;
        Main.tcpi = tcpInfo;
        loginController = Main.FXMLLOADER_LOGIN.getController();
    }

    @Override
    public void run() {
        if (!ClientListenerRunning) {
            if (tcpInfo != null) {
                try {
                    tcpInfo.getSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        while (ClientListenerRunning) {
            try {
                String message = tcpInfo.receiveMsg();
                if (message != null && !message.equals("")) {
                    processMessage(message);
                } else {
                    if (Main.parentWindow!=null) {
                        LoginController l = Main.FXMLLOADER_LOGIN.getController();
                        l.setDiscLoginUi();
                    }
                    ClientListenerRunning = false;
                }
            } catch (Exception ex) {
                try {
                    tcpInfo.getSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ex.printStackTrace();
                //TODO
            }
        }
    }

    private void processMessage(String message) {
        message = message.replace("#", "");
        String[] splittedMsg = message.split(":");

        switch (splittedMsg[0]) {
            case "S_NICK_LEN":
                loginController.setStatusText("Zadejte jméno v rozmezí 3-15 znaků", 3000);
                loginController.resetTCP();
                ClientListenerRunning = false;
                break;
            case "S_LOGGED":
                loginController.setLobbyUi();
                Main.userName = splittedMsg[1];
                break;
            case "S_NAME_EXISTS":
                loginController.setStatusText("Uživatel se jménem " + splittedMsg[1] + " již existuje", 3000);
                break;
            case "S_SERVER_FULL":
                loginController.setStatusText("Server je plný", 3000);
                break;
            case "S_JOIN_ERR":
                gameController.setStatusText("Připojení k místnosti " + splittedMsg[1] + " se nezdařilo", true);
                break;
            case "S_ROOM_INFO":
                gameController.addNewUser(Integer.parseInt(splittedMsg[1]), splittedMsg[2], Integer.parseInt(splittedMsg[3]));
                break;
            case "S_USR_JOINED":
//                gameController.console.setText(gameController.console.getText()+"Nový hráč: \""+splittedMsg[2]+"\"\n");
                gameController.addNewUser(Integer.parseInt(splittedMsg[1])-1, splittedMsg[2], 0);
                break;
            case "S_USR_LEFT":
                gameController.removeUser(splittedMsg[1]);
                break;
            case "S_USR_READY":
//                gameController.console.setText(gameController.console.getText()+"Hráč: "+splittedMsg[1]+" je připraven\n");
                gameController.updateUserReady(splittedMsg[1]);
                break;
            case "S_USR_READY_ACK":
//                gameController.console.setText(gameController.console.getText()+"Jsi připraven!\n");
                gameController.setReady();
                break;
            case "S_CONSOLE_INFO":
                if (gameController!= null) gameController.writeToConsole(splittedMsg[1]);
                break;
            case "S_ROOM_READY":
                break;
            case "S_CARDS_OWNED":
                gameController.readyTable(splittedMsg);
                break;
            case "S_ON_TURN":
                gameController.setOnTurn(splittedMsg[1], Integer.parseInt(splittedMsg[2]));
                break;
            case "S_CARD_LOST":
                gameController.lostCard(splittedMsg[1]);
                break;
//            case "S_USR_NREADY":
//                gameLobbyController = Main.FXMLLOADER_GAMELOBBY.getController();
//                gameLobbyController.updateUserReadyUi(Integer.parseInt(splittedMsg[2]), false);
//                if (splittedMsg[1].equals("1")) {
//                    gameLobbyController.unsetReadyBtn();
//                }
//                break;
//            case "S_ROOM_READY":
//                try {
//                    Main.clientInfo.setRoomIndex(Integer.parseInt(splittedMsg[2]));
//                    gameLobbyController = Main.FXMLLOADER_GAMELOBBY.getController();
//                    gameLobbyController.appendSrvrMsg("Všichni hráči připraveni, hra začíná!");
//                    gameLobbyController.setGameScene();
//                } catch (IOException e) {
//                    //TODO
//                }
//                break;
//            case "S_CHAT_USR":
//                if (splittedMsg[3].equals("0")) {
//                    gameLobbyController = Main.FXMLLOADER_GAMELOBBY.getController();
//                    gameLobbyController.appendUsrMsg(splittedMsg[1], splittedMsg[2]);
//                    break;
//                } else if (splittedMsg[3].equals("1")) {
//                    gameController = Main.FXMLLOADER_GAME.getController();
//                    gameController.appendUsrMsg(splittedMsg[1], splittedMsg[2]);
//                    break;
//                }
//            case "S_TIME_NOTIFY":
//                    gameController = Main.FXMLLOADER_GAME.getController();
//                    gameController.appendSrvrMsg(gameController.getUserName(Integer.parseInt(splittedMsg[1])) + ", jsi na tahu. Zbývá ti 10 vteřin.");
//                    break;
//            case "S_TURNED":
//                gameController = Main.FXMLLOADER_GAME.getController();
//                gameController.flipCard(Integer.parseInt(splittedMsg[1]), Integer.parseInt(splittedMsg[2]), Integer.parseInt(splittedMsg[3]));
//                break;
//            case "S_ON_TURN":
//                gameController = Main.FXMLLOADER_GAME.getController();
//                gameController.updateOnTurn(Integer.parseInt(splittedMsg[1]));
//                break;
//            case "S_NON_TURN":
//                gameController = Main.FXMLLOADER_GAME.getController();
//                gameController.setStatusText("Nejsi na tahu! počkej až protihráč dokončí svůj tah.", false);
//                break;
//            case "S_TIME":
//                gameController = Main.FXMLLOADER_GAME.getController();
//                gameController.updateTurnWait();
//                break;
//            case "S_SCORED":
//                gameController = Main.FXMLLOADER_GAME.getController();
//                gameController.playerScored(Integer.parseInt(splittedMsg[1]), Integer.parseInt(splittedMsg[2]), Integer.parseInt(splittedMsg[3]), Integer.parseInt(splittedMsg[4]), Integer.parseInt(splittedMsg[5]), Integer.parseInt(splittedMsg[6]));
//                break;
//            case "S_TURNBACK":
//                gameController = Main.FXMLLOADER_GAME.getController();
//                gameController.flipBack(Integer.parseInt(splittedMsg[1]), Integer.parseInt(splittedMsg[2]), Integer.parseInt(splittedMsg[3]), Integer.parseInt(splittedMsg[4]));
//                break;
//            case "S_GAME_END":
//                gameController = Main.FXMLLOADER_GAME.getController();
//                if (splittedMsg[1].equals("0")) {
//                    gameController.gameEnd(Integer.parseInt(splittedMsg[2]), Integer.parseInt(splittedMsg[3]), Integer.parseInt(splittedMsg[4]));
//                } else {
//                    gameController.gameEnd(Integer.parseInt(splittedMsg[2]), 0, Integer.parseInt(splittedMsg[3]));
//                }
//                break;
        }
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }
}


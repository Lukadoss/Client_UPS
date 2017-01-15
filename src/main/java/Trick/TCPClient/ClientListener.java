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
            if (tcpInfo == null) {
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
                loginController.setStatusText("Zadejte jméno v rozmezí 3-15 znaků", 1000);
                loginController.resetTCP();
                ClientListenerRunning = false;
                break;
            case "S_LOGGED":
                loginController.setLobbyUi();
                Main.userName = splittedMsg[1];
                break;
            case "S_NAME_EXISTS":
                loginController.setStatusText("Uživatel se jménem " + splittedMsg[1] + " již existuje", 1000);
                break;
            case "S_SERVER_FULL":
                loginController.setStatusText("Server je plný", 1000);
                break;
            case "S_JOIN_ERR":
                gameController.setStatusText("Připojení k místnosti " + splittedMsg[1] + " se nezdařilo", true);
                break;
            case "S_ROOM_INFO":
                gameController.writeToConsole(message);
                gameController.addNewUser(splittedMsg[1], Integer.parseInt(splittedMsg[2]), Integer.parseInt(splittedMsg[3]));
                break;
            case "S_USR_JOINED":
                gameController.writeToConsole(message);
                gameController.addNewUser(splittedMsg[1], 0, 0);
                break;
            case "S_USR_LEFT":
                gameController.writeToConsole(message);
                gameController.removeUser(splittedMsg[1]);
                break;
            case "S_USR_READY":
                gameController.writeToConsole(message);
                gameController.updateUserReady(splittedMsg[1]);
                break;
            case "S_USR_READY_ACK":
                gameController.writeToConsole(message);
                gameController.setReady();
                break;
            case "S_CONSOLE_INFO":
                if (gameController!= null) gameController.writeToConsole(splittedMsg[1]);
                break;
            case "S_CARDS_OWNED":
                gameController.writeToConsole(message);
                gameController.readyTable(splittedMsg);
                break;
            case "S_STACK_CARDS":
                gameController.writeToConsole(message);
                gameController.readyPlayground(Integer.parseInt(splittedMsg[1]), splittedMsg[2]);
                break;
            case "S_ON_TURN":
                gameController.writeToConsole(message);
                gameController.setOnTurn(splittedMsg[1], splittedMsg[2], Integer.parseInt(splittedMsg[3]));
                break;
            case "S_CARD_ACK":
                gameController.writeToConsole(message);
                gameController.lostCard(splittedMsg[1]);
                break;
            case "S_CARDS_NUM_CHANGE":
                break;
            case "S_GAME_WINNER":
                break;
            case "S_GAME_END":
                break;
            case "S_DISCONNECT":

                break;
            case "S_RECONNECT":
                break;
            default:
                gameController.console.setText(gameController.console.getText()+splittedMsg[0]+"\n");
                break;
        }
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }
}


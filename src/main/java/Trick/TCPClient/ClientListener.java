package Trick.TCPClient;

import Trick.Controller.LoginController;
import Trick.Controller.GameController;
import Trick.Main;
import javafx.application.Platform;

import java.io.IOException;
import java.util.Arrays;

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
            }
            if (!ClientListenerRunning) {
                if (tcpInfo != null) {
                    try {
                        tcpInfo.getSocket().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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
                loginController.resetTCP();
                ClientListenerRunning = false;
                break;
            case "S_SERVER_FULL":
                loginController.setStatusText("Server je plný", 1000);
                loginController.resetTCP();
                ClientListenerRunning = false;
                break;
            case "S_JOIN_ERR":
                loginController.setStatusText("Připojení k místnosti se nezdařilo", 1000);
                loginController.resetTCP();
                ClientListenerRunning = false;
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
                gameController.writeToConsole(message);
                gameController.setCheater(splittedMsg[1], Integer.parseInt(splittedMsg[2]));
                break;
            case "S_CHEATED_CARD":
                gameController.writeToConsole(message);
                gameController.setCheatedCard(splittedMsg[1]);
                break;
            case "S_GAME_WINNER":
                gameController.writeToConsole(message);
                gameController.gameEnd(splittedMsg[1]);
                break;
            case "S_GAME_END":
                gameController.writeToConsole(message);
                gameController.gameEnd("");
                break;
            case "S_DISCONNECT":
                gameController.writeToConsole(message);
                gameController.setDisconnected(splittedMsg[1]);
                break;
            case "S_RECONNECT":
                gameController.writeToConsole(message);
                gameController.setReconnected(splittedMsg[1]);
                break;
            default:
                if (gameController==null){
                    loginController.resetTCP();
                    ClientListenerRunning = false;
                }
                System.out.println("Chybna zprava: "+message);
//                gameController.console.setText(splittedMsg[0]+"\n"+gameController.console.getText());
                break;
        }
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }
}


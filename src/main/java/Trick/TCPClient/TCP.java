package Trick.TCPClient;

import Trick.Controller.LoginController;
import Trick.Main;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class TCP {
    private Socket socket;

    public TCP(InetAddress serverIP, int serverPort) {
        try {
            socket = new Socket(serverIP, serverPort);
        } catch (IOException e) {
            LoginController controller = Main.FXMLLOADER_LOGIN.getController();
            controller.setStatusText("Připojení k serveru " + serverIP + ":" + serverPort + " se nezdařilo", 3000);
        } catch (IllegalArgumentException e) {
            LoginController controller = Main.FXMLLOADER_LOGIN.getController();
            controller.setStatusText("Nesprávné číslo portu: číslo v rozmezí 0 - 65535", 3000);
        } catch (NullPointerException e) {
            LoginController controller = Main.FXMLLOADER_LOGIN.getController();
            controller.setStatusText("Hostitelský server nerozpoznán", 3000);
        }
    }

    public void loginUser(String name) {
        String connString = MsgTables.getType(MsgTypes.C_LOGIN) + ":" + name + "#";
        sendMsg(connString);
    }

    public void getRoomInfo(){
        String roomInfo = MsgTables.getType(MsgTypes.C_ROOM_INFO) + "#";
        sendMsg(roomInfo);
    }

    public void userReady() {
        String connString = MsgTables.getType(MsgTypes.C_USR_READY) + "#";
        sendMsg(connString);
    }

    public void putCard(String card) {
        String connString = MsgTables.getType(MsgTypes.C_PUT_CARD) + ":"+card+"#";
        sendMsg(connString);
    }

    public void turnAck(String roomId) {
        String connString = MsgTables.getType(MsgTypes.C_TURN_ACK) + ":" + roomId + "#";
        sendMsg(connString);
    }

    public void sendMsg(String data) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    if (socket != null) {
                        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                        dataOutputStream.write(data.getBytes());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public String receiveMsg() {
        try {
            if (socket != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String buffer;
                String msg = "";
                if ((buffer = br.readLine()) != null) {
                    msg += buffer;
                    if (msg.contains("#")) {
                        return msg;
                    }
                } else {
                    br.close();
                    return null;
                }

            } else {
                return null;
            }
        } catch (IOException e) {
            //e.printStackTrace();
            return null;
        }
        return null;
    }

    public Socket getSocket(){
        return socket;
    }
}

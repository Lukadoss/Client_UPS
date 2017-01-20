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
        Thread thread = new Thread(() -> {
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
        });
        thread.start();
        try{
            thread.join(1000);
        } catch (InterruptedException e) {
            System.out.println("Padlo to širďo");
            e.printStackTrace();
        }
    }

    public void loginUser(String name) {
        String connString = MsgTables.getType(MsgTypes.C_LOGIN) + ":" + name + "#";
        sendMsg(connString);
        System.out.println(connString);
    }

    public void getRoomInfo(){
        String roomInfo = MsgTables.getType(MsgTypes.C_ROOM_INFO) + "#";
        sendMsg(roomInfo);
        System.out.println(roomInfo);

    }

    public void userReady() {
        String usrrdy = MsgTables.getType(MsgTypes.C_USR_READY) + "#";
        sendMsg(usrrdy);
        System.out.println(usrrdy);

    }

    public void putCard(String card) {
        String cardplace = MsgTables.getType(MsgTypes.C_PUT_CARD) + ":"+card+"#";
        sendMsg(cardplace);
        System.out.println(cardplace);

    }

    public void checkCheat() {
        String cheater = MsgTables.getType(MsgTypes.C_CHECK_CHEAT) +"#";
        sendMsg(cheater);
        System.out.println(cheater);
    }

    private void sendMsg(String data) {
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

package Trick.TCPClient;

public class MsgTables {
    public static String getType(MsgTypes msgType) {
        switch (msgType) {
            case C_LOGIN:
                return "C_LOGIN";
            case C_USR_READY:
                return "C_USR_READY";
            case C_TURN_CARD:
                return "C_TURN_CARD";
            case C_TURN_ACK:
                return "C_TURN_ACK";
            case C_ROOM_INFO:
                return "C_ROOM_INFO";
            case C_PUT_CARD:
                return "C_PUT_CARD";
            case C_CHECK_CHEAT:
                return "C_CHECK_CHEAT";
            default:
                return "";
        }
    }
}



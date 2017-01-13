package Trick.Controller;

import Trick.Main;
import Trick.TCPClient.MsgTables;
import Trick.TCPClient.TCP;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Created by seda on 28/10/16.
 */
public class GameController implements Initializable {
    private TCP tcpConn;
    private boolean onTurn = false;
    private int r,g,b,k;

    @FXML
    public Pane mainGamePane;
    @FXML
    public Text statusText;
    @FXML
    public GridPane serverLobbyPane;
    @FXML
    public Button ready;
    @FXML
    public Text player, player1, player2, player3, player4, player5, player6, player7, playerr, player1r, player2r, player3r, player4r, player5r, player6r, player7r;
    @FXML
    public TextArea console;
    @FXML
    public VBox vboxUI;
    @FXML
    public HBox hboxUI, hboxCards;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.tcpConn = Main.tcpi;
        tcpConn.getRoomInfo();
        player.setText(Main.userName);
    }

    public void setStatusText(final String text, final boolean err) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (err) {
                    statusText.setFill(Color.RED);
                } else {
                    statusText.setFill(Color.BLACK);
                }
                statusText.setText(text);
                Thread timedText = new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            statusText.setText("");
                        } catch (InterruptedException e) {
                        }
                    }
                };
                timedText.start();
            }
        });

    }

    @FXML
    public void addNewUser(final int userIndex, final String name, final int ready) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
//                for (Node pn:hboxUI.getChildren()) {
//                    if (pn instanceof Pane){
//                        Node in = ((Pane) pn).getChildren().get(0);
//                        if (in instanceof Text){
//                            if (((Text) in).getText().equals("")){
//                                Node rdy = ((Pane) pn).getChildren().get(0);
//                                ((Text) rdy).setText(name);
//
//                                rdy = ((Pane) pn).getChildren().get(2);
//                                ((Text) rdy).setText("Nepřipraven");
//                                ((Text) rdy).setFill(Color.RED);
//                                break;
//                            }
//                        }
//                    }
//                }
                Text userName = new Text();
                userName.setText(name);
                userName.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, 25));
                userName.setLayoutY(60);
                userName.setTextAlignment(TextAlignment.CENTER);
                userName.setWrappingWidth(116);

                Text userReady = new Text();
                if(ready!=1) {
                    userReady.setText("Nepřipraven");
                    userReady.setFill(Color.RED);
                }
                else {
                    userReady.setText("Připraven");
                    userReady.setFill(Color.GREEN);
                }
                userReady.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));
                userReady.setLayoutY(125);
                userReady.setTextAlignment(TextAlignment.CENTER);
                userReady.setWrappingWidth(116);

                Text cardNum = new Text();
                cardNum.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));
                cardNum.setLayoutY(100);
                cardNum.setWrappingWidth(116);
                cardNum.setTextAlignment(TextAlignment.CENTER);
                cardNum.setText("Počet karet:");

                Line line = new Line();
                line.setLayoutX(116);
                line.setStartY(163);
                Line line1 = new Line();
                line1.setLayoutX(0);
                line1.setStartY(163);

                Pane pane = new Pane();
                pane.getChildren().add(0, userName);
                pane.getChildren().add(1, cardNum);
                pane.getChildren().add(2, userReady);
                pane.getChildren().add(3, line);
                pane.getChildren().add(4, line1);
                pane.setPrefHeight(156);
                pane.setPrefWidth(116);
                hboxUI.getChildren().add(userIndex, pane);
            }
        });
    }

    @FXML
    public void removeUser(String name) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int x=0;
                for (Node pn:hboxUI.getChildren()) {
                    if (pn instanceof Pane){
                        Node in = ((Pane) pn).getChildren().get(0);
                        if (in instanceof Text){
                            if (((Text) in).getText().equals(name)){
                                hboxUI.getChildren().remove(pn);
//                                Node rdy = ((Pane) pn).getChildren().get(0);
//                                ((Text) rdy).setText("");
//                                rdy = ((Pane) pn).getChildren().get(1);
//                                ((Text) rdy).setText("");
//                                rdy = ((Pane) pn).getChildren().get(2);
//                                ((Text) rdy).setText("");
                                break;
                            }
                            x++;
                        }
                    }
                }
            }
        });
    }

    public void updateUserReady(String name) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (Node pn:hboxUI.getChildren()) {
                    if (pn instanceof Pane){
                        Node in = ((Pane) pn).getChildren().get(0);
                        if (in instanceof Text){
                            if (((Text) in).getText().equals(name)){
                                Node rdy = ((Pane) pn).getChildren().get(2);
                                if (rdy instanceof Text){
                                    ((Text) rdy).setText("Připraven");
                                    ((Text) rdy).setFill(Color.GREEN);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    public void setUserReady(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tcpConn.userReady();
                ready.setDisable(true);
            }
        });
    }

    public void setReady() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                playerr.setText("Připraven");
                playerr.setFill(Color.GREEN);
            }
        });
    }

    public void readyTable(String[] splittedMsg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                r=0;
                g=0;
                b=0;
                k=0;
                for (int i=1;i<splittedMsg.length;i++){
                    switch (splittedMsg[i]){
                        case "K":
                            k++;
                            break;
                        case "B":
                            b++;
                            break;
                        case "G":
                            g++;
                            break;
                        case "R":
                            r++;
                            break;
                    }
                }
                hboxCards.setVisible(true);
                mainGamePane.setVisible(true);
                playerr.setText("HRAJEŠ!");
                playerr.setVisible(false);

                for (Node pn:hboxUI.getChildren()) {
                    if (pn instanceof Pane){
                        Node rdy = ((Pane) pn).getChildren().get(0);
                        if (rdy instanceof Text){
                            rdy = ((Pane) pn).getChildren().get(1);
                            ((Text) rdy).setText("Počet karet: "+(splittedMsg.length-1));
                            rdy = ((Pane) pn).getChildren().get(2);
                            ((Text) rdy).setText("HRAJE!");
                            rdy.setVisible(false);
                        }
                    }
                }
            }
        });
    }

    public void writeToConsole(String s) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                console.setText(console.getText()+s+"\n");
            }
        });
    }

    public void setOnTurn(String name, int cardNum) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (Node pn:hboxUI.getChildren()) {
                    if (pn instanceof Pane){
                        Node rdy = ((Pane) pn).getChildren().get(0);
                        if (rdy instanceof Text) {
                            rdy = ((Pane) pn).getChildren().get(2);
                            rdy.setVisible(false);
                        }
                    }
                }

                onTurn = Main.userName.equals(name);
                if(onTurn){
                    int i=0;
                    playerr.setVisible(true);
                    for (Node pn:hboxCards.getChildren()) {
                        if (pn instanceof Pane){
                            Node num = ((Pane) pn).getChildren().get(0);
                            i++;
                            if (num instanceof Text){
                                switch (i){
                                    case 1:
                                        ((Text) num).setText(""+k);
                                        break;
                                    case 2:
                                        ((Text) num).setText(""+b);
                                        break;
                                    case 3:
                                        ((Text) num).setText(""+g);
                                        break;
                                    case 4:
                                        ((Text) num).setText(""+r);
                                        break;
                                }
                            }
                        }
                    }
                }else{
                    for (Node pn:hboxUI.getChildren()) {
                        if (pn instanceof Pane){
                            Node rdy = ((Pane) pn).getChildren().get(0);
                            if (rdy instanceof Text){
                                if (((Text) rdy).getText().equals(name))
                                rdy = ((Pane) pn).getChildren().get(1);
                                ((Text) rdy).setText("Počet karet: "+(cardNum-1));
                                rdy = ((Pane) pn).getChildren().get(2);
                                rdy.setVisible(true);
                            }
                        }
                    }
                }
            }
        });
    }

    public void sendBlack(MouseEvent mouseEvent) {
        if (onTurn){
            if(b!=0) {
                tcpConn.putCard("B");
            }
        }
    }

    public void sendGreen(MouseEvent mouseEvent) {
        if (onTurn){
            if(b!=0) {
                tcpConn.putCard("G");
            }
        }
    }

    public void sendRed(MouseEvent mouseEvent) {
        if (onTurn){
            if(b!=0) {
                tcpConn.putCard("R");
            }
        }
    }

    public void sendBrown(MouseEvent mouseEvent) {
        if (onTurn){
            if(b!=0) {
                tcpConn.putCard("K");
            }
        }
    }

    public void lostCard(String s){
        switch (s){
            case "K":
                k--;
                break;
            case "B":
                b--;
                break;
            case "G":
                g--;
                break;
            case "R":
                r--;
                break;
        }
        setOnTurn(Main.userName, 0);
    }
}

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
import java.util.ResourceBundle;

/**
 * Created by seda on 28/10/16.
 */
public class GameController implements Initializable {
    private TCP tcpConn;

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
                for (Node pn:hboxUI.getChildren()) {
                    if (pn instanceof Pane){
                        Node in = ((Pane) pn).getChildren().get(0);
                        if (in instanceof Text){
                            if (((Text) in).getText().equals("")){
                                Node rdy = ((Pane) pn).getChildren().get(0);
                                ((Text) rdy).setText(name);

                                rdy = ((Pane) pn).getChildren().get(2);
                                ((Text) rdy).setText("Nepřipraven");
                                ((Text) rdy).setFill(Color.RED);
                                break;
                            }
                        }
                    }
                }
//                Text userName = new Text();
//                userName.setText(name);
//                userName.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, 25));
//                userName.setLayoutY(60);
//                userName.setTextAlignment(TextAlignment.CENTER);
//                userName.setWrappingWidth(116);
//
//                Text userReady = new Text();
//                if(ready!=1) {
//                    userReady.setText("Nepřipraven");
//                    userReady.setFill(Color.RED);
//                }
//                else {
//                    userReady.setText("Připraven");
//                    userReady.setFill(Color.GREEN);
//                }
//                userReady.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));
//                userReady.setLayoutY(125);
//                userReady.setTextAlignment(TextAlignment.CENTER);
//                userReady.setWrappingWidth(116);
//
//                Text cardNum = new Text();
//                cardNum.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));
//                cardNum.setLayoutY(100);
//                cardNum.setWrappingWidth(116);
//                cardNum.setTextAlignment(TextAlignment.CENTER);
//                cardNum.setText("Počet karet:");
//
//                Line line = new Line();
//                line.setLayoutX(116);
//                line.setStartY(163);
//
//                Pane pane = new Pane();
//                pane.getChildren().add(0, userName);
//                pane.getChildren().add(1, cardNum);
//                pane.getChildren().add(2, userReady);
//                pane.getChildren().add(3, line);
//                pane.setPrefHeight(156);
//                pane.setPrefWidth(116);
//                hboxUI.getChildren().add(userIndex, pane);
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
                                Node rdy = ((Pane) pn).getChildren().get(0);
                                ((Text) rdy).setText("");
                                rdy = ((Pane) pn).getChildren().get(1);
                                ((Text) rdy).setText("");
                                rdy = ((Pane) pn).getChildren().get(2);
                                ((Text) rdy).setText("");
                                break;
                            }
                            x++;
                        }
                    }
                }
//TODO:Nejde to spravne
//                for (int i = x; i<hboxUI.getChildren().size();i++){
//                    Node pn = hboxUI.getChildren().get(i);
//                    if (pn instanceof Pane){
//                        Node in = ((Pane) pn).getChildren().get(0);
//                        if (in instanceof Text) {
//                            if (!(((Text) in).getText().equals(""))) {
//                                Node pr = hboxUI.getChildren().get(x);
//                                if (pr instanceof Pane) {
//                                    Node rdy = ((Pane) pr).getChildren().get(0);
//                                    ((Text) rdy).setText(((Text)((Pane) pn).getChildren().get(0)).getText());
//                                    ((Text)((Pane) pn).getChildren().get(0)).setText("");
//
//                                    rdy = ((Pane) pr).getChildren().get(1);
//                                    ((Text) rdy).setText(((Text)((Pane) pn).getChildren().get(1)).getText());
//                                    ((Text)((Pane) pn).getChildren().get(1)).setText("");
//
//                                    rdy = ((Pane) pr).getChildren().get(2);
//                                    ((Text) rdy).setText(((Text)((Pane) pn).getChildren().get(2)).getText());
//                                    ((Text)((Pane) pn).getChildren().get(2)).setText("");
//                                }
//                            }
//                        }
//                    }
//                }

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
        int r=0, g=0, b=0, k=0;
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

        for (Node pn:hboxCards.getChildren()) {

        }

        for (Node pn:hboxUI.getChildren()) {
            if (pn instanceof Pane){
                Node in = ((Pane) pn).getChildren().get(0);
                if (in instanceof Text){
                    if (!((Text) in).getText().equals("")){
                        Node rdy = ((Pane) pn).getChildren().get(1);
                        ((Text) rdy).setText("Počet karet: "+(splittedMsg.length-1));
                        rdy = ((Pane) pn).getChildren().get(1);
                        ((Text) rdy).setText("HRAJE!");
                        rdy.setVisible(false);
                    }
                }
            }
        }
    }

    public void writeToConsole(String s) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                console.setText(console.getText()+s+"\n");
            }
        });
    }
}

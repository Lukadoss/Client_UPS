package Trick.Controller;

import Trick.Main;
import Trick.TCPClient.TCP;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {
    private TCP tcpConn;
    private boolean onTurn = true;
    private int r, g, b, k;
    private int hboxPos = 0;

    @FXML
    public Pane mainGamePane;
    @FXML
    public Text statusText, cardStack, player, playerr, turnCard, winner;
    @FXML
    public Rectangle firstCard, cheatCard;
    @FXML
    public GridPane serverLobbyPane;
    @FXML
    public Button ready;
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
        tcpConn.startPinging();
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
    public void addNewUser(final String name, final int ready, final int cardNums) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Text userName = new Text();
                userName.setText(name);
                userName.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, 30));
                while (userName.getLayoutBounds().getWidth() > 110) {
                    userName.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, userName.getFont().getSize() - 1));
                    userName.setText(name);
                }
                userName.setLayoutY(60);
                userName.setTextAlignment(TextAlignment.CENTER);
                userName.setWrappingWidth(116);

                Text userReady = new Text();
                if (ready != 1) {
                    userReady.setText("Nepřipraven");
                    userReady.setFill(Color.RED);
                } else {
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
                cardNum.setText("Počet karet: "+cardNums);

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
                hboxUI.getChildren().add(hboxPos, pane);
                hboxPos++;
            }
        });
    }

    @FXML
    public void removeUser(String name) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (Node pn : hboxUI.getChildren()) {
                    if (pn instanceof Pane) {
                        Node in = ((Pane) pn).getChildren().get(0);
                        if (in instanceof Text) {
                            if (((Text) in).getText().equals(name)) {
                                hboxUI.getChildren().remove(pn);
                                hboxPos--;
                                break;
                            }
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
                for (Node pn : hboxUI.getChildren()) {
                    if (pn instanceof Pane) {
                        Node in = ((Pane) pn).getChildren().get(0);
                        if (in instanceof Text) {
                            if (((Text) in).getText().equals(name)) {
                                Node rdy = ((Pane) pn).getChildren().get(2);
                                if (rdy instanceof Text) {
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

    public void setUserReady() {
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
                r = 0;
                g = 0;
                b = 0;
                k = 0;
                for (int i = 1; i < splittedMsg.length; i++) {
                    switch (splittedMsg[i]) {
                        case "K":
                            k++;
                            ((Text) ((Pane) hboxCards.getChildren().get(0)).getChildren().get(0)).setText("" + k);
                            break;
                        case "B":
                            b++;
                            ((Text) ((Pane) hboxCards.getChildren().get(1)).getChildren().get(0)).setText("" + b);
                            break;
                        case "G":
                            g++;
                            ((Text) ((Pane) hboxCards.getChildren().get(2)).getChildren().get(0)).setText("" + g);
                            break;
                        case "R":
                            r++;
                            ((Text) ((Pane) hboxCards.getChildren().get(3)).getChildren().get(0)).setText("" + r);
                            break;
                    }
                }
                hboxCards.setVisible(true);
                mainGamePane.setVisible(true);
                mainGamePane.setDisable(false);
                playerr.setText("HRAJEŠ!");
                playerr.setFill(Color.GREEN);
                playerr.setVisible(false);
                ready.setVisible(false);
                cardStack.setText(""+0);
                firstCard.setFill(Color.WHITE);

                if(onTurn) {
                    for (Node pn : hboxUI.getChildren()) {
                        if (pn instanceof Pane) {
                            Node rdy = ((Pane) pn).getChildren().get(0);
                            if (rdy instanceof Text) {
                                rdy = ((Pane) pn).getChildren().get(1);
                                ((Text) rdy).setText("Počet karet: " + (splittedMsg.length - 1));
                                rdy = ((Pane) pn).getChildren().get(2);
                                ((Text) rdy).setText("HRAJE!");
                                rdy.setVisible(false);
                            }
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
                if (console.getText().length()>1000) console.setText("");
                console.setText(s+"\n"+console.getText());
            }
        });
    }

    public void setOnTurn(String name, String nameBefore, int lastPlayerCardsNum) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (Node pn : hboxUI.getChildren()) {
                    if (pn instanceof Pane) {
                        Node rdy = ((Pane) pn).getChildren().get(0);
                        if (((Text) rdy).getText().equals(name)) ((Pane) pn).getChildren().get(2).setVisible(true);
                        if (((Text) rdy).getText().equals(nameBefore) && !nameBefore.equals(name))
                            ((Pane) pn).getChildren().get(2).setVisible(false);
                        if (((Text) rdy).getText().equals(nameBefore) && !nameBefore.equals(name))
                            ((Text) ((Pane) pn).getChildren().get(1)).setText("Počet karet: " +lastPlayerCardsNum);
                    }
                }

                onTurn = Main.userName.equals(name);

                if (onTurn) {
                    playerr.setVisible(true);
                    hboxCards.setDisable(false);
                    if (name.equals(nameBefore)) {

                    }
                } else {
                    playerr.setVisible(false);
                    hboxCards.setDisable(true);
                }
            }
        });
    }

    public void sendBlack(MouseEvent mouseEvent) {
        if (onTurn) {
            tcpConn.putCard("B");
        }
    }

    public void sendGreen(MouseEvent mouseEvent) {
        if (onTurn) {
            tcpConn.putCard("G");
        }
    }

    public void sendRed(MouseEvent mouseEvent) {
        if (onTurn) {
            tcpConn.putCard("R");
        }
    }

    public void sendBlue(MouseEvent mouseEvent) {
        if (onTurn) {
            tcpConn.putCard("K");
        }
    }

    public void lostCard(String s) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                switch (s) {
                    case "K":
                        k--;
                        ((Text) ((Pane) hboxCards.getChildren().get(0)).getChildren().get(0)).setText("" + k);
                        break;
                    case "B":
                        b--;
                        ((Text) ((Pane) hboxCards.getChildren().get(1)).getChildren().get(0)).setText("" + b);
                        break;
                    case "G":
                        g--;
                        ((Text) ((Pane) hboxCards.getChildren().get(2)).getChildren().get(0)).setText("" + g);
                        break;
                    case "R":
                        r--;
                        ((Text) ((Pane) hboxCards.getChildren().get(3)).getChildren().get(0)).setText("" + r);
                        break;
                }
                hboxCards.setDisable(true);
            }
        });
    }

    public void readyPlayground(int cardNum, String guessedCard) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                cardStack.setText(""+cardNum);
                switch (guessedCard) {
                    case "K":
                        firstCard.setFill(Color.BLUE);
                        break;
                    case "B":
                        firstCard.setFill(Color.BLACK);
                        break;
                    case "G":
                        firstCard.setFill(Color.LIME);
                        break;
                    case "R":
                        firstCard.setFill(Color.RED);
                        break;
                    case "X":
                        firstCard.setFill(Color.WHITE);
                        break;
                }
            }
        });
    }

    public void checkCheat(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(!cardStack.getText().equals("0")) {
                    tcpConn.checkCheat();
                }
            }
        });
    }

    public void setCheatedCard(String cheatedCard) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                switch (cheatedCard) {
                    case "K":
                        cheatCard.setFill(Color.BLUE);
                        break;
                    case "B":
                        cheatCard.setFill(Color.BLACK);
                        break;
                    case "G":
                        cheatCard.setFill(Color.LIME);
                        break;
                    case "R":
                        cheatCard.setFill(Color.RED);
                        break;
                }
                hboxCards.setDisable(true);
                mainGamePane.setDisable(true);
                for (Node pn : hboxUI.getChildren()) {
                    if (pn instanceof Pane) {
                        Node rdy = ((Pane) pn).getChildren().get(2);
                        if (rdy instanceof Text && rdy.isVisible()) {
                            rdy.setVisible(false);
                            break;
                        }
                    }
                }
                Timeline timeline = new Timeline(new KeyFrame(
                        Duration.millis(2000),
                        ae -> continueGame()));
                timeline.play();
            }
        });
    }

    public void setCheater(String name, int cardNum) {
        for (Node pn : hboxUI.getChildren()) {
            if (pn instanceof Pane) {
                Node rdy = ((Pane) pn).getChildren().get(0);
                if (((Text) rdy).getText().equals(name)) ((Text) ((Pane) pn).getChildren().get(1)).setText("Počet karet: " +cardNum);
            }
        }
        firstCard.setFill(Color.WHITE);
        cardStack.setText(""+0);
    }

    public void setDisconnected(String name) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (Node pn : hboxUI.getChildren()) {
                    if (pn instanceof Pane) {
                        Node rdy = ((Pane) pn).getChildren().get(0);
                        if (((Text) rdy).getText().equals(name)) {
                            ((Text) ((Pane) pn).getChildren().get(2)).setFill(Color.RED);
                            ((Text) ((Pane) pn).getChildren().get(2)).setText("DISCONNECT!");
                            ((Text) ((Pane) pn).getChildren().get(2)).setVisible(true);
                            break;
                        }
                    }
                }
            }
        });
    }

    public void setReconnected(String name) {
        for (Node pn : hboxUI.getChildren()) {
            if (pn instanceof Pane) {
                Node rdy = ((Pane) pn).getChildren().get(0);
                if (((Text) rdy).getText().equals(name)) {
                    ((Text) ((Pane) pn).getChildren().get(2)).setFill(Color.GREEN);
                    ((Text) ((Pane) pn).getChildren().get(2)).setText("HRAJE!");
                    ((Text) ((Pane) pn).getChildren().get(2)).setVisible(false);
                    break;
                }
            }
        }
    }

    public void gameEnd(String name) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Timeline timeline = new Timeline(new KeyFrame(
                        Duration.millis(5000),
                        ae -> clearRoom()));
                if (!name.equals("")){
                    mainGamePane.setVisible(false);
                    winner.setText("Víťezem hry se stává: "+name);
                    winner.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
                    while (winner.getLayoutBounds().getWidth() > 690) {
                        winner.setFont(Font.font("Verdana", FontWeight.BOLD, winner.getFont().getSize() - 1));
                        winner.setText(name);
                    }
                    winner.setVisible(true);
                    timeline.play();
                }else{
                    mainGamePane.setVisible(false);
                    winner.setText("Hráč se nestihl vrátit v časovém limitu. Hra končí.");
                    winner.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
                    winner.setVisible(true);
                    timeline.play();
                }
            }
        });
    }

    private void clearRoom(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                hboxCards.setVisible(false);
                ready.setDisable(false);
                ready.setVisible(true);
                playerr.setText("Nepřipraven");
                playerr.setFill(Color.RED);
                playerr.setVisible(true);
                winner.setVisible(false);
                while(hboxUI.getChildren().iterator().hasNext()){
                    hboxUI.getChildren().remove(0);
                    hboxPos--;
                }
                tcpConn.getRoomInfo();
                onTurn = true;
            }
        });
    }

    private void continueGame(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                hboxCards.setDisable(false);
                mainGamePane.setDisable(false);
                cheatCard.setFill(Color.WHITE);
            }
        });
    }
}

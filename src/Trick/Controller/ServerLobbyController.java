package Trick.Controller;

import Trick.Main;
import Trick.TCPClient.MsgTables;
import Trick.TCPClient.TCP;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by seda on 28/10/16.
 */
public class ServerLobbyController implements Initializable {
    public TCP tcpConn;

    @FXML
    public Text statusText;
    @FXML
    public Button joinRoom, refreshList;
    @FXML
    public GridPane serverLobbyPane;
    @FXML
    public TableView<Room> lobbyTable;
    @FXML
    public TableColumn<Room, String> tableRoomId, tableRoomName, tableConnPlayers, tableMaxPlayers;
    @FXML
    public Text player1, player2, player3, player4, player5, player6, player7, player1r, player2r, player3r, player4r, player5r, player6r, player7r;
    @FXML
    public TextArea console;
    @FXML
    public VBox vboxUI;

    public static class Room {
        private final SimpleStringProperty roomId;
        private final SimpleStringProperty roomName;
        private final SimpleStringProperty connPlayers;
        private final SimpleStringProperty maxPlayers;
        private final SimpleStringProperty roomStatus;

        private Room(String rId, String rName, String cPl, String mPl, String rStatus) {
            this.roomId = new SimpleStringProperty(rId);
            this.roomName = new SimpleStringProperty(rName);
            this.connPlayers = new SimpleStringProperty(cPl);
            this.maxPlayers = new SimpleStringProperty(mPl);
            this.roomStatus = new SimpleStringProperty(rStatus);
        }


        public String getRoomId() {
            return roomId.get();
        }

        public void setRoomId(String rId) {
            roomId.set(rId);
        }

        public String getRoomName() {
            return roomName.get();
        }

        public void setRoomName(String rName) {
            roomName.set(rName);
        }

        public String getConnPlayers() {
            return connPlayers.get();
        }

        public void setConnPlayers(String cPl) {
            connPlayers.set(cPl);
        }

        public String getMaxPlayers() {
            return maxPlayers.get();
        }

        public void setMaxPlayers(String mPl) {
            maxPlayers.set(mPl);
        }

        public String getRoomStatus() {
            return roomStatus.get();
        }

        public void setRoomStatus(String rStatus) {
            roomStatus.set(rStatus);
        }
    }

    private ObservableList<Room> data =
            FXCollections.observableArrayList();



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.tcpConn = Main.tcpi;
        setLobbyTable();
        joinRoom.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                assign();
            }
        });
        refreshList.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                refreshTable();
            }
        });
    }

    public void refreshTable() {
        lobbyTable.getItems().clear();
        tcpConn.getRoomsTable();
    }

    public void updateTableRow(String rId, String rName, String cPl, String mPl, String rStatus) {
        data.add(new Room(rId, rName, cPl, mPl, MsgTables.resolveRoomStatus(rStatus)));
        lobbyTable.setItems(data);
    }

    public void setLobbyTable() {
        tableRoomId.setCellValueFactory(
                new PropertyValueFactory<Room, String>("roomId"));
        tableRoomName.setCellValueFactory(
                new PropertyValueFactory<Room, String>("roomName"));
        tableConnPlayers.setCellValueFactory(
                new PropertyValueFactory<Room, String>("connPlayers"));
        tableMaxPlayers.setCellValueFactory(
                new PropertyValueFactory<Room, String>("maxPlayers"));
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

    public void assign() {
        try {
            Room room = lobbyTable.getSelectionModel().getSelectedItem();
            if (room.connPlayers != room.maxPlayers)
                tcpConn.joinRoom(Integer.parseInt(room.getRoomId()));
        } catch (NullPointerException ignored) {
        }
    }

    @FXML
    public void addNewUserUi(final int userIndex, final String name, final int ready,final String score) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Text userName = new Text();
                userName.setText(name.toUpperCase());
                userName.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
                Text userScore = new Text();
                userScore.setText("Skóre: " + score);
                userScore.setFill(Color.rgb(117, 117, 117, .99));
                Text userReady = new Text();
                if(ready == 1){
                    userReady.setText("Připraven!");
                }
                else {
                    userReady.setText("");
                }
                userReady.setFill(Color.rgb(33, 150, 243, .99));
                userReady.setFont(Font.font("Verdana", FontWeight.BOLD, 16));

                VBox vbox = new VBox();
                vbox = vboxUI;
                vbox.getChildren().add(1, userName);
                vbox.setAlignment(Pos.CENTER);
                vbox.setSpacing(5);
            }
        });
    }
}

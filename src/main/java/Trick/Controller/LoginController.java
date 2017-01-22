package Trick.Controller;

import Trick.Main;
import Trick.TCPClient.TCP;
import Trick.TCPClient.ClientListener;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Thread.sleep;
public class LoginController {
    @FXML
    public GridPane loginPane;
    @FXML
    public TextField ipField;
    @FXML
    public TextField portField;
    @FXML
    public TextField nickField;
    @FXML
    public Button loginBtn;
    @FXML
    public VBox statusTextVBox;
    @FXML
    public Text statusText;

    private TCP tcp;
    private ClientListener clientListener;

    public void attemptLogin() {
        statusText.setText("");
        loginBtn.setDisable(true);
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(1000),
                ae -> loginBtn.setDisable(false)));
        try {
            InetAddress inetAddress = InetAddress.getByName(ipField.getText());
            int port = Integer.parseInt(portField.getText());
            tcp = new TCP(inetAddress, port);

            if (tcp.getSocket() != null) {
                clientListener = new ClientListener(tcp);
                Thread thread = new Thread(clientListener);
                thread.start();
                tcp.loginUser(nickField.getText());
            }else{
                setStatusText("Nedostupný server", 1000);
            }
        } catch (Exception e) {
            setStatusText("Neplatný server/port", 1000);
        }
        timeline.play();
    }

    public void setLobbyUi() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    ((Stage) loginPane.getScene().getWindow()).close();

                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Stage/Game.fxml"));
                    Parent serverLobbyRoot = fxmlLoader.load();
                    final Stage serverLobbyStage = new Stage();
                    serverLobbyStage.setScene(new Scene(serverLobbyRoot, 1024, 768));
                    serverLobbyStage.setTitle("Trick - Herní lobby");
                    serverLobbyStage.setResizable(false);
                    serverLobbyStage.show();
                    serverLobbyStage.setOnCloseRequest(windowEvent -> {
                        System.exit(0);
                        Platform.exit();
                    });
                    Main.parentWindow = serverLobbyStage;
                    Main.FXMLLOADER_SERVERLOBBY = fxmlLoader;
                    GameController s = Main.FXMLLOADER_SERVERLOBBY.getController();
                    clientListener.setGameController(s);
                    s.setStatusText("Přihlášen na server", false);

                    tcp.startPinging();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setDiscLoginUi() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Main.parentWindow.close();

                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Stage/Login.fxml"));
                    Parent root = fxmlLoader.load();
                    final Stage loginStage = new Stage();
                    loginStage.setTitle("Login");
                    loginStage.setScene(new Scene(root, 350, 270));
                    loginStage.setResizable(false);
                    loginStage.show();
                    loginStage.setOnCloseRequest(windowEvent -> {
                        System.exit(0);
                        Platform.exit();
                    });
                    Main.FXMLLOADER_LOGIN = fxmlLoader;

                    LoginController l = Main.FXMLLOADER_LOGIN.getController();
                    l.setStatusText("Spojení se serverem bylo přerušeno", 8000);
                    clientListener.setGameController(null);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setStatusText(final String text, final int duration) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                statusText.setText(text);
                statusTextVBox.setAlignment(Pos.CENTER);
                statusText.setTextAlignment(TextAlignment.CENTER);
                Thread timedText = new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(duration);
                            statusText.setText("");
                        } catch (InterruptedException ignored) {
                        }
                    }
                };
                timedText.start();
            }
        });
    }

    public void resetTCP() {
        if(tcp.getSocket()!=null) {
            try {
                tcp.getSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        tcp = null;
    }
}

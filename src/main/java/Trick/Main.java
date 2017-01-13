package Trick;

import Trick.TCPClient.TCP;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static FXMLLoader FXMLLOADER_LOGIN;
    public static FXMLLoader FXMLLOADER_SERVERLOBBY;
    public static TCP tcpi;
    public static String userName;
    public static Stage parentWindow;

    @Override
    public void start(Stage loginStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Stage/Login.fxml"));
        Parent root = fxmlLoader.load();
        loginStage.setTitle("Login");
        loginStage.setScene(new Scene(root, 350, 270));
        loginStage.setResizable(false);
        loginStage.show();
        loginStage.setOnCloseRequest(windowEvent -> {
            System.exit(0);
            Platform.exit();
        });
        FXMLLOADER_LOGIN = fxmlLoader;
    }


    public static void main(String[] args) {
        launch(args);
    }
}

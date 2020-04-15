/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package todoclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Shorouk
 */
public class TodoClient extends Application {

    public static Stage parentStage;
    @Override
    public void start(Stage stage) throws Exception {
        parentStage = stage;
        stage.initStyle(StageStyle.UNDECORATED);
        Parent root = FXMLLoader.load(getClass().getResource("/views/ConnectToServer.fxml"));
        Scene scene = new Scene(root, 320, 200);
        stage.setScene(scene);
        
        stage.show();
        stage.setTitle("ToDo");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}

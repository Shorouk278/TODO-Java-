/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import models.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * FXML Controller class
 *
 * @author Fouad
 */
public class HomeController implements Initializable, Runnable {

    private Socket socket;
    private DataInputStream dis;
    private PrintStream ps;
    private Thread thread;
    JSONArray listsJson;

    @FXML
    private ListView<?> friends_listview;
    @FXML
    private Button add_list_btn;
    @FXML
    private AnchorPane rootPane;
    private AnchorPane pane;
    @FXML
    private TableView<List> createdListTable;
    @FXML
    private TableColumn<List, String> createdListTableColumn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        connectToServer(ConnectToServerController.ip);
        createdListTableColumn.setCellValueFactory(new PropertyValueFactory<List, String>("title"));

    }

    @FXML
    private void openAddListPage(ActionEvent event) {
        try {
            pane = FXMLLoader.load(getClass().getResource("/views/AddList.fxml"));
            rootPane.getChildren().setAll(pane);
        } catch (IOException ex) {
            System.out.println("Can Not Open AddList Page");
        }

    }

    private void connectToServer(String ip) {
        try {
            socket = new Socket(ip, 5005);
            dis = new DataInputStream(socket.getInputStream());
            ps = new PrintStream(socket.getOutputStream());
            JSONObject createdListsJson = new JSONObject();
            createdListsJson.put("functionNumber", "5");
            createdListsJson.put("username", LoginController.username);
            ps.println(createdListsJson);
            thread = new Thread(this);
            thread.start();
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "Cannot Connect To Server").show();
        }
    }

    public ObservableList<List> getTitle() {
        ObservableList<List> titles = FXCollections.observableArrayList();
        for (int i = 0; i < listsJson.length(); i++) {
            JSONObject obj = new JSONObject();
            obj = (JSONObject) listsJson.get(i);
            titles.add(new List(obj.get("title").toString()));
        }
        return titles;
    }

    @Override
    public void run() {
        String response;
        while (true) {
            try {
                response = dis.readLine();
                listsJson = new JSONArray(response);
                createdListTable.setItems(getTitle());

            } catch (IOException ex) {
                Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}

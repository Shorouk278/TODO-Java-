/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import models.FriendsList;
import org.json.JSONObject;

/**
 * FXML Controller class
 *
 * @author mostafa
 */
public class AddCollaboratorsController implements Initializable {

    ManageConnection connection = new ManageConnection();
    JSONObject online;
    @FXML
    private TableView<FriendsList> collaboratorsTable;
    @FXML
    private TableColumn<FriendsList, String> friendsColumn;
    @FXML
    private TableColumn<FriendsList, FriendsList> addColumn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (connection.connectToServer(ConnectToServerController.ip)) {
            setOnline();
        }
        collaboratorsTableSetting();

    }

    private void collaboratorsTableSetting() {

        friendsColumn.setCellValueFactory(new PropertyValueFactory<>("friendName"));
        friendsColumn.setSortable(false);
        friendsColumn.setResizable(false);
        addColumn.setSortable(false);
        addColumn.setResizable(false);

        Callback<TableColumn<FriendsList, FriendsList>, TableCell<FriendsList, FriendsList>> cellFactory
                = new Callback<TableColumn<FriendsList, FriendsList>, TableCell<FriendsList, FriendsList>>() {
            @Override
            public TableCell call(final TableColumn<FriendsList, FriendsList> param) {
                final TableCell<FriendsList, FriendsList> cell = new TableCell<FriendsList, FriendsList>() {
                    final Button btn = new Button("assign");

                    @Override
                    public void updateItem(FriendsList friend, boolean empty) {
                        super.updateItem(friend, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                JSONObject assign = new JSONObject();
                                assign.put("functionNumber", "17");
                                assign.put("username", LoginController.username);
                                assign.put("friend", HomeController.friends.get(this.getTableRow().getIndex()).getFriendName());
                                assign.put("itemID", HomeController.itemID);
                                connection.ps.println(assign);
                                btn.setDisable(true);
                                boolean status = connection.closeConnection();
                                ItemController.dialog.close();
                                if (status) {
                                    try {
                                        Navigation.navigateToPage(FXMLLoader.load(getClass().getResource("/views/Item.fxml")));
                                    } catch (IOException ex) {
                                        Logger.getLogger(AddCollaboratorsController.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };

        addColumn.setCellFactory(cellFactory);
        collaboratorsTable.setItems(HomeController.friends);
    }

    private void setOnline() {
        online = new JSONObject();
        online.put("functionNumber", "15");
        online.put("username", LoginController.username);
        connection.ps.println(online);
    }

}

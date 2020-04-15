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
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import models.*;
import org.json.JSONArray;
import org.json.JSONObject;
import todoclient.TodoClient;

/**
 * FXML Controller class
 *
 * @author Fouad
 */
public class HomeController implements Initializable, Runnable {

    ManageConnection connection = new ManageConnection();
    private Thread thread;
    private JSONObject responseJson;
    public static JSONObject searchJson;
    static JSONArray listsJson;
    private JSONArray friendRequestsJson;
    ObservableList<FriendRequest> requests;
    public static ObservableList<FriendsList> friends;
    ObservableList<Item> items;
    ObservableList<Item> assignedItems;
    ObservableList<FriendRequest> assignRequests;
    ObservableList<List> createdLists;
    ObservableList<Notification> notification;
    private JSONArray friendsJson;
    public static Stage dialog;
    public static int listID;
    public static int itemID;
    private int createdListRowIndex = 0;
    private BorderPane pane;
    JSONObject createdListsJson;
    private int requestFunctionNumber;
    @FXML
    private ListView<FriendsList> friends_listview;
    @FXML
    private Button add_list_btn;

    @FXML
    private TableView<List> createdListTable;
    @FXML
    private TableColumn<List, String> createdListTableColumn;
    @FXML
    private TextField search_txt;
    @FXML
    private Pane items_pane;
    @FXML
    private Label creationDate_lbl;
    @FXML
    private Label deadLine_lbl;
    @FXML
    private TableView<FriendRequest> friendsRequestTable;
    @FXML
    private TableColumn<FriendRequest, String> message_column;
    @FXML
    private TableColumn<FriendRequest, Button> accept_column;
    @FXML
    private TableColumn<FriendRequest, Button> reject_column;
    @FXML
    private Label username_search_label;
    @FXML
    private Button addFriend_button;
    @FXML
    private Pane search_pane;
    @FXML
    private MenuButton itemMenu;
    @FXML
    private MenuItem addItem_menu;
    @FXML
    private Label username_label;
    @FXML
    private Pane top_pane;
    @FXML
    private TableView<Item> itemTable;
    @FXML
    private TableColumn<Item, String> itemTitleColumn;
    @FXML
    private TableColumn<Item, String> itemStatusColumn;
    @FXML
    private TableColumn<Item, Item> removeItemColumn;
    @FXML
    private TableView<Item> assignedItemsTable;
    @FXML
    private TableColumn<Item, String> assignedItemsColumn;
    @FXML
    private TextField listTitleText;
    @FXML
    private CheckBox todoCheckBtn;
    @FXML
    private CheckBox inProgressCheckBtn;
    @FXML
    private CheckBox DoneCheckBtn;
    @FXML
    private Button saveListchangeBtn;
    @FXML
    private Label listNameValid;
    @FXML
    private MenuItem deleteListBtn;
    @FXML
    private Button liststate_btn;
    @FXML
    private BarChart<?, ?> state_graph;
    @FXML
    private NumberAxis y_axis;
    @FXML
    private CategoryAxis x_axis;
    @FXML
    private TableColumn<Notification, String> notification_column;
    @FXML
    private TableView<Notification> notification_table;
    @FXML
    private Button friendRequest_btn;
    @FXML
    private Button notififcation_btn;
    @FXML
    private Button assignedReq_btn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (connection.connectToServer(ConnectToServerController.ip)) {
            connection.ps.println(UserDetails.prepareJson());
            thread = new Thread(this);
            thread.start();
        }
        friendRequestsTableSetting();
        createdListTableSetting();
        friendsListViewSetting();
        itemTableSetting();
        assignedItemsTableSetting();
        notificationSetting();
        if (createdLists != null) {
            saveListchangeBtn.setVisible(false);
            listTitleText.setEditable(false);
            inProgressCheckBtn.setDisable(true);
            todoCheckBtn.setDisable(true);
            DoneCheckBtn.setDisable(true);
        }
        search_pane.setVisible(false);
        listNameValid.setVisible(false);
        username_label.setText(LoginController.username);
        state_graph.setVisible(false);

    }

    @FXML
    private void openAddListPage(ActionEvent event) {
        try {
            connection.closeConnection();
            thread.stop();
            Navigation.navigateToPage(FXMLLoader.load(getClass().getResource("/views/AddList.fxml")));

        } catch (IOException ex) {
            System.out.println("Can Not Open AddList Page");
        }

    }

    public ObservableList<FriendRequest> getFriendRequest() {
        requests = FXCollections.observableArrayList();
        for (int i = friendRequestsJson.length() - 1; i >= 0; i--) {
            requests.add(new FriendRequest(friendRequestsJson.getJSONObject(i).getString("sender"), friendRequestsJson.getJSONObject(i).getString("sender") + " Wants to add you as friend"));
        }
        return requests;
    }

    // created list title .
    public void getTitle() {

        for (int i = 0; i < listsJson.length(); i++) {
            createdLists.add(new List(listsJson.getJSONObject(i).get("title").toString()));
        }
    }

    public void getFriends() {
        for (int i = 0; i < friendsJson.length(); i++) {
            System.out.println(""+friendsJson);
            friends.add(new FriendsList(friendsJson.getJSONObject(i).getString("friend"),friendsJson.getJSONObject(i).getString("state")));
        }
    }

    public void getAssignedRequests(JSONArray array) {
        assignRequests = FXCollections.observableArrayList();
        for (int i = 0; i < array.length(); i++) {
            assignRequests.add(new FriendRequest(array.getJSONObject(i).getString("creator"), array.getJSONObject(i).getString("creator") + " Wants to assign you to item", array.getJSONObject(i).getInt("id")));
        }

        if (assignRequests.size() > 0) {
            assignedReq_btn.setStyle("-fx-background-image: url('/style/notepad.png')");
        }
    }

    public void getAssignedItems(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            assignedItems.add(new Item(array.getJSONObject(i).getString("title"), "", array.getJSONObject(i).getInt("id")));

        }
    }

    @Override
    public void run() {
        String response;
        while (true) {
            try {
                response = connection.dis.readLine();
                if (response != null) {
                    responseJson = new JSONObject(response);
                    System.out.println(responseJson);
                    if (!responseJson.isEmpty()) {
                        switch (Integer.parseInt(responseJson.getString("functionNumber").toString())) {
                            case 5:
                                getUserDetails();
                                break;
                            case 6:
                                createFriendSearchList(responseJson.get("username").toString(), "Add Friend");
                                break;
                            case 7:
                                requests = FXCollections.observableArrayList();
                                requests.add(new FriendRequest(responseJson.getString("sender"), responseJson.getString("sender") + " Wants to add you as friend"));
                                friendsRequestTable.setItems(requests);
                                friendsRequestTable.refresh();
                                friendRequest_btn.setStyle("-fx-background-image: url('/style/friends.png')");
                                break;
                            case 8:
                                if (responseJson.get("checkResult").toString().equalsIgnoreCase("false")) {
                                    searchJson = new JSONObject();
                                    searchJson.put("functionNumber", "6");
                                    searchJson.put("username", search_txt.getText().trim());
                                    connection.ps.println(searchJson);
                                } else {
                                    createFriendSearchList(search_txt.getText().trim(), "Pending");
                                }
                                break;
                            case 9:
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        friends.add(new FriendsList(responseJson.getString("reciever"), "online"));
                                    }
                                });
                                break;
                            case 10:
                                if (responseJson.getString("rejectCondition").equalsIgnoreCase("true")) {
                                    System.out.println("FriendRequest rejected Succesfully");
                                }
                                break;
                            case 11:
                                //error
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        removeFromFriendsList(responseJson.getString("remover"));
                                    }
                                });

                                break;
                            case 12:
                                //error
                                System.out.println(responseJson);
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateFriendState(responseJson.getString("friend"), responseJson.getString("state"));
                                    }
                                });

                                break;
                            case 13:
                                items.add(new Item(responseJson.getString("title"), "todo", responseJson.getInt("itemID")));
                                JSONObject itemObject = new JSONObject();
                                itemObject.put("title", responseJson.getString("title"));
                                itemObject.put("id", responseJson.getInt("itemID"));
                                itemObject.put("status", "todo");
                                itemObject.put("desc", "");
                                itemObject.put("comment", "");
                                listsJson.getJSONObject(createdListRowIndex).getJSONArray("items").put(itemObject);
                                break;
                            case 17:
                                assignRequests.add(new FriendRequest(responseJson.getString("sender"), responseJson.getString("sender") + " Wants to assign you to item", responseJson.getInt("id")));
                                assignedReq_btn.setStyle("-fx-background-image: url('/style/notepad.png')");
                                break;
                            case 18:
                                assignedItems.add(new Item(responseJson.getString("title"), "", responseJson.getInt("id")));
                                break;
                            case 19://creator removed assigned item
                                for (int i = 0; i < assignedItems.size(); i++) {
                                    if (responseJson.getInt("itemID") == assignedItems.get(i).getId()) {
                                        assignedItems.remove(i);
                                    }
                                }
                                break;
                            case 20:
                                String notificationMsg = responseJson.getString("updater") + "updated " + responseJson.getString("itemTitle");
                                notification.add(new Notification(notificationMsg));
                                notification_table.refresh();
                                notififcation_btn.setStyle("-fx-background-image: url('/style/notification.png')");
                                break;
                        }
                    }
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            connection.closeConnection();
                            thread.stop();
                            try {
                                new Alert(Alert.AlertType.ERROR, "Connection Lost", ButtonType.OK).show();
                                Navigation.navigateToPage(FXMLLoader.load(getClass().getResource("/views/Login.fxml")));

                            } catch (IOException ex1) {
                                Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex1);
                            }

                        }
                    });
                    break;
                }

            } catch (IOException ex) {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        connection.closeConnection();
                        thread.stop();
                        try {
                            new Alert(Alert.AlertType.ERROR, "Connection Lost", ButtonType.OK).show();
                            Navigation.navigateToPage(FXMLLoader.load(getClass().getResource("/views/Login.fxml")));

                        } catch (IOException ex1) {
                            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex1);
                        }

                    }
                });

                break;
            }

        }

    }

    @FXML
    private void didFinishSearch(ActionEvent event) {
        if (!search_txt.getText().trim().equalsIgnoreCase(LoginController.username)) {
            if (checkFriend(search_txt.getText().trim())) {
                search_pane.setVisible(true);
                addFriend_button.setText("Friend");
                addFriend_button.setDisable(true);
                username_search_label.setText(search_txt.getText().trim());
                addFriend_button.setStyle("-fx-background-color:white;");
            } else {
                checkRequests();
            }
        }
        if (search_txt.getText().trim().equals("")) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    search_pane.setVisible(false);
                }
            });
        }
    }

    private boolean checkFriend(String name) {
        for (int i = 0; i < friends.size(); i++) {
            if (friends.get(i).getFriendName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void sendFriendRequest() {
        JSONObject friendRequestJson = new JSONObject();
        friendRequestJson.put("functionNumber", "7");
        friendRequestJson.put("sender", LoginController.username);
        friendRequestJson.put("reciever", HomeController.searchJson.get("username").toString());
        connection.ps.println(friendRequestJson);
    }

    private void didSelectList(int clickedRow) {
        items.clear();
        listTitleText.setEditable(false);
        inProgressCheckBtn.setDisable(true);
        todoCheckBtn.setDisable(true);
        DoneCheckBtn.setDisable(true);
        saveListchangeBtn.setVisible(false);
        JSONObject listData = listsJson.getJSONObject(clickedRow);
        List selectedList = new List();
        selectedList.setId(Integer.parseInt(listData.get("id").toString()));
        listID = selectedList.getId();
        selectedList.setTitle(listData.getString("title"));
        selectedList.setColor(listData.getString("color"));
        selectedList.setDeadline(listData.getString("deadline"));
        selectedList.setStartDate(listData.getString("startdate"));
        selectedList.setStatus(listData.getString("status"));
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                creationDate_lbl.setText(selectedList.getStartDate());
                listTitleText.setText(selectedList.getTitle());
                deadLine_lbl.setText(selectedList.getDeadline());
                if (selectedList.getStatus().equalsIgnoreCase("todo")) {
                    todoCheckBtn.setSelected(true);
                    inProgressCheckBtn.setSelected(false);
                    DoneCheckBtn.setSelected(false);

                } else if (selectedList.getStatus().equalsIgnoreCase("inprogress")) {
                    inProgressCheckBtn.setSelected(true);
                    todoCheckBtn.setSelected(false);
                    DoneCheckBtn.setSelected(false);
                } else {
                    DoneCheckBtn.setSelected(true);
                    inProgressCheckBtn.setSelected(false);
                    todoCheckBtn.setSelected(false);
                }
                JSONArray itemsArray = listData.getJSONArray("items");
                if (itemsArray.length() > 0) {
                    deleteListBtn.setDisable(true);
                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject item = itemsArray.getJSONObject(i);
                        Item newItem = new Item();
                        newItem.setId(item.getInt("id"));
                        newItem.setDescription(item.getString("desc"));
                        newItem.setComment(item.getString("comment"));
                        newItem.setStatus(item.getString("status"));
                        newItem.setTitle(item.getString("title"));
                        items.add(newItem);
                    }
                } else {
                    deleteListBtn.setDisable(false);
                }

            }
        });

    }

    @FXML
    private void logOutPressed(ActionEvent event) {
        connection.closeConnection();
        thread.stop();
        try {

            pane = FXMLLoader.load(getClass().getResource("/views/Login.fxml"));
            TodoClient.parentStage.setScene(new Scene(pane, 1400, 800));
        } catch (IOException ex) {
            System.out.println("Can't Open Login Page");
        }

    }

    @FXML
    private void friendRequestsPressed(ActionEvent event) {
        friendRequest_btn.setStyle("-fx-background-image: url('/style/friends2.png')");
        friendsRequestTable.setItems(requests);
        requestFunctionNumber = 7;
        if (friendsRequestTable.isVisible()) {
            friendsRequestTable.setVisible(false);
        } else {
            friendsRequestTable.setVisible(true);
        }
    }

    private void friendRequestsTableSetting() {
        friendsRequestTable.setVisible(false);
        message_column.setCellValueFactory(new PropertyValueFactory<>("message"));
        accept_column.setCellValueFactory(new PropertyValueFactory<>("accept_btn"));
        reject_column.setCellValueFactory(new PropertyValueFactory<>("reject_btn"));
        message_column.setSortable(false);
        accept_column.setSortable(false);
        reject_column.setSortable(false);

        accept_column.setCellFactory(col -> new TableCell<FriendRequest, Button>() {

            private final Button button;

            {
                button = new Button("Accept");
                button.setOnAction(evt -> {
                    FriendRequest item = (FriendRequest) getTableRow().getItem();
                    if (requestFunctionNumber == 7) {
                        acceptFriendRequest(item, "9");//accept friend request
                    } else {
                        acceptFriendRequest(item, "18");//accept assign request
                    }

                });
            }

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : button);
            }
        });

        reject_column.setCellFactory(col -> new TableCell<FriendRequest, Button>() {

            private final Button button;

            {
                button = new Button("Reject");
                button.setOnAction(evt -> {
                    FriendRequest item = (FriendRequest) getTableRow().getItem();
                    if (requestFunctionNumber == 7) {
                        rejectFriendRequest(item, "10");//reject friend request
                    } else {
                        rejectFriendRequest(item, "19");//reject assign request
                    }

                });
            }

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : button);
            }
        });

    }

    private void acceptFriendRequest(FriendRequest req, String num) {

        //Update database and remove from table
        JSONObject acceptRequest = new JSONObject();
        acceptRequest.put("functionNumber", num);
        acceptRequest.put("sender", req.getUserName());
        acceptRequest.put("reciever", LoginController.username);

        if (num.equals("9")) {
            requests.remove(req);
            //add to Friends List
          friends.add(new FriendsList(req.getUserName(), "online"));
          
        } else {
            acceptRequest.put("id", req.getId());
            assignRequests.remove(req);
        }
        connection.ps.println(acceptRequest);
    }

    private void rejectFriendRequest(FriendRequest req, String num) {

        //Update database and remove from table
        JSONObject rejectRequest = new JSONObject();
        rejectRequest.put("functionNumber", num);
        rejectRequest.put("sender", req.getUserName());
        rejectRequest.put("reciever", LoginController.username);

        if (num.equals("10")) {
            requests.remove(req);
        } else {
            assignRequests.remove(req);
            rejectRequest.put("id", req.getId());
        }
        connection.ps.println(rejectRequest);
    }

    private void createdListTableSetting() {
        createdLists = FXCollections.observableArrayList();
        createdListTableColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        createdListTableColumn.setSortable(false);
        createdListTableColumn.setResizable(false);
        createdListTable.setRowFactory(tv -> {
            TableRow<List> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY
                        && event.getClickCount() == 1) {
                    int clickedRow = row.getIndex();
                    createdListRowIndex = clickedRow;
                    didSelectList(createdListRowIndex);
                }
            });
            return row;
        });
        createdListTable.setItems(createdLists);
    }

    private void assignedItemsTableSetting() {
        assignedItemsColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        assignedItemsColumn.setSortable(false);
        assignedItemsColumn.setResizable(false);
        assignedItemsTable.setRowFactory(tv -> {
            TableRow<Item> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY
                        && event.getClickCount() == 1) {
                    int clickedRow = row.getIndex();
                    itemID = assignedItems.get(clickedRow).getId();
                    try {
                        connection.closeConnection();
                        thread.stop();
                        Navigation.navigateToPage(FXMLLoader.load(getClass().getResource("/views/Item.fxml")));
                    } catch (IOException ex) {
                        Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);

                    }
                }
            });
            return row;
        });
        assignedItems = FXCollections.observableArrayList();
        assignedItemsTable.setItems(assignedItems);
    }

    private void checkRequests() {
        JSONObject checkRequests = new JSONObject();
        checkRequests.put("functionNumber", "8");
        checkRequests.put("sender", LoginController.username);
        checkRequests.put("receiver", search_txt.getText().trim());
        connection.ps.println(checkRequests);

    }

    @FXML
    private void addFriendAction(ActionEvent event) {
        sendFriendRequest();
        addFriend_button.setText("Pending");
        addFriend_button.setDisable(true);
        addFriend_button.setStyle("-fx-background-color:white;");
//        search_pane.setVisible(false);
//        search_txt.setText("");
    }

    private void friendsListViewSetting() {
        friends_listview.setCellFactory(friendsListView -> new FriendsListCell());
        friends = FXCollections.observableArrayList();
        friends_listview.setItems(friends);
        friends_listview.refresh();
    }

    private void removeFromFriendsList(String frName) {
        for (int i = 0; i < friends.size(); i++) {
            if (friends.get(i).getFriendName().equals(frName)) {
                friends.remove(i);

            }
        }
    }

    private void updateFriendState(String frName, String state) {
        for (int i = 0; i < friends.size(); i++) {
            if (friends.get(i).getFriendName().equals(frName)) {
                friends.get(i).setOnline_state(state);
            }
        }
        friends_listview.refresh();

    }

    @FXML
    private void addItem(ActionEvent event) {
        dialog = new Stage();
        dialog.initStyle(StageStyle.UNDECORATED);
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/AddItem.fxml"));
            Scene scene = new Scene(root, 400, 250);
            dialog.setScene(scene);
            dialog.initOwner(TodoClient.parentStage);
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.showAndWait();
            dialog.setTitle("Add Item");
        } catch (IOException ex) {
            System.out.println("Can Not Open Add Item");
        }

    }

    private void closeHome(ActionEvent event) {
        connection.closeConnection();
        thread.stop();
        TodoClient.parentStage.close();
    }

    private void itemTableSetting() {

        items = FXCollections.observableArrayList();
        itemTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        itemTitleColumn.setSortable(false);
        itemTitleColumn.setResizable(false);
        itemStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        itemStatusColumn.setSortable(false);
        itemStatusColumn.setResizable(false);
        removeItemColumn.setSortable(false);
        removeItemColumn.setResizable(false);

        Callback<TableColumn<Item, Item>, TableCell<Item, Item>> cellFactory
                = new Callback<TableColumn<Item, Item>, TableCell<Item, Item>>() {
            @Override
            public TableCell call(final TableColumn<Item, Item> param) {
                final TableCell<Item, Item> cell = new TableCell<Item, Item>() {

                    final Button removeBtn = new Button("");

                    @Override
                    public void updateItem(Item item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            removeBtn.getStyleClass().add("remove-item-btn");
                            removeBtn.setOnAction(event -> {
                                removeItem(this.getTableRow().getIndex());
                            });
                            setGraphic(removeBtn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };
        removeItemColumn.setCellFactory(cellFactory);

        itemTable.setRowFactory(tv -> {
            TableRow<Item> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY
                        && event.getClickCount() == 1) {
                    int clickedRow = row.getIndex();
                    itemID = items.get(clickedRow).getId();
                    try {
                        if (connection.closeConnection()) {
                            thread.stop();
                            Navigation.navigateToPage(FXMLLoader.load(getClass().getResource("/views/Item.fxml")));
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            return row;
        });
        itemTable.setItems(items);
    }

    private void removeItem(int index) {
        JSONObject removeItem = new JSONObject();
        removeItem.put("functionNumber", "13");
        removeItem.put("itemID", items.get(index).getId());
        removeItem.put("list_id", listID);
        connection.ps.println(removeItem);
        items.remove(index);
        if (items.isEmpty()) {
            deleteListBtn.setDisable(false);
        }
        listsJson.getJSONObject(createdListRowIndex).getJSONArray("items").remove(index);
    }

    @FXML
    private void assignRequest(ActionEvent event) {
        friendsRequestTable.setItems(assignRequests);
        friendsRequestTable.refresh();
        requestFunctionNumber = 17;
        if (friendsRequestTable.isVisible()) {
            friendsRequestTable.setVisible(false);
        } else {
            friendsRequestTable.setVisible(true);
        }
        assignedReq_btn.setStyle("-fx-background-image: url('/style/note.png')");
    }

    @FXML
    private void updateList(ActionEvent event) {

        saveListchangeBtn.setVisible(true);
        listTitleText.setEditable(true);
        listTitleText.setStyle("-fx-background-color:white;-fx-text-fill:black;");
        inProgressCheckBtn.setDisable(false);
        todoCheckBtn.setDisable(false);
        DoneCheckBtn.setDisable(false);
    }

    @FXML
    private void deleteList(ActionEvent event) {
        JSONObject deleteList = new JSONObject();
        deleteList.put("functionNumber", "22");
        deleteList.put("listID", listID);
        connection.ps.println(deleteList);
        createdLists.remove(createdListRowIndex);
        listsJson.remove(createdListRowIndex);
        if (createdListRowIndex != 0) {
            didSelectList(--createdListRowIndex);
        } else {
            listTitleText.clear();
            todoCheckBtn.setSelected(false);
            inProgressCheckBtn.setSelected(false);
            todoCheckBtn.setSelected(false);
            creationDate_lbl.setText("");
            deadLine_lbl.setText("");
        }
        if (listsJson.length() == 0) {
            itemMenu.setDisable(true);
        }

    }

    @FXML
    private void todoChecked(ActionEvent event) {
        inProgressCheckBtn.setSelected(false);
        DoneCheckBtn.setSelected(false);
    }

    @FXML
    private void inProgressChecked(ActionEvent event) {
        todoCheckBtn.setSelected(false);
        DoneCheckBtn.setSelected(false);

    }

    @FXML
    private void DoneChecked(ActionEvent event) {
        inProgressCheckBtn.setSelected(false);
        todoCheckBtn.setSelected(false);
    }

    @FXML
    private void saveListchange(ActionEvent event) {

        if (listTitleText.getText().trim().isEmpty()) {
            listNameValid.setVisible(true);
        } else {
            listNameValid.setVisible(false);
            JSONObject updateList = new JSONObject();
            updateList.put("functionNumber", "21");
            updateList.put("title", listTitleText.getText().trim());
            updateList.put("listID", listID);
            if (inProgressCheckBtn.isSelected()) {
                updateList.put("status", "inprogress");
                listsJson.getJSONObject(createdListRowIndex).put("status", "inprogress");
            } else if (DoneCheckBtn.isSelected()) {
                updateList.put("status", "done");
                listsJson.getJSONObject(createdListRowIndex).put("status", "done");

            } else {
                updateList.put("status", "todo");
                listsJson.getJSONObject(createdListRowIndex).put("status", "todo");
                todoCheckBtn.setSelected(true);
            }
            connection.ps.println(updateList);
            listTitleText.setEditable(false);
            inProgressCheckBtn.setDisable(true);
            todoCheckBtn.setDisable(true);
            DoneCheckBtn.setDisable(true);
            saveListchangeBtn.setVisible(false);
            listTitleText.setStyle("-fx-background-color-grey;-fx-text-fill:white;");
            createdLists.get(createdListRowIndex).setTitle(listTitleText.getText().trim());
            createdListTable.refresh();
            listsJson.getJSONObject(createdListRowIndex).put("title", listTitleText.getText().trim());

        }
    }

    @FXML
    private void showStatistics(ActionEvent event) {
        state_graph.getData().clear();
        if (state_graph.isVisible()) {

            state_graph.setVisible(false);
        } else {
            state_graph.setVisible(true);
        }
        x_axis.setLabel("Status");
        y_axis.setLabel("Count");
        XYChart.Series series1 = new XYChart.Series();
        XYChart.Series series2 = new XYChart.Series();
        XYChart.Series series3 = new XYChart.Series();

        int todo_counter = 0;
        int inprogress_counter = 0;
        int done_counter = 0;

        for (int i = 0; i < listsJson.length(); i++) {
            if (listsJson.getJSONObject(i).get("status").toString().equalsIgnoreCase("todo")) {
                todo_counter++;
            } else if (listsJson.getJSONObject(i).get("status").toString().equalsIgnoreCase("done")) {
                done_counter++;
            } else {
                inprogress_counter++;
            }

        }
        series1.setName("Todo");
        series1.getData().add(new XYChart.Data("Todo", todo_counter));
        series2.setName("Inprogress");
        series2.getData().add(new XYChart.Data("Done", done_counter));
        series3.setName("Done");
        series3.getData().add(new XYChart.Data("InProgress", inprogress_counter));
        state_graph.setTitle("Statistics");
        state_graph.getData().addAll(series1, series2, series3);
    }

    @FXML
    private void notification(ActionEvent event) {
        notififcation_btn.setStyle("-fx-background-image: url('/style/bell.png')");
        if (notification_table.isVisible()) {
            notification_table.setVisible(false);
        } else {
            notification_table.setVisible(true);
        }

    }

    class FriendsListCell extends ListCell<FriendsList> {

        private final Label label = new Label("");
        private final Button removeBtn = new Button("");
        private final ImageView state = new ImageView();
        private final Pane pane = new Pane();
        private final Pane pane2 = new Pane();
        private final HBox contentContainer = new HBox();

        public FriendsListCell() {
            contentContainer.getChildren().addAll(label, pane, state, pane2, removeBtn);
            contentContainer.setHgrow(pane, Priority.ALWAYS);
            contentContainer.setHgrow(pane2, Priority.ALWAYS);
            label.getStyleClass().add("friend-name");
            removeBtn.getStyleClass().add("remove-friend");
            state.getStyleClass().add("friend-state");
        }
        @Override
        public void updateItem(FriendsList name, boolean empty) {
            super.updateItem(name, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                label.setText(name.getFriendName());
                if (name.getOnline_state().equals("online")) {
                    state.setVisible(true);
                    state.setImage(new Image(getClass().getResourceAsStream("/style/online.png")));
                } else {
                    state.setVisible(false);
                    state.setImage(new Image(getClass().getResourceAsStream("/style/offline.png")));
                }
                state.setFitHeight(10);
                state.setFitWidth(10);
                removeBtn.setOnAction(new EventHandler() {
                    @Override
                    public void handle(Event event) {
                        JSONObject removeFriend = new JSONObject();
                        removeFriend.put("functionNumber", "11");
                        removeFriend.put("username", LoginController.username);
                        removeFriend.put("friend", name.getFriendName());
                        connection.ps.println(removeFriend);
                        friends.remove(name);
                    }

                });

                setGraphic(contentContainer);
            }
        }

    }

    /*
     * show friend requests - friends - lists.
     */
    private void getUserDetails() {
        listsJson = new JSONArray();
        listsJson = responseJson.getJSONArray("listArray");
        if (listsJson.length() > 0) {
            getTitle();
            itemMenu.setDisable(false);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    didSelectList(0);
                }
            });
        } else {
            itemMenu.setDisable(true);
        }
        friendRequestsJson = new JSONArray();
        friendRequestsJson = responseJson.getJSONArray("friendRequest");
        if (friendRequestsJson.length() > 0) {
            friendsRequestTable.setItems(getFriendRequest());
            friendRequest_btn.setStyle("-fx-background-image: url('/style/friends.png')");
        }
        friendsJson = responseJson.getJSONArray("friends");
        if (friendsJson.length() > 0) {
            getFriends();
        }
        getAssignedRequests(responseJson.getJSONArray("itemsReq"));
        getAssignedItems(responseJson.getJSONArray("assignedItems"));
    }

    private void createFriendSearchList(String username, String buttonText) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                search_pane.setVisible(true);
                username_search_label.setText(username);
                addFriend_button.setText(buttonText);
                if (buttonText.equalsIgnoreCase("Pending")) {
                    addFriend_button.setDisable(true);
                    addFriend_button.setStyle("-fx-background-color:white;");
                } else {
                    addFriend_button.setDisable(false);
                    addFriend_button.setStyle("-fx-background-color:gray;");
                }

            }
        });

    }

    private void notificationSetting() {
        notification = FXCollections.observableArrayList();
        notification_column.setCellValueFactory(new PropertyValueFactory<>("message"));
        notification_column.setSortable(false);
        notification_column.setResizable(false);
        notification_table.setVisible(false);
        notification_table.setItems(notification);
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package todoclient;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Shorouk
 */
public class FXMLDocumentController implements Initializable, Runnable {

    private Socket socket;
    private DataInputStream dis;
    private PrintStream ps;
    private Thread thread;
    private AnchorPane pane;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private TextField name_textfield;
    @FXML
    private PasswordField password_textfield;
    @FXML
    private Button login_btn;
    @FXML
    private Button sign_up_btn;
    @FXML
    private Hyperlink recovery_link;
    @FXML
    private TextField ip_textfield;
    @FXML
    private Label user_name_lbl;
    @FXML
    private Label password_lbl;
    @FXML
    private Label ip_lbl;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Pattern pattern = Pattern.compile("[a-zA-Z0-9\\-\\+]*");
        password_textfield.setTextFormatter(new TextFormatter<String>(change -> {
    if(pattern.matcher(change.getText()).matches()) {
        return change;
    }

    return null;
}));
        // TODO
    }

    /**
     * navigates to signUp page when signUp button is pressed .
     *
     * @param event
     */
    @FXML
    private void moveToSignUp(ActionEvent event) {
        try {
            pane = FXMLLoader.load(getClass().getResource("SignUp.fxml"));
            rootPane.getChildren().setAll(pane);
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "cant open sign up page").show();
        }
    }

    /**
     * validate all login fields are filled then connect to the server and send
     * the data from text fields.
     *
     * @param event
     */
    @FXML
    private void loginAction(ActionEvent event) {
        if (loginValidation()) {
            user_name_lbl.setText("");
            password_lbl.setText("");
            user_name_lbl.setText("");
            connectToServer(ip_textfield.getText());
            
        }
    }

    @FXML
    private void moveToRecovery(ActionEvent event) {
    }

    /**
     * check that all fields meet the constraints required .
     *
     * @return boolean
     */
    private boolean loginValidation() {
        boolean flag = true;
        if (name_textfield.getText().trim().isEmpty()){
            flag = false;
            user_name_lbl.setText(" UserName can't be empty");
        } else {
            user_name_lbl.setText("");
        }
        if ((password_textfield.getText().trim().isEmpty()) || (password_textfield.getText().length() < 7)) {
            flag = false;
            password_lbl.setText("password must be more than 7 char");
        } else {
            password_lbl.setText("");
        }

        if (!ip_textfield.getText().matches("^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$")) {
            flag = false;
            ip_lbl.setText(" Use approriate pattern");

        } else {
            ip_lbl.setText("");
        }

        return flag;
    }

    /**
     * open connection with server .
     *
     * @param ip server IP .
     */
    private void connectToServer(String ip) {
        try {
            socket = new Socket(ip, 5005);
            dis = new DataInputStream(socket.getInputStream());
            ps = new PrintStream(socket.getOutputStream());
            String loginMessage = "1*#*" + name_textfield.getText().trim() + "*#*" + password_textfield.getText();
            ps.println(loginMessage);
            thread = new Thread(this);
            thread.start();
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "Cannot Connect To Server").show();
        }
    }

    @Override
    /**
     *
     */
    public void run() {
        while (true) {
            
            try {
                if (dis.readLine().equalsIgnoreCase("true")) {
                    System.out.println("Login Succeeded");
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            ip_lbl.setText("Invalid User-Name or Password");
                        }
                    });

                }
            } catch (IOException ex) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        new Alert(Alert.AlertType.ERROR,"Connection Lost to Server");
                    }
                });
            }
        }
    }

}

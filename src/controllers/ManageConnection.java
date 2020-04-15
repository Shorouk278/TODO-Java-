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
import javafx.scene.control.Alert;

/**
 *
 * @author Fouad
 */
public class ManageConnection {

    public Socket socket;
    public DataInputStream dis;
    public PrintStream ps;

    public boolean connectToServer(String ip) {
        boolean state = false;
        try {
            socket = new Socket(ip, 5005);
            dis = new DataInputStream(socket.getInputStream());
            ps = new PrintStream(socket.getOutputStream());
            state = true;
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "Cannot Connect To Server").show();
        }
        return state;
    }

    public boolean closeConnection() {
        boolean state = false;
        try {
            dis.close();
            ps.close();
            socket.close();
            state = true;
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "Cannot close Connection").show();
        }
        return state;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import javafx.scene.control.Button;

/**
 *
 * @author AhmedSaber
 */
public class FriendRequest {
    String userName;
    String message ; 
    int id;

    public FriendRequest() {
    }

    public FriendRequest(String userName,String message) {
        this.userName = userName;
        this.message = message;
        
    }

    public FriendRequest(String userName, String message, int id) {
        this.userName = userName;
        this.message = message;
        this.id = id;
    }
    
    public String getUserName() {
        return userName;
    }
    

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

   
}

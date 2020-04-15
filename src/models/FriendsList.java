/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

/**
 *
 * @author mostafa
 */
public class FriendsList {
    private String friendName;
    private String online_state;

    public FriendsList() {
    }

    public FriendsList(String friendName, String online_state) {
        this.friendName = friendName;
        this.online_state = online_state;
    }
     public FriendsList(String friendName) {
        this.friendName = friendName;
    }
    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
        this.online_state = "offline";
    }
    
     public String getOnline_state() {
        return online_state;
    }

    public void setOnline_state(String online_state) {
        this.online_state = online_state;
    }
    
    
}

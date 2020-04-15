/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import org.json.JSONObject;

/**
 *
 * @author Fouad
 */
public class UserDetails {

    private static JSONObject createdListsJson;
    public static JSONObject prepareJson() {
        createdListsJson = new JSONObject();
        createdListsJson.put("functionNumber", "5");
        createdListsJson.put("username", LoginController.username);
        return createdListsJson;
    }

}

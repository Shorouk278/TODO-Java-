/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;

/**
 *
 * @author Fouad
 */
public class Navigation {

    public static void navigateToPage(BorderPane pane) {
        todoclient.TodoClient.parentStage.setScene(new Scene(pane, Screen.getPrimary().getBounds().getWidth(), Screen.getPrimary().getBounds().getHeight()));
        todoclient.TodoClient.parentStage.setMaximized(true);
    }
}

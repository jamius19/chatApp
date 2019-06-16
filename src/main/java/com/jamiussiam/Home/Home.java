package com.jamiussiam.Home;

import com.jamiussiam.Entity.ChatGroup;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import java.net.URL;
import java.util.ResourceBundle;

public class Home implements Initializable {

    @FXML
    public Button logoutBt, createGroupBt, leaveGroupBt, deleteGroupBt;

    @FXML
    public ListView<ChatGroup> groupList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}

package com.jamiussiam.Home;

import com.jamiussiam.Entity.ChatGroup;
import com.jamiussiam.Entity.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import java.net.URL;
import java.util.ResourceBundle;

public class Home implements Initializable {

    @FXML
    public Button logoutBt, createGroupBt, leaveGroupBt, deleteGroupBt, addMemberBt, joinGroupBt, renameBt;

    @FXML
    public ListView<ChatGroup> groupList;

    @FXML
    public ListView<User> membersList;

    @FXML
    public Label msg, welcome;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        msg.setVisible(false);
    }
}

package com.jamiussiam.Home;

import com.jamiussiam.Entity.ChatGroup;
import com.jamiussiam.Entity.Message;
import com.jamiussiam.Entity.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class Home implements Initializable {

    @FXML
    public Button logoutBt, createGroupBt, leaveGroupBt, deleteGroupBt, addMemberBt, joinGroupBt, renameBt, sendBt, attachBt;

    @FXML
    public ListView<ChatGroup> groupList;

    @FXML
    public ListView<User> membersList;

    @FXML
    public ListView<Message> msgList;

    @FXML
    public Label msg, welcome;

    @FXML
    public TextArea msgEdit;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        msg.setVisible(false);
    }
}

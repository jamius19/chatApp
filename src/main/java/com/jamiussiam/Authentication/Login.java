package com.jamiussiam.Authentication;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class Login implements Initializable {
    @FXML
    public Button loginBt;

    @FXML
    public Button forgotBt;

    @FXML
    public Button signupBt;

    @FXML
    public PasswordField passField;

    @FXML
    public TextField emailField;

    @FXML
    public Label msg;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}



package com.jamiussiam.Authentication;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class Forgot implements Initializable {

    @FXML
    public TextField emailField;

    @FXML
    public TextField securityAnswer;

    @FXML
    public PasswordField passField;

    @FXML
    public TextField passFieldConfirm;

    @FXML
    public Label msg;

    @FXML
    public Button loginBt, signupBt, resetBt;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}

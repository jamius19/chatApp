package com.jamiussiam.Authentication;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;


public class Signup implements Initializable{
    @FXML
    public Button loginBt;

    @FXML
    public Button signupBt;

    @FXML
    public PasswordField passField;

    @FXML
    public PasswordField passFieldConfirm;

    @FXML
    public TextField emailField;

    @FXML
    public TextField nameField;

    @FXML
    public Label msg;

    @FXML
    public TextField securityAnswer;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}

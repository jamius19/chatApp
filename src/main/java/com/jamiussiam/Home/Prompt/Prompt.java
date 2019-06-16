package com.jamiussiam.Home.Prompt;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Prompt implements Initializable {

    @FXML
    public Button submitBt;

    @FXML
    public Button cancelBt;

    @FXML
    public TextField ansField;

    @FXML
    public Label qusField;

    private OnPromptComplete promptComplete;
    private String question;


    public enum SubmitType {
        Submit,
        Cancel
    }

    public static interface OnPromptComplete {
        void onComplete(String value, SubmitType submitType);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }

    public void display (String question, String title, boolean noAnswer, OnPromptComplete promptComplete) throws Exception{
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/home/prompt/Prompt.fxml"));
        Parent root = loader.load();

        Prompt controller = loader.getController();

        this.question = question;
        this.promptComplete = promptComplete;


        controller.qusField.setText(question);

        controller.submitBt.setOnMouseClicked(event ->
                promptComplete.onComplete(controller.ansField.getText(), SubmitType.Submit));

        controller.cancelBt.setOnMouseClicked(event ->
                promptComplete.onComplete(controller.ansField.getText(), SubmitType.Cancel));

        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(root, 463, 165);
        stage.setResizable(false);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }
}

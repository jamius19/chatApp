package com.jamiussiam;

import com.jamiussiam.Authentication.Forgot;
import com.jamiussiam.Authentication.Signup;
import com.jamiussiam.Entity.ChatGroup;
import com.jamiussiam.Entity.Password;
import com.jamiussiam.Entity.User;
import com.jamiussiam.Authentication.Login;
import com.jamiussiam.Home.Home;
import com.jamiussiam.Home.Prompt.Prompt;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Todo Create group chat, member add, leave group
 */
public class App extends Application {
    private static SessionFactory sessionFactory;
    private static Stage primaryStage;
    public static User currentUser;
    public static App app;

    PauseTransition hideMsg;
    private List<ChatGroup> chatGroupList = new ArrayList<>();
    private List<ChatGroup> chatGroupAdmin = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        app = this;
        primaryStage = stage;
        Configuration configuration = new Configuration().configure()
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Password.class)
                .addAnnotatedClass(ChatGroup.class);

        StandardServiceRegistry registryBuilder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties()).build();

        sessionFactory = configuration.buildSessionFactory(registryBuilder);
        Login();
    }

    private void Signup() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/authentication/signup.fxml"));
        Parent root = loader.load();
        final Signup signup = loader.getController();
        signup.msg.setVisible(false);

        signup.loginBt.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    Login();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        signup.signupBt.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                signup.msg.setVisible(true);

                String regex = "^(.+)@(.+)\\.(.+)$";

                boolean isEmpty = signup.nameField.getText().isEmpty() || signup.emailField.getText().isEmpty()
                        || signup.passField.getText().isEmpty() || signup.passFieldConfirm.getText().isEmpty()
                        || signup.securityAnswer.getText().isEmpty();

                if (isEmpty) {
                    signup.msg.setText("Please Enter Your Details.");
                } else if (!signup.emailField.getText().matches(regex)) {
                    signup.msg.setText("Please Enter a Valid Email.");
                } else if (!signup.passField.getText().equals(signup.passFieldConfirm.getText())) {
                    signup.msg.setText("Please Enter same password.");
                } else if (signup.passField.getText().length() <= 8) {
                    signup.msg.setText("Password must be at-lest 8 characters long.");
                } else {
                    signup.msg.setText("Please Wait.");

                    Session session = sessionFactory.openSession();
                    session.beginTransaction();
                    String md5Hex = DigestUtils.md5Hex(signup.passField.getText()).toUpperCase();

                    String hql = String.format("FROM User where email='%s'", signup.emailField.getText());
                    Query query = session.createQuery(hql);
                    List results = query.list();

                    if(results.size() == 0){
                        User user = new User();
                        user.setName(signup.nameField.getText());
                        user.setEmail(signup.emailField.getText().toLowerCase());
                        user.setPassword(md5Hex);
                        user.setSqAnswer(signup.securityAnswer.getText().toLowerCase());

                        Password password = new Password();
                        password.setPassword(md5Hex);
                        password.setUser(user);
                        session.save(user);
                        session.save(password);

                        session.getTransaction().commit();
                        signup.msg.setText("Created a new account for " + signup.nameField.getText() + ". You can now login.");
                    } else {
                        signup.msg.setText("Another user with this email is already registered.");
                    }

                    session.close();
                }

                PauseTransition hideMsg = new PauseTransition(Duration.seconds(0.5f));
                hideMsg.setOnFinished(e -> signup.msg.setVisible(false));
                hideMsg.setDelay(Duration.millis(6000));
                hideMsg.play();
            }
        });

        Scene scene = new Scene(root, 676, 406);
        primaryStage.setResizable(false);
        primaryStage.setTitle("ChatApp Signup");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void Login() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/authentication/login.fxml"));
        Parent root = loader.load();
        final Login login = loader.getController();

        login.msg.setVisible(false);
        login.emailField.setText("jamiussiam@gmail.com");
        login.passField.setText("123456789");

        login.loginBt.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                login.msg.setVisible(true);

                String regex = "^(.+)@(.+)\\.(.+)$";

                if (login.emailField.getText().isEmpty() || login.passField.getText().isEmpty()) {
                    login.msg.setText("Please Enter Your Details.");
                } else if (!login.emailField.getText().matches(regex)) {
                    login.msg.setText("Please Enter a Valid Email.");
                } else {
                    login.msg.setText("Please Wait.");

                    Session session = sessionFactory.openSession();
                    session.beginTransaction();

                    String hql = "FROM User where email='" + login.emailField.getText() + "'";
                    Query query = session.createQuery(hql);
                    List results = query.list();

                    if (results.size() == 0) {
                        login.msg.setText("No user with these credentials found.");
                    } else {
                        User user = (User) results.get(0);
                        String hash = user.getPassword();
                        String password = login.passField.getText();

                        String md5Hex = DigestUtils.md5Hex(password).toUpperCase();

                        if (md5Hex.equals(hash)) {
                            login.msg.setText("Login successful for user " + user.getName() + ". Please Wait.");
                            login.emailField.setDisable(true);
                            login.passField.setDisable(true);
                            login.loginBt.setDisable(true);
                            login.signupBt.setDisable(true);
                            login.forgotBt.setDisable(true);

                            PauseTransition hideMsg = new PauseTransition(Duration.seconds(0.5f));
                            hideMsg.setOnFinished(e -> {
                                try {
                                    currentUser = user;
                                    Home();
                                } catch (IOException ex) {
                                    login.emailField.setDisable(false);
                                    login.passField.setDisable(false);
                                    login.loginBt.setDisable(false);
                                    login.signupBt.setDisable(false);
                                    login.forgotBt.setDisable(false);
                                    ex.printStackTrace();
                                }
                            });
                            hideMsg.setDelay(Duration.millis(1500));
                            hideMsg.play();
                        } else {
                            login.msg.setText("No user with these credentials found.");
                            PauseTransition hideMsg = new PauseTransition(Duration.seconds(0.5f));
                            hideMsg.setOnFinished(e -> login.msg.setVisible(false));
                            hideMsg.setDelay(Duration.millis(3000));
                            hideMsg.play();
                        }
                    }

                    session.close();
                }

            }
        });

        login.signupBt.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    Signup();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        login.forgotBt.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    Forgot();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        Scene scene = new Scene(root, 676, 406);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Chatty Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void Forgot() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/authentication/forgot.fxml"));
        Parent root = loader.load();
        final Forgot controller = loader.getController();

        controller.msg.setVisible(false);

        controller.loginBt.setOnMouseClicked(event -> {
            try {
                Login();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        controller.signupBt.setOnMouseClicked(event -> {
            try {
                Signup();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        controller.resetBt.setOnMouseClicked(event -> {

            boolean isEmpty = controller.emailField.getText().isEmpty()
                    || controller.securityAnswer.getText().isEmpty() || controller.passField.getText().isEmpty()
                    || controller.passFieldConfirm.getText().isEmpty();

            controller.msg.setVisible(true);

            String regex = "^(.+)@(.+)\\.(.+)$";

            if(isEmpty){
                //controller.msg.setText("Please Enter Details");
                FlashMessage("Please Enter Details", controller.msg, 5);
            }  else if (!controller.emailField.getText().matches(regex)) {
                //controller.msg.setText("Please Enter a Valid Email.");
                FlashMessage("Please Enter a Valid Email.", controller.msg, 5);
            } else if (!controller.passField.getText().equals(controller.passFieldConfirm.getText())) {
                //controller.msg.setText("Please Enter same password.");
                FlashMessage("Please Enter same password.", controller.msg, 5);
            } else if (controller.passField.getText().length() <= 7) {
                //controller.msg.setText("Password must be at-lest 8 characters long.");
                FlashMessage("Password must be at-lest 8 characters long.", controller.msg, 5);

            } else {

                Session session = sessionFactory.openSession();
                session.beginTransaction();

                String hql = String.format("from User where email='%s' and sqAnswer='%s'",
                        controller.emailField.getText().toLowerCase(), controller.securityAnswer.getText().toLowerCase());
                Query query = session.createQuery(hql);
                List results = query.list();

                if(results.size() != 0){
                    boolean noDuplicatePass = true;

                    String md5Hex = DigestUtils.md5Hex(controller.passField.getText()).toUpperCase();
                    User user = (User) results.get(0);

                    List<Password> prevPass = user.getPrevPasswords();

                    for (Password pass : prevPass){
                        if(pass.getPassword().equals(md5Hex)){
                            noDuplicatePass = false;
                            break;
                        }
                    }

                    if(noDuplicatePass){
                        user.setPassword(md5Hex);

                        Password password = new Password();
                        password.setUser(user);
                        password.setPassword(md5Hex);


                        session.save(user);
                        session.save(password);
                        //controller.msg.setText("Account password changed. You can now login.");
                        FlashMessage("Account password changed. You can now login.", controller.msg, 5);

                    } else {
                        //controller.msg.setText("This password was used before. Please chose another password.");
                        FlashMessage("This password was used before. Please chose another password.", controller.msg, 5);
                    }

                    session.getTransaction().commit();


                } else {
                    //controller.msg.setText("No account with matching credentials found.");
                    FlashMessage("No account with matching credentials found.", controller.msg, 5);
                }

                session.close();
            }

            /*PauseTransition hideMsg = new PauseTransition(Duration.seconds(0.5f));
            hideMsg.setOnFinished(e -> controller.msg.setVisible(false));
            hideMsg.setDelay(Duration.millis(6000));
            hideMsg.play();*/
        });

        Scene scene = new Scene(root, 676, 406);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Account Recovery");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void Home() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/home/home.fxml"));
        Parent root = loader.load();
        Home controller = loader.getController();
        chatGroupList = currentUser.getGroups();
        chatGroupAdmin = currentUser.getAdminGroups();

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.close();

        for (ChatGroup chatGroup : chatGroupList){
            controller.groupList.getItems().add(chatGroup);
        }



        controller.groupList.setOnMouseClicked(event -> {
            boolean isAdmin = chatGroupAdmin.contains(controller.groupList.getSelectionModel().getSelectedItem());
            controller.deleteGroupBt.setDisable(!isAdmin);
            controller.leaveGroupBt.setDisable(false);
        });


        controller.logoutBt.setOnMouseClicked(event -> {
            currentUser = null;
            try {
                Login();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Scene scene = new Scene(root, 924, 563);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Chatty Home");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void FlashMessage(String message, Label label, int time){
        if(hideMsg != null)
            hideMsg.stop();

        label.setVisible(true);
        label.setText(message);

        hideMsg = new PauseTransition(Duration.seconds(0.5));
        hideMsg.setOnFinished(e -> label.setVisible(false));
        hideMsg.setDelay(Duration.seconds(time));
        hideMsg.play();
    }
}
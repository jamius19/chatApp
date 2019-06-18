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
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionModel;
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

import javax.swing.plaf.ColorUIResource;
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
                    FlashMessage("Please Enter Your Details.", signup.msg, 5);
                } else if (!signup.emailField.getText().matches(regex)) {
                    FlashMessage("Please Enter a Valid Email.", signup.msg, 5);
                } else if (!signup.passField.getText().equals(signup.passFieldConfirm.getText())) {
                    FlashMessage("Please Enter same password.", signup.msg, 5);
                } else if (signup.passField.getText().length() <= 8) {
                    FlashMessage("Password must be at-lest 8 characters long.", signup.msg, 5);
                } else {
                    FlashMessage("Please Wait.", signup.msg, 5);


                    Session session = sessionFactory.openSession();
                    session.beginTransaction();
                    String md5Hex = DigestUtils.md5Hex(signup.passField.getText()).toUpperCase();

                    String hql = String.format("FROM User where email='%s'", signup.emailField.getText());
                    Query query = session.createQuery(hql);
                    List results = query.list();

                    if (results.size() == 0) {
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
                        FlashMessage("Created a new account for " + signup.emailField.getText() + ". You can now login.",
                                signup.msg, 5);

                    } else {
                        FlashMessage("Another user with this email is already registered.", signup.msg, 5);

                    }

                    session.close();
                }
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
        //login.emailField.setText("jamiussiam@gmail.com");
        //login.passField.setText("123456789");

        login.loginBt.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                login.msg.setVisible(true);

                String regex = "^(.+)@(.+)\\.(.+)$";

                if (login.emailField.getText().isEmpty() || login.passField.getText().isEmpty()) {
                    FlashMessage("Please Enter Your Details.", login.msg, 5);
                } else if (!login.emailField.getText().matches(regex)) {
                    FlashMessage("Please Enter a Valid Email.", login.msg, 5);
                } else {
                    FlashMessage("Please Wait.", login.msg, 5);

                    Session session = sessionFactory.openSession();
                    session.beginTransaction();

                    String hql = "FROM User where email='" + login.emailField.getText() + "'";
                    Query query = session.createQuery(hql);
                    List results = query.list();

                    if (results.size() == 0) {
                        FlashMessage("No user with these credentials found.", login.msg, 5);
                    } else {
                        User user = (User) results.get(0);
                        String hash = user.getPassword();
                        String password = login.passField.getText();

                        String md5Hex = DigestUtils.md5Hex(password).toUpperCase();

                        if (md5Hex.equals(hash)) {
                            FlashMessage("Login successful for " + user.getEmail() + ". Please Wait.", login.msg, 5);

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
                            FlashMessage("No user with these credentials found.", login.msg, 5);
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

            if (isEmpty) {
                FlashMessage("Please Enter Details", controller.msg, 5);
            } else if (!controller.emailField.getText().matches(regex)) {
                FlashMessage("Please Enter a Valid Email.", controller.msg, 5);
            } else if (!controller.passField.getText().equals(controller.passFieldConfirm.getText())) {
                FlashMessage("Please Enter same password.", controller.msg, 5);
            } else if (controller.passField.getText().length() <= 7) {
                FlashMessage("Password must be at-lest 8 characters long.", controller.msg, 5);

            } else {

                Session session = sessionFactory.openSession();
                session.beginTransaction();

                String hql = String.format("from User where email='%s' and sqAnswer='%s'",
                        controller.emailField.getText().toLowerCase(), controller.securityAnswer.getText().toLowerCase());
                Query query = session.createQuery(hql);
                List results = query.list();

                if (results.size() != 0) {
                    boolean noDuplicatePass = true;

                    String md5Hex = DigestUtils.md5Hex(controller.passField.getText()).toUpperCase();
                    User user = (User) results.get(0);

                    List<Password> prevPass = user.getPrevPasswords();

                    for (Password pass : prevPass) {
                        if (pass.getPassword().equals(md5Hex)) {
                            noDuplicatePass = false;
                            break;
                        }
                    }

                    if (noDuplicatePass) {
                        user.setPassword(md5Hex);

                        Password password = new Password();
                        password.setUser(user);
                        password.setPassword(md5Hex);


                        session.update(user);
                        session.save(password);
                        FlashMessage("Account password changed. You can now login.", controller.msg, 5);

                    } else {
                        FlashMessage("This password was used before. Please chose another password.", controller.msg, 5);
                    }

                    session.getTransaction().commit();


                } else {
                    FlashMessage("No account with matching credentials found.", controller.msg, 5);
                }

                session.close();
            }
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

        ReloadGroups(controller.groupList);

        controller.welcome.setText("Welcome " + currentUser.getName());

        controller.groupList.setOnMouseClicked(event -> {
            UpdateButtonStatus(controller);
        });

        controller.deleteGroupBt.setOnMouseClicked(event -> {
            Prompt prompt = new Prompt();
            ChatGroup currentGroup = controller.groupList.getSelectionModel().getSelectedItem();

            try {
                prompt.display(String.format("You sure you want to delete group %s?\nAll messages will be deleted.", currentGroup.getName()),
                        "Delete Confirmation", true,
                        (value, submitType, promptController, stage) -> {
                            if (submitType == Prompt.SubmitType.Submit) {
                                try {
                                    Session session = sessionFactory.openSession();
                                    session.beginTransaction();

                                    ChatGroup currentGroupTemp = session.get(ChatGroup.class, currentGroup.getId());
                                    /*currentGroupTemp.setAdmin(null);
                                    currentGroupTemp.getUsers().clear();*/
                                    session.delete(currentGroupTemp);
                                    session.getTransaction().commit();
                                    session.close();
                                    FlashMessage("Group deleted successfully.", controller.msg, 5);
                                } catch (Exception e) {
                                    FlashMessage("Error deleting group.", controller.msg, 5);
                                }

                                stage.close();
                                ReloadGroups(controller.groupList);
                                ReloadMembersList(controller.membersList, controller.groupList);
                                UpdateButtonStatus(controller);
                            } else {
                                stage.close();
                            }
                        });
            } catch (Exception e) {
                FlashMessage("Error deleting group.", controller.msg, 5);
                e.printStackTrace();
            }
        });

        controller.createGroupBt.setOnMouseClicked(event -> {
            Prompt prompt = new Prompt();
            try {
                prompt.display("Please Enter the name of your group.", "Group Creation", false,
                        (value, submitType, promptController, stage) -> {
                            if (submitType == Prompt.SubmitType.Submit) {
                                if (value.isEmpty()) {
                                    FlashMessage("Please Enter a Group Name", promptController.msg, 5);
                                } else {
                                    String cap = value.substring(0, 1).toUpperCase() + value.substring(1);

                                    Session session = sessionFactory.openSession();
                                    session.beginTransaction();

                                    ChatGroup chatGroup = new ChatGroup();
                                    chatGroup.setName(value);
                                    chatGroup.setAdmin(currentUser);
                                    chatGroup.getUsers().add(currentUser);
                                    session.save(chatGroup);
                                    session.getTransaction().commit();
                                    session.close();
                                    stage.close();
                                    FlashMessage("Group created successfully. You can add members now.", controller.msg, 5);
                                    ReloadGroups(controller.groupList);
                                    ReloadMembersList(controller.membersList, controller.groupList);
                                    UpdateButtonStatus(controller);
                                }
                            } else {
                                stage.close();
                            }
                        });
            } catch (Exception e) {
                FlashMessage("Error creating group.", controller.msg, 5);
                e.printStackTrace();
            }
        });

        controller.leaveGroupBt.setOnMouseClicked(event -> {
            ChatGroup currentChatGroup = controller.groupList.getSelectionModel().getSelectedItem();
            //boolean isAdmin = chatGroupAdmin.contains(currentChatGroup);
            String addedInfo = currentChatGroup.getUsers().size() > 1 ? "\nNext senior member will be made admin as there are\nother" +
                    " members in the group." : "\nAs there are no other members in the group, it\nwill be achieved if you leave.";

            try {
                Prompt prompt = new Prompt();
                prompt.display("You sure you want to leave group " + currentChatGroup.getName() + "?" + addedInfo,
                        "Leave Group?", true,
                        (value, submitType, promptController, stage) -> {
                            if (submitType == Prompt.SubmitType.Submit) {
                                boolean deleted = false;

                                Session session = sessionFactory.openSession();
                                session.beginTransaction();
                                ChatGroup currentChatGroupTemp = session.get(ChatGroup.class, currentChatGroup.getId());
                                User userTemp = session.get(User.class, currentUser.getId());

                                /*if (isAdmin) {
                                    if (currentChatGroup.getUsers().size() > 1) {
                                        User nextAdmin = currentChatGroup.getUsers().get(1);
                                        currentChatGroup.setAdmin(nextAdmin);
                                        currentChatGroup.getUsers().remove(currentUser);
                                        session.update(currentChatGroup);
                                    } else {
                                        deleted = true;
                                        session.delete(currentChatGroup);
                                    }
                                }*/

                                if (currentChatGroupTemp.getUsers().size() > 1) {
                                    User nextAdmin = currentChatGroupTemp.getUsers().get(1);
                                    currentChatGroupTemp.setAdmin(nextAdmin);
                                } else {
                                    currentChatGroupTemp.setAdmin(null);
                                }

                                currentChatGroupTemp.getUsers().remove(userTemp);
                                session.update(currentChatGroupTemp);

                                session.getTransaction().commit();
                                session.close();
                                ReloadGroups(controller.groupList);
                                ReloadMembersList(controller.membersList, controller.groupList);
                                UpdateButtonStatus(controller);
                                FlashMessage("Successfully left " + (deleted ? "and deleted" : "") + " the group.", controller.msg, 5);
                            }

                            stage.close();
                        });

            } catch (Exception e) {
                FlashMessage("Error leaving group.", controller.msg, 5);
                e.printStackTrace();
            }
        });

        controller.logoutBt.setOnMouseClicked(event -> {
            currentUser = null;
            try {
                Login();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        controller.addMemberBt.setOnMouseClicked(event -> {
            ChatGroup currentGroup = controller.groupList.getSelectionModel().getSelectedItem();

            Prompt prompt = new Prompt();

            try {
                prompt.display("Please enter the email of the user you want to add.", "Adding Member",
                        false, (value, submitType, promptController, stage) -> {
                            if (submitType == Prompt.SubmitType.Submit) {
                                String regex = "^(.+)@(.+)\\.(.+)$";

                                if (!value.matches(regex)) {
                                    FlashMessage("Please enter a valid email.", promptController.msg, 5);
                                } else {
                                    Session session = sessionFactory.openSession();
                                    session.beginTransaction();
                                    ChatGroup currentGroupNew = session.get(ChatGroup.class, currentGroup.getId());

                                    String hql = "FROM User where email='" + value + "'";
                                    Query query = session.createQuery(hql);
                                    List results = query.list();

                                    if (results.size() == 0) {
                                        FlashMessage("No account associated with this email was found.", controller.msg, 5);
                                    } else {
                                        User user = (User) results.get(0);

                                        if (currentGroupNew.getUsers().contains(user)) {
                                            FlashMessage("User already is a member group " + currentGroupNew.getName() + ".", controller.msg, 5);
                                        } else {
                                            FlashMessage("User with email " + user.getEmail() + " was successfully added to the gourp.", controller.msg, 5);
                                            currentGroupNew.getUsers().add(user);
                                            session.update(currentGroupNew);
                                        }
                                    }

                                    stage.close();

                                    session.getTransaction().commit();
                                    session.close();

                                    ReloadGroups(controller.groupList);
                                    ReloadMembersList(controller.membersList, controller.groupList);
                                    UpdateButtonStatus(controller);
                                }
                            } else {
                                stage.close();
                            }


                        });
            } catch (Exception e) {
                FlashMessage("Error adding members.", controller.msg, 5);
                e.printStackTrace();
            }
        });

        controller.joinGroupBt.setOnMouseClicked(event -> {
            Prompt prompt = new Prompt();
            try {
                prompt.display("Please enter the id of the group.", "Joining Group", false,
                        (value, submitType, promptController, stage) -> {
                            if (submitType == Prompt.SubmitType.Submit) {
                                String regex = "\\d+";

                                if (!value.matches(regex)) {
                                    FlashMessage("Please enter a valid numeric group id.", promptController.msg, 5);
                                } else {
                                    Session session = sessionFactory.openSession();
                                    session.beginTransaction();
                                    ChatGroup targetGroup = session.get(ChatGroup.class, Integer.parseInt(value));

                                    try {
                                        System.out.println("users " + targetGroup.getUsers());
                                    } catch (Exception e){
                                        System.out.println("targetgroup null");
                                    }

                                    boolean containsUser = false;

                                    if(targetGroup != null){
                                        for(User user : targetGroup.getUsers()){
                                            if (user.getEmail().toLowerCase().equals(currentUser.getEmail().toLowerCase())) {
                                                containsUser = true;
                                            }
                                        }
                                    }

                                    if (targetGroup == null) {
                                        FlashMessage("No group with the given id exists", promptController.msg, 5);
                                        session.close();
                                    } else if (containsUser) {
                                        FlashMessage("You're already a member of the group.", promptController.msg, 5);
                                        session.close();
                                    } else {
                                        targetGroup.getUsers().add(currentUser);
                                        session.update(targetGroup);
                                        FlashMessage("Joined group successfully.", controller.msg, 5);
                                        session.getTransaction().commit();
                                        session.close();

                                        ReloadGroups(controller.groupList);
                                        ReloadMembersList(controller.membersList, controller.groupList);
                                        UpdateButtonStatus(controller);
                                        stage.close();
                                    }
                                }
                            } else {
                                stage.close();
                            }

                        });
            } catch (Exception e) {
                FlashMessage("Error joining group.", controller.msg, 5);
                e.printStackTrace();
            }
        });

        controller.renameBt.setOnMouseClicked(event -> {
            Prompt prompt = new Prompt();
            try {
                prompt.display("Please enter the new name.", "Renaming Group", false,
                        (value, submitType, promptController, stage) -> {
                            if (submitType == Prompt.SubmitType.Submit) {
                                ;

                                if (value.isEmpty()) {
                                    FlashMessage("Please enter a valid name.", promptController.msg, 5);
                                } else {
                                    Session session = sessionFactory.openSession();
                                    session.beginTransaction();
                                    ChatGroup targetGroup = session.get(ChatGroup.class, controller.groupList.getSelectionModel().getSelectedItem().getId());

                                    targetGroup.setName(value);

                                    session.update(targetGroup);
                                    session.getTransaction().commit();
                                    session.close();

                                    ReloadGroups(controller.groupList);
                                    ReloadMembersList(controller.membersList, controller.groupList);
                                    stage.close();
                                }
                            } else {
                                stage.close();
                            }

                        });
            } catch (Exception e) {
                FlashMessage("Error joining group.", controller.msg, 5);
                e.printStackTrace();
            }
        });


        Scene scene = new Scene(root, 924, 563);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Chatty Home");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void ReloadGroups(ListView<ChatGroup> chatGroupListView) {
        UpdateUserDetails();
        chatGroupListView.getItems().clear();

        System.out.println("Reload groups");
        for (ChatGroup chatGroup : chatGroupList) {
            chatGroupListView.getItems().add(chatGroup);
        }
    }

    private void ReloadMembersList(ListView<User> userListView, ListView<ChatGroup> chatGroupListView) {
        //ReloadGroups(chatGroupListView);

        ChatGroup currentGroup = chatGroupListView.getSelectionModel().getSelectedItem();
        if (currentGroup != null) {
            User currentGroupAdmin = currentGroup.getAdmin();
            userListView.getItems().clear();

            for (User user : currentGroup.getUsers()) {
                if (user.equals(currentGroupAdmin))
                    user.setAdmin(true);

                userListView.getItems().add(user);
            }
        } else {
            userListView.getItems().clear();
        }
    }

    private void UpdateUserDetails() {
        System.out.println("Updating current User");
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        currentUser = session.get(User.class, currentUser.getId());
        session.getTransaction().commit();
        session.close();

        chatGroupList = currentUser.getGroups();
        chatGroupAdmin = currentUser.getAdminGroups();
    }


    private void FlashMessage(String message, Label label, int time) {
        if (hideMsg != null)
            hideMsg.stop();

        label.setVisible(true);
        label.setText(message);

        hideMsg = new PauseTransition(Duration.seconds(0.5));
        hideMsg.setOnFinished(e -> label.setVisible(false));
        hideMsg.setDelay(Duration.seconds(time));
        hideMsg.play();
    }

    private void UpdateButtonStatus(Home controller) {
        boolean isAdmin = chatGroupAdmin.contains(controller.groupList.getSelectionModel().getSelectedItem());
        controller.deleteGroupBt.setDisable(!isAdmin);
        controller.addMemberBt.setDisable(!isAdmin);
        controller.renameBt.setDisable(!isAdmin);
        controller.leaveGroupBt.setDisable(controller.groupList.getSelectionModel().getSelectedItem() == null);
        ReloadMembersList(controller.membersList, controller.groupList);
    }
}
package com.jamiussiam;

import com.jamiussiam.Authentication.Signup;
import com.jamiussiam.Entity.User;
import com.jamiussiam.Authentication.Login;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import java.util.List;

/**
 * Hello world!
 */
public class App extends Application {
    static SessionFactory sessionFactory;

    @Override
    public void start(Stage stage) throws Exception {
        Configuration configuration = new Configuration().configure()
                .addAnnotatedClass(User.class);

        StandardServiceRegistry registryBuilder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties()).build();

        sessionFactory = configuration.buildSessionFactory(registryBuilder);

//        session.beginTransaction();
//        User user = new User();
//        /*user.setName("Siam");
//        user.setEmail("jam");
//        user.setPassword("jam22");
//        session.save(user);
//        session.getTransaction().commit();*/
//        user = (User) session.get(User.class, 1);
//        System.out.println(user);
//        session.close();

//        Thread t = new Thread() {
//            public void run() {
//                session.beginTransaction();
//                User user = (User) session.get(User.class, 1);
//                System.out.println(user);
//                login.emailField.setText(user.getEmail());
//                session.close();
//            }
//        };

        Login(stage);
    }

    public void Signup(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login/signup.fxml"));
        Parent root = loader.load();
        final Signup signup = loader.getController();
        signup.msg.setVisible(false);

        signup.loginBt.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    Login(stage);
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


                if(signup.nameField.getText().isEmpty() || signup.emailField.getText().isEmpty() || signup.passField.getText().isEmpty()){
                    signup.msg.setText("Please Enter Your Details.");
                } else if (!signup.emailField.getText().matches(regex)) {
                    signup.msg.setText("Please Enter a Valid Email.");
                } else {
                    signup.msg.setText("Please Wait.");

                    Session session = sessionFactory.openSession();
                    session.beginTransaction();
                    String md5Hex = DigestUtils.md5Hex(signup.passField.getText()).toUpperCase();

                    User user = new User();
                    user.setName(signup.nameField.getText());
                    user.setEmail(signup.emailField.getText());
                    user.setPassword(md5Hex);
                    session.save(user);
                    session.getTransaction().commit();
                    session.close();
                    signup.msg.setText("Created a new account for " + signup.nameField.getText() + ". You can now login.");

                    PauseTransition hideMsg = new PauseTransition(Duration.seconds(0.5f));
                    hideMsg.setOnFinished(e -> signup.msg.setVisible(false));
                    hideMsg.setDelay(Duration.millis(6000));
                    hideMsg.play();

                }
            }
        });

        Scene scene = new Scene(root, 600, 400);
        stage.setResizable(false);
        stage.setTitle("ChatApp Signup");
        stage.setScene(scene);
        stage.show();
    }

    public void Login(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login/login.fxml"));
        Parent root = loader.load();
        final Login login = loader.getController();

        login.msg.setVisible(false);

        login.loginBt.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                login.msg.setVisible(true);

                String regex = "^(.+)@(.+)\\.(.+)$";

                if(login.emailField.getText().isEmpty() || login.passField.getText().isEmpty()){
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

                    if(results.size() == 0){
                        login.msg.setText("No user with these credentials found.");
                    } else {
                        User user = (User) results.get(0);
                        String hash = user.getPassword();
                        String password = login.passField.getText();

                        String md5Hex = DigestUtils.md5Hex(password).toUpperCase();

                        if(md5Hex.equals(hash)){
                            login.msg.setText("Login successful for user " + user.getName());
                        } else {
                            login.msg.setText("No user with these credentials found.");
                        }
                    }

                    session.close();

                    PauseTransition hideMsg = new PauseTransition(Duration.seconds(0.5f));
                    hideMsg.setOnFinished(e -> login.msg.setVisible(false));
                    hideMsg.setDelay(Duration.millis(3000));
                    hideMsg.play();

                }
            }
        });


        login.signupBt.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    Signup(stage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Scene scene = new Scene(root, 600, 400);
        stage.setResizable(false);
        stage.setTitle("ChatApp Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
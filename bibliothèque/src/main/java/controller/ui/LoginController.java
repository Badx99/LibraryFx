package controller.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final ApplicationContext applicationContext;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    @Value("classpath:/view/DashboardView.fxml")
    private Resource dashboardResource;

    public LoginController(AuthenticationManager authenticationManager, ApplicationContext applicationContext) {
        this.authenticationManager = authenticationManager;
        this.applicationContext = applicationContext;
    }

    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            // Login successful
            loadDashboard();
        } catch (AuthenticationException e) {
            errorLabel.setVisible(true);
            errorLabel.setText("Identifiants incorrects");
        }
    }

    private void loadDashboard() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(dashboardResource.getURL());
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent parent = fxmlLoader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(parent, 1000, 700);
            String cssPath = getClass().getResource("/style.css").toExternalForm();
            scene.getStylesheets().add(cssPath);

            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setVisible(true);
            errorLabel.setText("Erreur de chargement du tableau de bord");
        }
    }
}

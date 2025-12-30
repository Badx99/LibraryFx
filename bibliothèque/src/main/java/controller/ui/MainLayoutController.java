package controller.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class MainLayoutController {

    @FXML
    private StackPane contentArea;

    private final ApplicationContext applicationContext;

    @Value("classpath:/view/DashboardStatsView.fxml")
    private Resource dashboardStatsViewResource;

    @Value("classpath:/view/BookListView.fxml")
    private Resource bookListViewResource;

    @Value("classpath:/view/MemberListView.fxml")
    private Resource memberListViewResource;

    @Value("classpath:/view/BorrowListView.fxml")
    private Resource borrowListViewResource;

    @Value("classpath:/view/NotificationListView.fxml")
    private Resource notificationListViewResource;

    @FXML
    private Button dashboardButton;

    @FXML
    private Button booksButton;

    @FXML
    private Button membersButton;

    @FXML
    private Button borrowsButton;

    @FXML
    private Button notificationsButton;

    private void setActive(Button activeButton) {

        booksButton.getStyleClass().remove("sidebar-button-active");
        membersButton.getStyleClass().remove("sidebar-button-active");
        borrowsButton.getStyleClass().remove("sidebar-button-active");
        notificationsButton.getStyleClass().remove("sidebar-button-active");
        if (dashboardButton != null)
            dashboardButton.getStyleClass().remove("sidebar-button-active");

        if (!activeButton.getStyleClass().contains("sidebar-button-active")) {
            activeButton.getStyleClass().add("sidebar-button-active");
        }
    }

    public MainLayoutController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @FXML
    public void initialize() {
        showDashboard(); // Default view
    }

    @FXML
    public void showDashboard() {
        setActive(dashboardButton);
        loadView(dashboardStatsViewResource);
    }

    @FXML
    public void showBooks() {
        setActive(booksButton);
        loadView(bookListViewResource);
    }

    @FXML
    public void showMembers() {
        setActive(membersButton);
        loadView(memberListViewResource);
    }

    @FXML
    public void showBorrows() {
        setActive(borrowsButton);
        loadView(borrowListViewResource);
    }

    @FXML
    public void showNotifications() {
        setActive(notificationsButton);
        loadView(notificationListViewResource);
    }

    @FXML
    public void handleLogout() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent parent = fxmlLoader.load();

            javafx.stage.Stage stage = (javafx.stage.Stage) contentArea.getScene().getWindow();
            javafx.scene.Scene scene = new javafx.scene.Scene(parent, 450, 450);
            String cssPath = getClass().getResource("/style.css").toExternalForm();
            scene.getStylesheets().add(cssPath);

            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setTitle("LibraryFX Management - Connexions");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(Resource resource) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(resource.getURL());
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent view = fxmlLoader.load();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

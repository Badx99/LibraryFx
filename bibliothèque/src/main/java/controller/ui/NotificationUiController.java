package controller.ui;

import entity.Notification;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.springframework.stereotype.Controller;
import service.NotificationService;

import java.time.format.DateTimeFormatter;

@Controller
public class NotificationUiController {

    private final NotificationService notificationService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private TableView<Notification> notificationTable;
    @FXML
    private TableColumn<Notification, Long> idColumn;
    @FXML
    private TableColumn<Notification, String> dateColumn;
    @FXML
    private TableColumn<Notification, String> recipientColumn;
    @FXML
    private TableColumn<Notification, String> typeColumn;
    @FXML
    private TableColumn<Notification, String> contentColumn;
    @FXML
    private TableColumn<Notification, String> statusColumn;

    private final ObservableList<Notification> notificationList = FXCollections.observableArrayList();

    public NotificationUiController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getDateEnvoi() != null ? cellData.getValue().getDateEnvoi().format(formatter)
                        : "Non envoyé"));
        recipientColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getAdherent() != null ? cellData.getValue().getAdherent().getNom() + " "
                        + cellData.getValue().getAdherent().getPrenom() : "Inconnu"));
        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType().toString()));
        contentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContenu()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getEnvoyee() ? "Envoyé" : "En attente"));

        loadAllNotifications();
    }

    private void loadAllNotifications() {
        notificationList.setAll(notificationService.getAllNotifications());
        notificationTable.setItems(notificationList);
    }

    @FXML
    public void handleRefresh() {
        loadAllNotifications();
    }

    @FXML
    public void handleShowSent() {
        notificationList.setAll(notificationService.getSentNotifications());
    }

    @FXML
    public void handleShowPending() {
        notificationList.setAll(notificationService.getPendingNotifications());
    }
}

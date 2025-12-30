package controller.ui;

import entity.Book;
import entity.Borrow;
import entity.Member;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import service.BorrowService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@Controller
public class BorrowUiController {

    private final BorrowService borrowService;

    @FXML
    private TableView<Borrow> borrowTable;
    @FXML
    private TableColumn<Borrow, Long> idColumn;
    @FXML
    private TableColumn<Borrow, String> bookTitleColumn;
    @FXML
    private TableColumn<Borrow, String> memberNameColumn;
    @FXML
    private TableColumn<Borrow, LocalDate> dateEmpruntColumn;
    @FXML
    private TableColumn<Borrow, LocalDate> dateRetourPrevueColumn;
    @FXML
    private TableColumn<Borrow, LocalDate> dateRetourReelleColumn;
    @FXML
    private TableColumn<Borrow, String> statutColumn;

    @Value("classpath:/view/BorrowFormView.fxml")
    private Resource borrowFormResource;

    private final ObservableList<Borrow> borrowList = FXCollections.observableArrayList();
    private final ApplicationContext applicationContext;

    public BorrowUiController(BorrowService borrowService, ApplicationContext applicationContext) {
        this.borrowService = borrowService;
        this.applicationContext = applicationContext;
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
        bookTitleColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLivre().getTitre()));
        memberNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getAdherent().getNom() + " " + cellData.getValue().getAdherent().getPrenom()));
        dateEmpruntColumn
                .setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDateEmprunt()));
        dateRetourPrevueColumn
                .setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDateRetourPrevue()));
        dateRetourReelleColumn
                .setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDateRetourReelle()));
        statutColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatut().toString()));

        ContextMenu contextMenu = new ContextMenu();
        MenuItem validateItem = new MenuItem("Valider la demande");
        validateItem.setOnAction(event -> handleValidateBorrow());
        MenuItem refuseItem = new MenuItem("Refuser la demande");
        refuseItem.setOnAction(event -> handleRefuseBorrow());
        MenuItem returnItem = new MenuItem("Retourner le livre");
        returnItem.setOnAction(event -> handleReturnBook());
        MenuItem extendItem = new MenuItem("Prolonger l'emprunt");
        extendItem.setOnAction(event -> handleExtendBorrow());

        contextMenu.getItems().addAll(validateItem, refuseItem, returnItem, extendItem);
        borrowTable.setContextMenu(contextMenu);

        loadAllBorrows();
    }

    private void loadAllBorrows() {
        borrowList.setAll(borrowService.getAllBorrows());
        borrowTable.setItems(borrowList);
    }

    @FXML
    public void handleRefresh() {
        loadAllBorrows();
    }

    @FXML
    public void handleShowAll() {
        loadAllBorrows();
    }

    @FXML
    public void handleShowCurrent() {
        borrowList.setAll(borrowService.getCurrentBorrows());
    }

    @FXML
    public void handleShowLate() {
        borrowList.setAll(borrowService.getLateBorrows());
    }

    @FXML
    public void handleNewBorrow() {
        try {
            FXMLLoader loader = new FXMLLoader(borrowFormResource.getURL());
            loader.setControllerFactory(applicationContext::getBean);
            Parent content = loader.load();
            BorrowFormController controller = loader.getController();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(new DialogPane());
            dialog.getDialogPane().setContent(content);
            dialog.setTitle("Nouvel Emprunt");
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Member selectedMember = controller.getSelectedMember();
                Book selectedBook = controller.getSelectedBook();

                if (selectedMember != null && selectedBook != null) {
                    LocalDate borrowDate = controller.getBorrowDate();
                    LocalDate returnDate = controller.getReturnDate();
                    borrowService.createBorrow(selectedBook.getId(), selectedMember.getId(), borrowDate, returnDate);
                    loadAllBorrows();
                    showAlert("Succès", "Demande d'emprunt créée avec succès (En attente de validation).");
                } else {
                    showAlert("Erreur", "Veuillez sélectionner un adhérent et un livre.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le formulaire : " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de créer l'emprunt : " + e.getMessage());
        }
    }

    private void handleValidateBorrow() {
        Borrow selectedBorrow = borrowTable.getSelectionModel().getSelectedItem();
        if (selectedBorrow != null) {
            if (selectedBorrow.getStatut() == Borrow.BorrowStatus.DEMANDE) {
                borrowService.validateBorrow(selectedBorrow.getId());
                handleRefresh();
                showAlert("Succès", "Emprunt validé.");
            } else {
                showAlert("Info", "Cet emprunt n'est pas en attente de validation.");
            }
        }
    }

    private void handleRefuseBorrow() {
        Borrow selectedBorrow = borrowTable.getSelectionModel().getSelectedItem();
        if (selectedBorrow != null) {
            if (selectedBorrow.getStatut() == Borrow.BorrowStatus.DEMANDE) {
                borrowService.refuseBorrow(selectedBorrow.getId());
                handleRefresh();
                showAlert("Succès", "Emprunt refusé.");
            } else {
                showAlert("Info", "Cet emprunt n'est pas en attente de validation.");
            }
        }
    }

    @FXML
    public void handleReturnBook() {
        Borrow selectedBorrow = borrowTable.getSelectionModel().getSelectedItem();
        if (selectedBorrow != null) {
            if (selectedBorrow.getDateRetourReelle() != null) {
                showAlert("Attention", "Ce livre a déjà été retourné.");
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Retourner le livre ?");
            alert.setContentText("Confirmer le retour du livre : " + selectedBorrow.getLivre().getTitre());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                borrowService.returnBook(selectedBorrow.getId());
                handleRefresh();
                showAlert("Succès", "Livre retourné.");
            }
        } else {
            showAlert("Attention", "Veuillez sélectionner un emprunt.");
        }
    }

    @FXML
    public void handleExtendBorrow() {
        Borrow selectedBorrow = borrowTable.getSelectionModel().getSelectedItem();
        if (selectedBorrow != null) {
            if (selectedBorrow.getDateRetourReelle() != null) {
                showAlert("Erreur", "Livre déjà retourné.");
                return;
            }

            if (selectedBorrow.getProlongationsCount() != null && selectedBorrow.getProlongationsCount() >= 2) {
                showAlert("Limite de prolongation", "L'emprunt a déjà été prolongé 2 fois (limite maximum).");
                return;
            }

            TextInputDialog dialog = new TextInputDialog("14");
            dialog.setTitle("Prolongation");
            dialog.setHeaderText("Prolonger l'emprunt");
            dialog.setContentText("Nombre de jours supplémentaires :");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(days -> {
                try {
                    int additionalDays = Integer.parseInt(days);
                    if (additionalDays <= 0) {
                        showAlert("Erreur", "Le nombre de jours doit être positif.");
                        return;
                    }
                    borrowService.extendBorrow(selectedBorrow.getId(), additionalDays);
                    handleRefresh();
                    showAlert("Succès", "Emprunt prolongé de " + additionalDays + " jours.");
                } catch (NumberFormatException e) {
                    showAlert("Erreur", "Veuillez entrer un nombre valide.");
                } catch (Exception e) {
                    showAlert("Erreur", "Impossible de prolonger : " + e.getMessage());
                }
            });
        } else {
            showAlert("Attention", "Veuillez sélectionner un emprunt.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

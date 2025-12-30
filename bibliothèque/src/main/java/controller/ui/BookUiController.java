package controller.ui;

import entity.Book;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
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
import service.BookService;

import java.io.IOException;
import java.util.Optional;

@Controller
public class BookUiController {

    private final BookService bookService;

    @FXML
    private TextField searchField;
    @FXML
    private TableView<Book> bookTable;
    @FXML
    private TableColumn<Book, Long> idColumn;
    @FXML
    private TableColumn<Book, String> titleColumn;
    @FXML
    private TableColumn<Book, String> authorColumn;
    @FXML
    private TableColumn<Book, String> categoryColumn;
    @FXML
    private TableColumn<Book, String> isbnColumn;
    @FXML
    private TableColumn<Book, Boolean> availableColumn;

    @Value("classpath:/view/BookFormView.fxml")
    private Resource bookFormResource;

    private final ObservableList<Book> bookList = FXCollections.observableArrayList();
    private final ApplicationContext applicationContext;

    public BookUiController(BookService bookService, ApplicationContext applicationContext) {
        this.bookService = bookService;
        this.applicationContext = applicationContext;
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getId()).asObject());
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitre()));
        authorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuteur()));
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategorie()));
        isbnColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIsbn()));
        availableColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().getDisponible()));

        loadBooks();
    }

    private void loadBooks() {
        bookList.setAll(bookService.getAllBooks());
        bookTable.setItems(bookList);
    }

    @FXML
    public void handleSearch() {
        String keyword = searchField.getText();
        if (keyword == null || keyword.isEmpty()) {
            loadBooks();
        } else {
            bookList.setAll(bookService.searchBooks(keyword));
        }
    }

    @FXML
    public void handleRefresh() {
        searchField.clear();
        loadBooks();
    }

    @FXML
    public void handleAddNew() {
        showBookForm(new Book(), "Ajouter un Livre");
    }

    @FXML
    public void handleEdit() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            showBookForm(selectedBook, "Modifier le Livre");
        } else {
            showAlert("Attention", "Veuillez sélectionner un livre à modifier.");
        }
    }

    private void showBookForm(Book book, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(bookFormResource.getURL());
            loader.setControllerFactory(applicationContext::getBean);
            Parent content = loader.load();
            BookFormController controller = loader.getController();
            controller.setBook(book);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(new DialogPane());
            dialog.getDialogPane().setContent(content);
            dialog.setTitle(title);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                controller.updateBook(book);
                if (book.getId() == null) {
                    bookService.createBook(book);
                } else {
                    bookService.updateBook(book.getId(), book);
                }
                loadBooks();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le formulaire : " + e.getMessage());
        }
    }

    @FXML
    public void handleDelete() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Supprimer le livre ?");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer : " + selectedBook.getTitre() + " ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                bookService.deleteBook(selectedBook.getId());
                bookList.remove(selectedBook);
            }
        } else {
            showAlert("Attention", "Veuillez sélectionner un livre à supprimer.");
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

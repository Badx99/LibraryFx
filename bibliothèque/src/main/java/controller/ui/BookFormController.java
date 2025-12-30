package controller.ui;

import entity.Book;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("prototype")
public class BookFormController {

    @FXML
    private TextField titleField;
    @FXML
    private TextField authorField;
    @FXML
    private TextField categoryField;
    @FXML
    private TextField isbnField;
    @FXML
    private TextField publicationYearField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private CheckBox availableBox;

    public void setBook(Book book) {
        if (book != null) {
            titleField.setText(book.getTitre());
            authorField.setText(book.getAuteur());
            categoryField.setText(book.getCategorie());
            isbnField.setText(book.getIsbn());
            if (book.getAnneePublication() != null) {
                publicationYearField.setText(String.valueOf(book.getAnneePublication()));
            }
            descriptionArea.setText(book.getDescription());
            availableBox.setSelected(book.getDisponible() != null ? book.getDisponible() : true);
        }
    }

    public void updateBook(Book book) {
        book.setTitre(titleField.getText());
        book.setAuteur(authorField.getText());
        book.setCategorie(categoryField.getText());
        book.setIsbn(isbnField.getText());
        try {
            if (publicationYearField.getText() != null && !publicationYearField.getText().isEmpty()) {
                book.setAnneePublication(Integer.parseInt(publicationYearField.getText()));
            } else {
                book.setAnneePublication(null);
            }
        } catch (NumberFormatException e) {
            book.setAnneePublication(null);
        }
        book.setDescription(descriptionArea.getText());
        book.setDisponible(availableBox.isSelected());
    }
}

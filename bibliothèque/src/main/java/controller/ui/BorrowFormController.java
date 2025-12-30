package controller.ui;

import entity.Book;
import entity.Member;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import service.BookService;
import service.MemberService;
import javafx.scene.control.DatePicker;
import java.time.LocalDate;

@Controller
@Scope("prototype")
public class BorrowFormController {

    private final BookService bookService;
    private final MemberService memberService;

    @FXML
    private ComboBox<Member> memberComboBox;
    @FXML
    private ComboBox<Book> bookComboBox;
    @FXML
    private DatePicker borrowDatePicker;
    @FXML
    private DatePicker returnDatePicker;

    public BorrowFormController(BookService bookService, MemberService memberService) {
        this.bookService = bookService;
        this.memberService = memberService;
    }

    @FXML
    public void initialize() {
        configureMemberComboBox();
        configureBookComboBox();
        loadData();
        borrowDatePicker.setValue(LocalDate.now());
        returnDatePicker.setValue(LocalDate.now().plusDays(14));
    }

    private void loadData() {
        memberComboBox.setItems(FXCollections.observableArrayList(memberService.getActiveMembers()));
        bookComboBox.setItems(FXCollections.observableArrayList(bookService.getAvailableBooks()));
    }

    public Member getSelectedMember() {
        return memberComboBox.getValue();
    }

    public Book getSelectedBook() {
        return bookComboBox.getValue();
    }

    public LocalDate getBorrowDate() {
        return borrowDatePicker.getValue();
    }

    public LocalDate getReturnDate() {
        return returnDatePicker.getValue();
    }

    private void configureMemberComboBox() {
        Callback<ListView<Member>, ListCell<Member>> cellFactory = new Callback<>() {
            @Override
            public ListCell<Member> call(ListView<Member> l) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Member item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.getNom() + " " + item.getPrenom() + " (" + item.getEmail() + ")");
                        }
                    }
                };
            }
        };
        memberComboBox.setCellFactory(cellFactory);
        memberComboBox.setButtonCell(cellFactory.call(null));
    }

    private void configureBookComboBox() {
        Callback<ListView<Book>, ListCell<Book>> cellFactory = new Callback<>() {
            @Override
            public ListCell<Book> call(ListView<Book> l) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Book item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.getTitre() + " - " + item.getAuteur());
                        }
                    }
                };
            }
        };
        bookComboBox.setCellFactory(cellFactory);
        bookComboBox.setButtonCell(cellFactory.call(null));
    }
}

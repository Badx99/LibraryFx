package controller.ui;

import entity.Member;
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
import service.MemberService;

import java.io.IOException;
import java.util.Optional;

@Controller
public class MemberUiController {

    private final MemberService memberService;

    @FXML
    private TextField searchField;
    @FXML
    private TableView<Member> memberTable;
    @FXML
    private TableColumn<Member, Long> idColumn;
    @FXML
    private TableColumn<Member, String> nomColumn;
    @FXML
    private TableColumn<Member, String> prenomColumn;
    @FXML
    private TableColumn<Member, String> emailColumn;
    @FXML
    private TableColumn<Member, String> telephoneColumn;
    @FXML
    private TableColumn<Member, Boolean> actifColumn;

    @Value("classpath:/view/MemberFormView.fxml")
    private Resource memberFormResource;

    private final ObservableList<Member> memberList = FXCollections.observableArrayList();
    private final ApplicationContext applicationContext;

    public MemberUiController(MemberService memberService, ApplicationContext applicationContext) {
        this.memberService = memberService;
        this.applicationContext = applicationContext;
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getId()).asObject());
        nomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        prenomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrenom()));
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        telephoneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTelephone()));
        actifColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().getActif()));

        loadMembers();
    }

    private void loadMembers() {
        memberList.setAll(memberService.getAllMembers());
        memberTable.setItems(memberList);
    }

    @FXML
    public void handleSearch() {
        String keyword = searchField.getText();
        if (keyword == null || keyword.isEmpty()) {
            loadMembers();
        } else {
            memberList.setAll(memberService.searchMembers(keyword));
        }
    }

    @FXML
    public void handleRefresh() {
        searchField.clear();
        loadMembers();
    }

    @FXML
    public void handleAddNew() {
        showMemberForm(new Member(), "Ajouter un Adhérent");
    }

    @FXML
    public void handleEdit() {
        Member selectedMember = memberTable.getSelectionModel().getSelectedItem();
        if (selectedMember != null) {
            showMemberForm(selectedMember, "Modifier l'Adhérent");
        } else {
            showAlert("Attention", "Veuillez sélectionner un adhérent.");
        }
    }

    private void showMemberForm(Member member, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(memberFormResource.getURL());
            loader.setControllerFactory(applicationContext::getBean);
            Parent content = loader.load();
            MemberFormController controller = loader.getController();
            controller.setMember(member);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(new DialogPane());
            dialog.getDialogPane().setContent(content);
            dialog.setTitle(title);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                controller.updateMember(member);
                if (member.getId() == null) {
                    memberService.createMember(member);
                } else {
                    memberService.updateMember(member.getId(), member);
                }
                loadMembers();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le formulaire : " + e.getMessage());
        }
    }

    @FXML
    public void handleDelete() {
        Member selectedMember = memberTable.getSelectionModel().getSelectedItem();
        if (selectedMember != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Désactiver l'adhérent ?");
            alert.setContentText("Êtes-vous sûr de vouloir désactiver : " + selectedMember.getNom() + " "
                    + selectedMember.getPrenom() + " ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                memberService.deleteMember(selectedMember.getId());
                // Refresh list to show updated status (usually deleteMember sets active=false)
                loadMembers();
            }
        } else {
            showAlert("Attention", "Veuillez sélectionner un adhérent.");
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

package controller.ui;

import entity.Member;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("prototype")
public class MemberFormController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField telephoneField;
    @FXML
    private TextField adresseField;
    @FXML
    private CheckBox actifBox;

    public void setMember(Member member) {
        if (member != null) {
            nomField.setText(member.getNom());
            prenomField.setText(member.getPrenom());
            emailField.setText(member.getEmail());
            telephoneField.setText(member.getTelephone());
            adresseField.setText(member.getAdresse());
            actifBox.setSelected(member.getActif() != null ? member.getActif() : true);
        }
    }

    public void updateMember(Member member) {
        member.setNom(nomField.getText());
        member.setPrenom(prenomField.getText());
        member.setEmail(emailField.getText());
        member.setTelephone(telephoneField.getText());
        member.setAdresse(adresseField.getText());
        member.setActif(actifBox.isSelected());
    }
}

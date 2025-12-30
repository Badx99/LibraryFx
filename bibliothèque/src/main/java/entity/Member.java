package entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "adherents")
@Data
public class Member {
	   @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @NotBlank(message = "Le nom est obligatoire")
	    @Column(nullable = false)
	    private String nom;

	    @NotBlank(message = "Le prénom est obligatoire")
	    @Column(nullable = false)
	    private String prenom;

	    @Email(message = "Email invalide")
	    @NotBlank(message = "L'email est obligatoire")
	    @Column(nullable = false, unique = true)
	    private String email;

	    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Numéro de téléphone invalide")
	    @Column(name = "telephone")
	    private String telephone;

	    @Column(name = "adresse")
	    private String adresse;

	    @Column(name = "date_inscription")
	    private LocalDateTime dateInscription;

	    @Column(nullable = false)
	    private Boolean actif = true;

	    @OneToMany(mappedBy = "adherent", cascade = CascadeType.ALL)
	    private List<Borrow> emprunts = new ArrayList<>();

	    public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getNom() {
			return nom;
		}

		public void setNom(String nom) {
			this.nom = nom;
		}

		public String getPrenom() {
			return prenom;
		}

		public void setPrenom(String prenom) {
			this.prenom = prenom;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getTelephone() {
			return telephone;
		}

		public void setTelephone(String telephone) {
			this.telephone = telephone;
		}

		public String getAdresse() {
			return adresse;
		}

		public void setAdresse(String adresse) {
			this.adresse = adresse;
		}

		public LocalDateTime getDateInscription() {
			return dateInscription;
		}

		public void setDateInscription(LocalDateTime dateInscription) {
			this.dateInscription = dateInscription;
		}

		public Boolean getActif() {
			return actif;
		}

		public void setActif(Boolean actif) {
			this.actif = actif;
		}

		public List<Borrow> getEmprunts() {
			return emprunts;
		}

		public void setEmprunts(List<Borrow> emprunts) {
			this.emprunts = emprunts;
		}

		@PrePersist
	    protected void onCreate() {
	        dateInscription = LocalDateTime.now();
	    }
}

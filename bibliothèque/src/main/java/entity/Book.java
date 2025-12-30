package entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "livres")
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Column(nullable = false)
    private String titre;

    @NotBlank(message = "L'auteur est obligatoire")
    @Column(nullable = false)
    private String auteur;

    @NotBlank(message = "La catégorie est obligatoire")
    @Column(nullable = false)
    private String categorie;

    @NotNull
    @Column(nullable = false)
    private Boolean disponible = true;

    @Column(name = "isbn", unique = true)
    private String isbn;

    @Column(name = "annee_publication")
    private Integer anneePublication;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "livre", cascade = CascadeType.ALL)
    private List<Borrow> emprunts = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    
    
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public String getAuteur() {
		return auteur;
	}

	public void setAuteur(String auteur) {
		this.auteur = auteur;
	}

	public String getCategorie() {
		return categorie;
	}

	public void setCategorie(String categorie) {
		this.categorie = categorie;
	}

	public Boolean getDisponible() {
		return disponible;
	}

	public void setDisponible(Boolean disponible) {
		this.disponible = disponible;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public Integer getAnneePublication() {
		return anneePublication;
	}

	public void setAnneePublication(Integer anneePublication) {
		this.anneePublication = anneePublication;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Borrow> getEmprunts() {
		return emprunts;
	}

	public void setEmprunts(List<Borrow> emprunts) {
		this.emprunts = emprunts;
	}

	@PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}

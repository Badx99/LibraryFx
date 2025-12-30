package entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "emprunts")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Borrow {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "livre_id", nullable = false)
	private Book livre;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "adherent_id", nullable = false)
	private Member adherent;

	@NotNull
	@Column(name = "date_emprunt", nullable = false)
	private LocalDate dateEmprunt;

	@NotNull
	@Column(name = "date_retour_prevue", nullable = false)
	private LocalDate dateRetourPrevue;

	@Column(name = "date_retour_reelle")
	private LocalDate dateRetourReelle;

	@Column(name = "retard_jours")
	private Integer retardJours;

	@Column(name = "statut")
	@Enumerated(EnumType.STRING)
	private BorrowStatus statut = BorrowStatus.EN_COURS;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "prolongations_count")
	private Integer prolongationsCount = 0;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		if (prolongationsCount == null)
			prolongationsCount = 0;
		calculateRetard();
	}

	@PreUpdate
	protected void onUpdate() {
		calculateRetard();
		updateBookStatus();
	}

	public void calculateRetard() {
		if (dateRetourReelle != null) {
			// ... existing logic for returned books ...
			if (dateRetourReelle.isAfter(dateRetourPrevue)) {
				retardJours = (int) ChronoUnit.DAYS.between(dateRetourPrevue, dateRetourReelle);
			} else {
				retardJours = 0;
			}
			statut = BorrowStatus.RETOURNE;
		} else if (LocalDate.now().isAfter(dateRetourPrevue)) {
			retardJours = (int) ChronoUnit.DAYS.between(dateRetourPrevue, LocalDate.now());
			if (retardJours <= 7) {
				statut = BorrowStatus.RETARD_LEGER;
			} else if (retardJours <= 30) {
				statut = BorrowStatus.RETARD_MOYEN;
			} else {
				statut = BorrowStatus.RETARD_GRAVE;
			}
		} else if (statut != BorrowStatus.DEMANDE && statut != BorrowStatus.REFUSE && statut != BorrowStatus.VALIDE) {
			// Keep current status if it's not late yet (e.g. EN_COURS or PROLONGE)
			retardJours = 0;
			if (statut == BorrowStatus.RETARD_LEGER || statut == BorrowStatus.RETARD_MOYEN
					|| statut == BorrowStatus.RETARD_GRAVE) {
				statut = BorrowStatus.EN_COURS; // Reset if it was late but isn't anymore (e.g. date changed)
			}
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Book getLivre() {
		return livre;
	}

	public void setLivre(Book livre) {
		this.livre = livre;
	}

	public Member getAdherent() {
		return adherent;
	}

	public void setAdherent(Member adherent) {
		this.adherent = adherent;
	}

	public LocalDate getDateEmprunt() {
		return dateEmprunt;
	}

	public void setDateEmprunt(LocalDate dateEmprunt) {
		this.dateEmprunt = dateEmprunt;
	}

	public LocalDate getDateRetourPrevue() {
		return dateRetourPrevue;
	}

	public void setDateRetourPrevue(LocalDate dateRetourPrevue) {
		this.dateRetourPrevue = dateRetourPrevue;
	}

	public LocalDate getDateRetourReelle() {
		return dateRetourReelle;
	}

	public void setDateRetourReelle(LocalDate dateRetourReelle) {
		this.dateRetourReelle = dateRetourReelle;
	}

	public Integer getRetardJours() {
		return retardJours;
	}

	public void setRetardJours(Integer retardJours) {
		this.retardJours = retardJours;
	}

	public BorrowStatus getStatut() {
		return statut;
	}

	public void setStatut(BorrowStatus statut) {
		this.statut = statut;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Integer getProlongationsCount() {
		return prolongationsCount;
	}

	public void setProlongationsCount(Integer prolongationsCount) {
		this.prolongationsCount = prolongationsCount;
	}

	private void updateBookStatus() {
		if (livre != null) {
			if (dateRetourReelle != null) {
				livre.setDisponible(true);
			} else {
				livre.setDisponible(false);
			}
		}
	}

	public enum BorrowStatus {
		DEMANDE, VALIDE, REFUSE, EN_COURS, PROLONGE, RETARD, RETARD_LEGER, RETARD_MOYEN, RETARD_GRAVE, RETOURNE
	}
}

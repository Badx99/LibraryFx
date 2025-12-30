package entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "emprunt_id", nullable = false)
	private Borrow emprunt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Borrow getEmprunt() {
		return emprunt;
	}

	public void setEmprunt(Borrow emprunt) {
		this.emprunt = emprunt;
	}

	public Member getAdherent() {
		return adherent;
	}

	public void setAdherent(Member adherent) {
		this.adherent = adherent;
	}

	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}

	public String getContenu() {
		return contenu;
	}

	public void setContenu(String contenu) {
		this.contenu = contenu;
	}

	public LocalDateTime getDateEnvoi() {
		return dateEnvoi;
	}

	public void setDateEnvoi(LocalDateTime dateEnvoi) {
		this.dateEnvoi = dateEnvoi;
	}

	public Boolean getEnvoyee() {
		return envoyee;
	}

	public void setEnvoyee(Boolean envoyee) {
		this.envoyee = envoyee;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "adherent_id", nullable = false)
	private Member adherent;

	@Column(name = "type_notification")
	@Enumerated(EnumType.STRING)
	private NotificationType type;

	@Column(name = "contenu", columnDefinition = "TEXT")
	private String contenu;

	@Column(name = "date_envoi")
	private LocalDateTime dateEnvoi;

	@Column(name = "envoyee")
	private Boolean envoyee = false;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	public enum NotificationType {
		RETARD, RAPPEL, NOUVEL_EMPRUNT, LIVRE_RENDU, PROLONGATION
	}
}

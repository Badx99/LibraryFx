package service;

import entity.Borrow;
import entity.Notification;
import DAO.BorrowRepository;
import DAO.NotificationRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final BorrowRepository borrowRepository;
    private final JavaMailSender mailSender;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Transactional
    public void sendNewBorrowNotification(Borrow emprunt) {
        Notification notification = new Notification();
        notification.setEmprunt(emprunt);
        notification.setAdherent(emprunt.getAdherent());
        notification.setType(Notification.NotificationType.NOUVEL_EMPRUNT);
        notification.setContenu(String.format(
                "Nouvel emprunt confirmé :\n" +
                        "Livre : %s\n" +
                        "Date d'emprunt : %s\n" +
                        "Date de retour prévue : %s\n" +
                        "Merci de votre confiance !",
                emprunt.getLivre().getTitre(),
                emprunt.getDateEmprunt().format(DATE_FORMATTER),
                emprunt.getDateRetourPrevue().format(DATE_FORMATTER)));
        notification.setDateEnvoi(LocalDateTime.now());

        notificationRepository.save(notification);
        sendEmailNotification(notification);
    }

    @Transactional
    public void sendBookReturnedNotification(Borrow emprunt) {
        Notification notification = new Notification();
        notification.setEmprunt(emprunt);
        notification.setAdherent(emprunt.getAdherent());
        notification.setType(Notification.NotificationType.LIVRE_RENDU);

        String contenu;
        if (emprunt.getRetardJours() != null && emprunt.getRetardJours() > 0) {
            contenu = String.format(
                    "Livre retourné avec retard :\n" +
                            "Livre : %s\n" +
                            "Retard : %d jours\n" +
                            "Amende : %.2f €\n" +
                            "Merci !",
                    emprunt.getLivre().getTitre(),
                    emprunt.getRetardJours(),
                    calculateFine(emprunt.getRetardJours()));
        } else {
            contenu = String.format(
                    "Livre retourné :\n" +
                            "Livre : %s\n" +
                            "Merci d'avoir respecté les délais !",
                    emprunt.getLivre().getTitre());
        }

        notification.setContenu(contenu);
        notification.setDateEnvoi(LocalDateTime.now());

        notificationRepository.save(notification);
        sendEmailNotification(notification);
    }

    @Transactional
    public void sendProlongationNotification(Borrow emprunt) {
        Notification notification = new Notification();
        notification.setEmprunt(emprunt);
        notification.setAdherent(emprunt.getAdherent());
        notification.setType(Notification.NotificationType.PROLONGATION);
        notification.setContenu(String.format(
                "Emprunt prolongé :\n" +
                        "Livre : %s\n" +
                        "Nouvelle date de retour prévue : %s\n" +
                        "Merci !",
                emprunt.getLivre().getTitre(),
                emprunt.getDateRetourPrevue().format(DATE_FORMATTER)));
        notification.setDateEnvoi(LocalDateTime.now());

        notificationRepository.save(notification);
        sendEmailNotification(notification);
    }

    @Scheduled(cron = "0 0 8 * * *") // Tous les jours à 8h
    @Transactional
    public void refreshBorrowStatuses() {
        List<Borrow> activeBorrows = borrowRepository.findEmpruntsEnCours();
        for (Borrow borrow : activeBorrows) {
            // This will trigger @PreUpdate which calls calculateRetard()
            borrowRepository.save(borrow);
        }
    }

    @Scheduled(cron = "0 0 9 * * *") // Tous les jours à 9h
    @Transactional
    public void checkLateBorrows() {
        List<Borrow> lateBorrows = borrowRepository.findEmpruntsEnRetard(LocalDate.now());

        for (Borrow emprunt : lateBorrows) {
            // Updated logic: if status is late, ensure they got at least one notification
            if (emprunt.getStatut() == Borrow.BorrowStatus.RETARD_LEGER ||
                    emprunt.getStatut() == Borrow.BorrowStatus.RETARD_MOYEN ||
                    emprunt.getStatut() == Borrow.BorrowStatus.RETARD_GRAVE) {

                if (!hasLateNotification(emprunt)) {
                    sendLateNotification(emprunt);
                }
            }
        }
    }

    @Scheduled(cron = "0 0 10 * * 1") // Tous les lundis à 10h
    @Transactional
    public void sendReminders() {
        List<Borrow> currentBorrows = borrowRepository.findEmpruntsEnCours();
        LocalDate threeDaysLater = LocalDate.now().plusDays(3);

        for (Borrow emprunt : currentBorrows) {
            if (emprunt.getDateRetourPrevue().isBefore(threeDaysLater) ||
                    emprunt.getDateRetourPrevue().isEqual(threeDaysLater)) {
                sendReminderNotification(emprunt);
            }
        }
    }

    private void sendLateNotification(Borrow emprunt) {
        Notification notification = new Notification();
        notification.setEmprunt(emprunt);
        notification.setAdherent(emprunt.getAdherent());
        notification.setType(Notification.NotificationType.RETARD);
        notification.setContenu(String.format(
                "RAPPEL - Livre en retard :\n" +
                        "Livre : %s\n" +
                        "Date de retour prévue : %s\n" +
                        "Retard actuel : %d jours\n" +
                        "Amende : %.2f €\n" +
                        "Merci de retourner le livre au plus vite !",
                emprunt.getLivre().getTitre(),
                emprunt.getDateRetourPrevue().format(DATE_FORMATTER),
                emprunt.getRetardJours() != null ? emprunt.getRetardJours() : 0,
                calculateFine(emprunt.getRetardJours() != null ? emprunt.getRetardJours() : 0)));
        notification.setDateEnvoi(LocalDateTime.now());

        notificationRepository.save(notification);
        sendEmailNotification(notification);
    }

    private void sendReminderNotification(Borrow emprunt) {
        Notification notification = new Notification();
        notification.setEmprunt(emprunt);
        notification.setAdherent(emprunt.getAdherent());
        notification.setType(Notification.NotificationType.RAPPEL);
        notification.setContenu(String.format(
                "RAPPEL - Échéance proche :\n" +
                        "Livre : %s\n" +
                        "Date de retour prévue : %s\n" +
                        "Merci de penser à retourner le livre à temps !",
                emprunt.getLivre().getTitre(),
                emprunt.getDateRetourPrevue().format(DATE_FORMATTER)));
        notification.setDateEnvoi(LocalDateTime.now());

        notificationRepository.save(notification);
        sendEmailNotification(notification);
    }

    private boolean hasLateNotification(Borrow emprunt) {
        List<Notification> notifications = notificationRepository.findByAdherentId(emprunt.getAdherent().getId());
        return notifications.stream()
                .anyMatch(n -> n.getType() == Notification.NotificationType.RETARD &&
                        n.getEmprunt().getId().equals(emprunt.getId()));
    }

    private double calculateFine(int daysLate) {
        return daysLate * 0.50; // 0.50€ par jour de retard
    }

    private void sendEmailNotification(Notification notification) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(notification.getAdherent().getEmail());
            helper.setSubject(getEmailSubject(notification.getType()));
            helper.setText(notification.getContenu());

            mailSender.send(message);
            notification.setEnvoyee(true);
            notificationRepository.save(notification);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
        }
    }

    private String getEmailSubject(Notification.NotificationType type) {
        return switch (type) {
            case RETARD -> "RAPPEL URGENT - Livre en retard - Bibliothèque";
            case RAPPEL -> "Rappel - Échéance proche - Bibliothèque";
            case NOUVEL_EMPRUNT -> "Confirmation d'emprunt - Bibliothèque";
            case LIVRE_RENDU -> "Confirmation de retour - Bibliothèque";
            case PROLONGATION -> "Confirmation de prolongation - Bibliothèque";
        };
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByMember(Long adherentId) {
        return notificationRepository.findByAdherentId(adherentId);
    }

    @Transactional(readOnly = true)
    public List<Notification> getPendingNotifications() {
        return notificationRepository.findByEnvoyeeFalse();
    }

    @Transactional(readOnly = true)
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Notification> getSentNotifications() {
        return notificationRepository.findByEnvoyeeTrue();
    }
}

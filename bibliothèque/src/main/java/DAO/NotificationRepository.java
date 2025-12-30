package DAO;

import entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Override
    @Query("SELECT n FROM Notification n LEFT JOIN FETCH n.adherent LEFT JOIN FETCH n.emprunt e LEFT JOIN FETCH e.livre LEFT JOIN FETCH e.adherent")
    List<Notification> findAll();

    @Query("SELECT n FROM Notification n LEFT JOIN FETCH n.adherent LEFT JOIN FETCH n.emprunt e LEFT JOIN FETCH e.livre LEFT JOIN FETCH e.adherent WHERE n.envoyee = false")
    List<Notification> findByEnvoyeeFalse();

    @Query("SELECT n FROM Notification n LEFT JOIN FETCH n.adherent LEFT JOIN FETCH n.emprunt e LEFT JOIN FETCH e.livre LEFT JOIN FETCH e.adherent WHERE n.envoyee = true")
    List<Notification> findByEnvoyeeTrue();

    @Query("SELECT n FROM Notification n LEFT JOIN FETCH n.adherent LEFT JOIN FETCH n.emprunt e LEFT JOIN FETCH e.livre LEFT JOIN FETCH e.adherent WHERE n.adherent.id = :adherentId")
    List<Notification> findByAdherentId(@Param("adherentId") Long adherentId);
}

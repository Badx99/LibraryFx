package DAO;

import entity.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long> {

    @Override
    @Query("SELECT e FROM Borrow e LEFT JOIN FETCH e.livre LEFT JOIN FETCH e.adherent")
    List<Borrow> findAll();

    @Query("SELECT e FROM Borrow e LEFT JOIN FETCH e.livre LEFT JOIN FETCH e.adherent WHERE e.adherent.id = :adherentId")
    List<Borrow> findByAdherentId(@Param("adherentId") Long adherentId);

    @Query("SELECT e FROM Borrow e LEFT JOIN FETCH e.livre LEFT JOIN FETCH e.adherent WHERE e.livre.id = :livreId")
    List<Borrow> findByLivreId(@Param("livreId") Long livreId);

    @Query("SELECT e FROM Borrow e LEFT JOIN FETCH e.livre LEFT JOIN FETCH e.adherent WHERE e.dateRetourReelle IS NULL AND e.dateRetourPrevue < :date")
    List<Borrow> findEmpruntsEnRetard(@Param("date") LocalDate date);

    @Query("SELECT e FROM Borrow e LEFT JOIN FETCH e.livre LEFT JOIN FETCH e.adherent WHERE e.dateRetourReelle IS NULL")
    List<Borrow> findEmpruntsEnCours();

    @Query("SELECT e FROM Borrow e LEFT JOIN FETCH e.livre LEFT JOIN FETCH e.adherent WHERE e.statut = :statut")
    List<Borrow> findByStatut(@Param("statut") Borrow.BorrowStatus statut);

    @Query("SELECT e FROM Borrow e LEFT JOIN FETCH e.livre LEFT JOIN FETCH e.adherent WHERE e.adherent.email = :email AND e.dateRetourReelle IS NULL")
    List<Borrow> findEmpruntsEnCoursByEmail(@Param("email") String email);
}

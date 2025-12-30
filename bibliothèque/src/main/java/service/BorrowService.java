package service;

import entity.Book;
import entity.Borrow;
import entity.Member;
import DAO.BorrowRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowService {
    private final BorrowRepository borrowRepository;
    private final BookService bookService;
    private final MemberService memberService;
    private final NotificationService notificationService;

    @Transactional
    public Borrow createBorrow(Long livreId, Long adherentId) {
        return createBorrow(livreId, adherentId, LocalDate.now(), LocalDate.now().plusWeeks(2));
    }

    @Transactional
    public Borrow createBorrow(Long livreId, Long adherentId, LocalDate dateEmprunt, LocalDate dateRetourPrevue) {
        Book livre = bookService.getBookById(livreId);
        Member adherent = memberService.getMemberById(adherentId);

        if (!livre.getDisponible()) {
            throw new IllegalStateException("Le livre n'est pas disponible");
        }

        Borrow emprunt = new Borrow();
        emprunt.setLivre(livre);
        emprunt.setAdherent(adherent);
        emprunt.setDateEmprunt(dateEmprunt != null ? dateEmprunt : LocalDate.now());
        emprunt.setDateRetourPrevue(dateRetourPrevue != null ? dateRetourPrevue : LocalDate.now().plusWeeks(2));
        emprunt.setStatut(Borrow.BorrowStatus.DEMANDE);

        // Mettre à jour la disponibilité du livre
        livre.setDisponible(false);

        Borrow savedEmprunt = borrowRepository.save(emprunt);

        // Envoyer une notification
        notificationService.sendNewBorrowNotification(savedEmprunt);

        return savedEmprunt;
    }

    @Transactional
    public Borrow returnBook(Long empruntId) {
        Borrow emprunt = getBorrowById(empruntId);

        emprunt.setDateRetourReelle(LocalDate.now());
        emprunt.calculateRetard(); // Ensure retardJours is updated immediately
        emprunt.setStatut(Borrow.BorrowStatus.RETOURNE);

        // Mettre à jour la disponibilité du livre
        emprunt.getLivre().setDisponible(true);

        Borrow returnedEmprunt = borrowRepository.save(emprunt);

        // Envoyer une notification
        notificationService.sendBookReturnedNotification(returnedEmprunt);

        return returnedEmprunt;
    }

    @Transactional(readOnly = true)
    public Borrow getBorrowById(Long id) {
        return borrowRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Emprunt non trouvé avec l'ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Borrow> getAllBorrows() {
        return borrowRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Borrow> getBorrowsByMember(Long adherentId) {
        return borrowRepository.findByAdherentId(adherentId);
    }

    @Transactional(readOnly = true)
    public List<Borrow> getCurrentBorrows() {
        return borrowRepository.findEmpruntsEnCours();
    }

    @Transactional(readOnly = true)
    public List<Borrow> getLateBorrows() {
        return borrowRepository.findEmpruntsEnRetard(LocalDate.now());
    }

    @Transactional
    public void deleteBorrow(Long id) {
        Borrow emprunt = getBorrowById(id);

        // Remettre le livre disponible si l'emprunt est supprimé
        if (emprunt.getDateRetourReelle() == null) {
            emprunt.getLivre().setDisponible(true);
        }

        borrowRepository.delete(emprunt);
    }

    @Transactional(readOnly = true)
    public List<Borrow> getBorrowsByStatus(Borrow.BorrowStatus statut) {
        return borrowRepository.findByStatut(statut);
    }

    @Transactional
    public Borrow extendBorrow(Long empruntId, int additionalDays) {
        Borrow emprunt = getBorrowById(empruntId);

        if (emprunt.getDateRetourReelle() != null) {
            throw new IllegalStateException("Le livre a déjà été retourné");
        }

        if (emprunt.getProlongationsCount() != null && emprunt.getProlongationsCount() >= 2) {
            throw new IllegalStateException("Limite de prolongations atteinte (max 2)");
        }

        emprunt.setDateRetourPrevue(emprunt.getDateRetourPrevue().plusDays(additionalDays));
        emprunt.setStatut(Borrow.BorrowStatus.PROLONGE);
        emprunt.setProlongationsCount(
                (emprunt.getProlongationsCount() != null ? emprunt.getProlongationsCount() : 0) + 1);

        Borrow savedEmprunt = borrowRepository.save(emprunt);
        notificationService.sendProlongationNotification(savedEmprunt);
        return savedEmprunt;
    }

    @Transactional
    public void validateBorrow(Long borrowId) {
        Borrow borrow = getBorrowById(borrowId);
        borrow.setStatut(Borrow.BorrowStatus.EN_COURS);
        borrowRepository.save(borrow);
    }

    @Transactional
    public void refuseBorrow(Long borrowId) {
        Borrow borrow = getBorrowById(borrowId);
        borrow.setStatut(Borrow.BorrowStatus.REFUSE);
        borrow.getLivre().setDisponible(true);
        borrowRepository.save(borrow);
    }
}

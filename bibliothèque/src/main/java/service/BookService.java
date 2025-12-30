package service;
import entity.Book;
import DAO.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    @Transactional
    public Book createBook(Book book) {
        if (book.getIsbn() != null && bookRepository.existsByIsbn(book.getIsbn())) {
            throw new IllegalArgumentException("Un livre avec cet ISBN existe déjà");
        }
        return bookRepository.save(book);
    }

    @Transactional(readOnly = true)
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livre non trouvé avec l'ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Book> getAvailableBooks() {
        return bookRepository.findByDisponibleTrue();
    }

    @Transactional
    public Book updateBook(Long id, Book bookDetails) {
        Book book = getBookById(id);
        
        if (bookDetails.getTitre() != null) book.setTitre(bookDetails.getTitre());
        if (bookDetails.getAuteur() != null) book.setAuteur(bookDetails.getAuteur());
        if (bookDetails.getCategorie() != null) book.setCategorie(bookDetails.getCategorie());
        if (bookDetails.getDisponible() != null) book.setDisponible(bookDetails.getDisponible());
        if (bookDetails.getIsbn() != null) book.setIsbn(bookDetails.getIsbn());
        if (bookDetails.getAnneePublication() != null) book.setAnneePublication(bookDetails.getAnneePublication());
        if (bookDetails.getDescription() != null) book.setDescription(bookDetails.getDescription());
        
        return bookRepository.save(book);
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = getBookById(id);
        bookRepository.delete(book);
    }

    @Transactional(readOnly = true)
    public List<Book> searchBooks(String keyword) {
        return bookRepository.searchByKeyword(keyword);
    }

    @Transactional(readOnly = true)
    public List<Book> searchByTitle(String titre) {
        return bookRepository.findByTitreContainingIgnoreCase(titre);
    }

    @Transactional(readOnly = true)
    public List<Book> searchByAuthor(String auteur) {
        return bookRepository.findByAuteurContainingIgnoreCase(auteur);
    }

    @Transactional(readOnly = true)
    public List<Book> searchByCategory(String categorie) {
        return bookRepository.findByCategorieContainingIgnoreCase(categorie);
    }
}

package DAO;
import entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>{
	   List<Book> findByDisponibleTrue();
	    List<Book> findByTitreContainingIgnoreCase(String titre);
	    List<Book> findByAuteurContainingIgnoreCase(String auteur);
	    List<Book> findByCategorieContainingIgnoreCase(String categorie);
	    
	    @Query("SELECT b FROM Book b WHERE " +
	           "LOWER(b.titre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
	           "LOWER(b.auteur) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
	           "LOWER(b.categorie) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
	           "LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	    List<Book> searchByKeyword(@Param("keyword") String keyword);
	    
	    boolean existsByIsbn(String isbn);
}

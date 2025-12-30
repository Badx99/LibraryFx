package DAO;
import entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>{
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
    
    @Query("SELECT m FROM Member m WHERE " +
           "LOWER(m.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Member> searchByKeyword(@Param("keyword") String keyword);
    
    List<Member> findByActifTrue();
}

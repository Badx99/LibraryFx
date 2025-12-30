package service;
import entity.Member;
import DAO.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
	  private final MemberRepository memberRepository;

	    @Transactional
	    public Member createMember(Member member) {
	        if (memberRepository.existsByEmail(member.getEmail())) {
	            throw new IllegalArgumentException("Un adhérent avec cet email existe déjà");
	        }
	        return memberRepository.save(member);
	    }

	    @Transactional(readOnly = true)
	    public Member getMemberById(Long id) {
	        return memberRepository.findById(id)
	                .orElseThrow(() -> new EntityNotFoundException("Adhérent non trouvé avec l'ID: " + id));
	    }

	    @Transactional(readOnly = true)
	    public List<Member> getAllMembers() {
	        return memberRepository.findAll();
	    }

	    @Transactional(readOnly = true)
	    public List<Member> getActiveMembers() {
	        return memberRepository.findByActifTrue();
	    }

	    @Transactional
	    public Member updateMember(Long id, Member memberDetails) {
	        Member member = getMemberById(id);
	        
	        if (memberDetails.getNom() != null) member.setNom(memberDetails.getNom());
	        if (memberDetails.getPrenom() != null) member.setPrenom(memberDetails.getPrenom());
	        if (memberDetails.getEmail() != null) {
	            if (!member.getEmail().equals(memberDetails.getEmail()) && 
	                memberRepository.existsByEmail(memberDetails.getEmail())) {
	                throw new IllegalArgumentException("Un adhérent avec cet email existe déjà");
	            }
	            member.setEmail(memberDetails.getEmail());
	        }
	        if (memberDetails.getTelephone() != null) member.setTelephone(memberDetails.getTelephone());
	        if (memberDetails.getAdresse() != null) member.setAdresse(memberDetails.getAdresse());
	        if (memberDetails.getActif() != null) member.setActif(memberDetails.getActif());
	        
	        return memberRepository.save(member);
	    }

	    @Transactional
	    public void deleteMember(Long id) {
	        Member member = getMemberById(id);
	        member.setActif(false);
	        memberRepository.save(member);
	    }

	    @Transactional(readOnly = true)
	    public List<Member> searchMembers(String keyword) {
	        return memberRepository.searchByKeyword(keyword);
	    }

	    @Transactional(readOnly = true)
	    public Member getMemberByEmail(String email) {
	        return memberRepository.findByEmail(email)
	                .orElseThrow(() -> new EntityNotFoundException("Adhérent non trouvé avec l'email: " + email));
	    }
}

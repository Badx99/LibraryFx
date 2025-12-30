package service;
import entity.Administrateur;
import DAO.AdministrateurRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdministrateurService {
    private final AdministrateurRepository administrateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Administrateur createAdmin(Administrateur admin) {
        if (administrateurRepository.existsByEmail(admin.getEmail())) {
            throw new IllegalArgumentException("Un administrateur avec cet email existe déjà");
        }
        
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return administrateurRepository.save(admin);
    }

    @Transactional(readOnly = true)
    public Administrateur getAdminById(Long id) {
        return administrateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Administrateur non trouvé avec l'ID: " + id));
    }

    @Transactional(readOnly = true)
    public Administrateur getAdminByEmail(String email) {
        return administrateurRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Administrateur non trouvé avec l'email: " + email));
    }

    @Transactional
    public Administrateur updateAdmin(Long id, Administrateur adminDetails) {
        Administrateur admin = getAdminById(id);
        
        if (adminDetails.getNom() != null) admin.setNom(adminDetails.getNom());
        if (adminDetails.getPrenom() != null) admin.setPrenom(adminDetails.getPrenom());
        if (adminDetails.getEmail() != null) {
            if (!admin.getEmail().equals(adminDetails.getEmail()) && 
                administrateurRepository.existsByEmail(adminDetails.getEmail())) {
                throw new IllegalArgumentException("Un administrateur avec cet email existe déjà");
            }
            admin.setEmail(adminDetails.getEmail());
        }
        if (adminDetails.getPassword() != null) {
            admin.setPassword(passwordEncoder.encode(adminDetails.getPassword()));
        }
        if (adminDetails.getActif() != null) admin.setActif(adminDetails.getActif());
        
        return administrateurRepository.save(admin);
    }

    @Transactional
    public void updateLastLogin(String email) {
        Administrateur admin = getAdminByEmail(email);
        admin.setLastLogin(LocalDateTime.now());
        administrateurRepository.save(admin);
    }

    @Transactional
    public void deleteAdmin(Long id) {
        Administrateur admin = getAdminById(id);
        admin.setActif(false);
        administrateurRepository.save(admin);
    }

    @Transactional(readOnly = true)
    public boolean validateCredentials(String email, String password) {
        return administrateurRepository.findByEmail(email)
                .map(admin -> passwordEncoder.matches(password, admin.getPassword()))
                .orElse(false);
    }


}

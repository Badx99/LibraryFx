package config;

import entity.Administrateur;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import service.AdministrateurService;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(AdministrateurService adminService) {
        return args -> {
            try {
                adminService.getAdminByEmail("admin@library.com");
            } catch (Exception e) {
                // Admin not found, create one
                Administrateur admin = new Administrateur();
                admin.setNom("Admin");
                admin.setPrenom("System");
                admin.setEmail("admin@library.com");
                admin.setPassword("password"); // Will be encoded by service
                admin.setRole("ROLE_ADMIN");
                admin.setActif(true);

                adminService.createAdmin(admin);
                System.out.println("Default Admin created: admin@library.com / password");
            }
        };
    }
}

package config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseFix {

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void fixConstraints() {
        try {
            log.info("Checking and fixing database constraints...");

            // Drop the old constraint if it exists.
            // In PostgreSQL, hibernate creates a check constraint for enums.
            jdbcTemplate.execute("ALTER TABLE emprunts DROP CONSTRAINT IF EXISTS emprunts_statut_check");

            // Add the new constraint with all values
            jdbcTemplate.execute("ALTER TABLE emprunts ADD CONSTRAINT emprunts_statut_check " +
                    "CHECK (statut IN ('DEMANDE', 'VALIDE', 'REFUSE', 'EN_COURS', 'PROLONGE', 'RETARD', 'RETARD_LEGER', 'RETARD_MOYEN', 'RETARD_GRAVE', 'RETOURNE'))");

            // Fix notifications table constraint
            jdbcTemplate.execute(
                    "ALTER TABLE notifications DROP CONSTRAINT IF EXISTS notifications_type_notification_check");
            jdbcTemplate.execute("ALTER TABLE notifications ADD CONSTRAINT notifications_type_notification_check " +
                    "CHECK (type_notification IN ('RETARD', 'RAPPEL', 'NOUVEL_EMPRUNT', 'LIVRE_RENDU', 'PROLONGATION'))");

            log.info("Database constraints fixed successfully.");
        } catch (Exception e) {
            log.error("Failed to fix database constraints: {}", e.getMessage());
        }
    }
}

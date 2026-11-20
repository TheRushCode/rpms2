package com.kiranacademy.rpms.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${spring.datasource.url:${DATABASE_URL:}}")
    private String rawUrl;

    @Value("${spring.datasource.username:${DB_USERNAME:}}")
    private String defaultUsername;

    @Value("${spring.datasource.password:${DB_PASSWORD:}}")
    private String defaultPassword;

    @Bean
    @Primary
    @Profile("!test")
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();

        String dbUrl = rawUrl != null ? rawUrl.trim() : "";
        String username = defaultUsername;
        String password = defaultPassword;

        // Support Render / Heroku postgres:// or postgresql:// connection strings
        if (dbUrl.startsWith("postgres://") || (dbUrl.startsWith("postgresql://") && !dbUrl.startsWith("jdbc:"))) {
            try {
                // Ensure URI can parse (some use postgresql:// which URI supports)
                String uriStr = dbUrl.startsWith("postgres://") ? dbUrl.replaceFirst("postgres://", "http://") : dbUrl.replaceFirst("postgresql://", "http://");
                URI uri = new URI(uriStr);

                String userInfo = uri.getUserInfo();
                if (userInfo != null && userInfo.contains(":")) {
                    String[] parts = userInfo.split(":", 2);
                    username = parts[0];
                    password = parts[1];
                }

                int port = uri.getPort() > 0 ? uri.getPort() : 5432;
                String path = uri.getPath();
                String cleanJdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port + path;

                log.info("Detected Render PostgreSQL URL. Configured host={}, port={}, database={}", uri.getHost(), port, path);
                dbUrl = cleanJdbcUrl;
            } catch (Exception e) {
                log.warn("Could not parse DATABASE_URL as URI, using as-is: {}", e.getMessage());
            }
        }

        if (dbUrl.isEmpty()) {
            dbUrl = "jdbc:postgresql://localhost:5432/rpms_db";
        }
        if (username == null || username.isEmpty()) {
            username = "postgres";
        }
        if (password == null) {
            password = "";
        }

        config.setJdbcUrl(dbUrl);
        config.setUsername(username);
        config.setPassword(password);

        if (dbUrl.contains("h2:mem")) {
            config.setDriverClassName("org.h2.Driver");
        } else {
            config.setDriverClassName("org.postgresql.Driver");
        }

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        return new HikariDataSource(config);
    }

    @Bean
    @Profile("!test")
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            log.info("Executing Flyway repair to clear any failed migration history...");
            try {
                flyway.repair();
            } catch (Exception e) {
                log.warn("Flyway repair encountered an issue (continuing to migrate): {}", e.getMessage());
            }
            log.info("Executing Flyway migrate...");
            flyway.migrate();
        };
    }
}

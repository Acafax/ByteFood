package org.example.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;

@Component
@Profile("!test")
public class SqlScriptRunner {

    private final DataSource dataSource;
    private final ResourceLoader resourceLoader;

    public SqlScriptRunner(DataSource dataSource, ResourceLoader resourceLoader) {
        this.dataSource = dataSource;
        this.resourceLoader = resourceLoader;
    }

    @EventListener
    public void RunSqlAfterStartup(ApplicationReadyEvent event) {
        System.out.println("✅ Aplikacja w pełni uruchomiona. Uruchamiam skrypt SQL...");
        try (Connection connection = dataSource.getConnection()) {
            Resource resource = resourceLoader.getResource("classpath:db-init.sql");

            // To jest bezpieczna metoda Springa do uruchamiania skryptów SQL:
            ScriptUtils.executeSqlScript(connection, resource);

            System.out.println("✅ Skrypt SQL wykonany poprawnie (użyto ScriptUtils)");
        } catch (Exception e) {
            System.err.println("❌ Błąd podczas wykonywania skryptu SQL: " + e.getMessage());
            e.printStackTrace(); // Warto widzieć pełny stack trace
        }
    }


}

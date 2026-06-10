package org.example.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Slf4j
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
        log.info("Application ready — running SQL init script");
        try (Connection connection = dataSource.getConnection()) {
            Resource resource = resourceLoader.getResource("classpath:/db/db-init.sql");
            ScriptUtils.executeSqlScript(connection, resource);
            log.info("SQL init script executed successfully");
        } catch (Exception e) {
            log.error("Failed to execute SQL init script", e);
        }
    }
}

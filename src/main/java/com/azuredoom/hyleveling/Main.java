package com.azuredoom.hyleveling;

import com.azuredoom.hyleveling.database.DataSourceFactory;
import com.azuredoom.hyleveling.database.JdbcLevelRepository;
import com.azuredoom.hyleveling.level.LevelServiceImpl;
import com.azuredoom.hyleveling.level.formulas.ExponentialLevelFormula;
import com.azuredoom.hyleveling.level.formulas.LevelFormula;

import java.util.UUID;

public class Main {

    private static final System.Logger LOGGER = System.getLogger(Main.class.getName());

    /**
     * Bootstrap / example entrypoint for HyLeveling.
     * <p>
     * This demonstrates wiring a {@link LevelFormula} and a JDBC-backed
     * {@link com.azuredoom.hyleveling.database.LevelRepository} into
     * {@link com.azuredoom.hyleveling.level.LevelServiceImpl}.
     * <h2>Supported JDBC URLs</h2>
     * <ul>
     * <li><b>H2 (file)</b>: {@code jdbc:h2:file:./data/hyleveling;AUTO_SERVER=TRUE;MODE=PostgreSQL}
     * <ul>
     * <li>{@code AUTO_SERVER=TRUE} allows multiple processes to access the same database file.</li>
     * <li>{@code MODE=PostgreSQL} enables PostgreSQL-like SQL behavior (optional, but helps portability).</li>
     * <li>H2 commonly uses empty credentials; pass {@code ""} for username and password unless configured
     * otherwise.</li>
     * </ul>
     * </li>
     * <li><b>MySQL</b>: {@code jdbc:mysql://host:3306/dbname}</li>
     * <li><b>MariaDB</b>: {@code jdbc:mariadb://host:3306/dbname}</li>
     * <li><b>PostgreSQL</b>: {@code jdbc:postgresql://host:5432/dbname}</li>
     * </ul>
     * <h2>Shutdown</h2>
     * <p>
     * A shutdown hook is registered to close the repository (and underlying connection pool, if used), ensuring
     * resources are released cleanly.
     */
    static void main() {
        var formula = new ExponentialLevelFormula(100, 1.7);
        var jdbcUrl = "jdbc:h2:file:./data/hyleveling;AUTO_SERVER=TRUE;MODE=PostgreSQL";
        var dataSource = DataSourceFactory.create(jdbcUrl, "", "");
        var repository = new JdbcLevelRepository(dataSource);
        var levelService = new LevelServiceImpl(formula, repository);
        var testId = UUID.fromString("d3804858-4bb8-4026-ae21-386255ed467d");

        levelService.addXp(testId, 500);

        LOGGER.log(System.Logger.Level.INFO, String.format("XP: %d", levelService.getXp(testId)));
        LOGGER.log(System.Logger.Level.INFO, String.format("Level: %d", levelService.getLevel(testId)));

        Runtime.getRuntime().addShutdownHook(new Thread(repository::close));
    }
}

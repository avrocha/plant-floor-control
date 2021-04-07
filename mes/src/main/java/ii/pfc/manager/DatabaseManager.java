package ii.pfc.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseManager implements IDatabaseManager{

    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    //

    private BasicDataSource dataSource = new BasicDataSource();

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public boolean openConnection() {
        dataSource.setUrl("jdbc:postgresql://5.249.10.25:5432/postgres");
        dataSource.setUsername("postgres");
        dataSource.setPassword(System.getenv("DB_PASSWORD"));
        dataSource.setMinIdle(5);
        dataSource.setMaxIdle(10);
        dataSource.setMaxOpenPreparedStatements(100);

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("SELECT 1")) {
                if(!sql.execute()) {
                    logger.error("Could not connect to SQL database.");
                    return false;
                }
            }

        } catch(SQLException ex) {
            ex.printStackTrace();
        }

        return true;

    }

    @Override
    public void closeConnection() {
        try {
            dataSource.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

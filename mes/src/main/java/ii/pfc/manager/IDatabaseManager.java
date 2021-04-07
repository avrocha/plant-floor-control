package ii.pfc.manager;

import java.sql.Connection;
import java.sql.SQLException;

public interface IDatabaseManager {

    Connection getConnection() throws SQLException;

    boolean openConnection();

    void closeConnection();

}

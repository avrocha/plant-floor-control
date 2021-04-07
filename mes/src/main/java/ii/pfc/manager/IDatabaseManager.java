package ii.pfc.manager;

import ii.pfc.order.UnloadOrder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

public interface IDatabaseManager {

    Connection getConnection() throws SQLException;

    boolean openConnection();

    void closeConnection();

    /*

     */

    Collection<UnloadOrder> fetchUnloadOrders();
}

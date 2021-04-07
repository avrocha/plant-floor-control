package ii.pfc.manager;

import junit.framework.TestCase;
import org.junit.Test;

public class TestDB extends TestCase {

    private IDatabaseManager databaseManager;

    @Override
    protected void setUp() throws Exception {
        databaseManager = new DatabaseManager();
    }

    @Test
    public void testDB() {
        databaseManager.openConnection();

        System.out.println(databaseManager.fetchUnloadOrders().toString());

        databaseManager.closeConnection();
    }

}

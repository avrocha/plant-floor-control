package ii.pfc.manager;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import ii.pfc.conveyor.Conveyor;
import ii.pfc.order.LoadOrder;
import ii.pfc.order.TransformationOrder;
import ii.pfc.order.UnloadOrder;
import ii.pfc.part.Part;
import ii.pfc.part.PartType;
import ii.pfc.part.Process;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.crypto.dsig.Transform;

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


    /*

     */

    @Override
    public Collection<UnloadOrder> fetchUnloadOrders() {
        List<UnloadOrder>orders = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("SELECT * FROM unload_order;")) {
                ResultSet result = sql.executeQuery();
                while(result.next()) {
                    UnloadOrder order = new UnloadOrder(
                            result.getInt("order_id"),
                            PartType.getType(result.getString("type")),
                            result.getInt("conveyor_id"),
                            result.getDate("date"),
                            result.getInt("quantity")
                            );
                    orders.add(order);
                }
            }

        } catch(SQLException ex) {
            ex.printStackTrace();
        }

        return orders;
    }

    @Override
    public Collection<TransformationOrder> fetchAllTransformOrders() {
        List<TransformationOrder>orders = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("SELECT * FROM transform_order;")) {
                ResultSet result = sql.executeQuery();
                while(result.next()) {
                    TransformationOrder order = new TransformationOrder(
                            result.getInt("order_id"),
                            PartType.getType(result.getString("source_type")),
                            PartType.getType(result.getString("target_type")),
                            result.getDate("date"),
                            result.getInt("quantity"),
                            result.getDate("deadline"),
                            result.getInt("penalty")
                    );
                    orders.add(order);
                }
            }

        } catch(SQLException ex) {
            ex.printStackTrace();
        }

        return orders;
    }

    @Override
    public Collection<Part> fetchPart() {
        List<Part>parts = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("SELECT * FROM part;")) {
                ResultSet result = sql.executeQuery();
                while(result.next()) {
                    Part part = new Part(
                            UUID.fromString(result.getString("id")),
                            PartType.getType(result.getString("type"))
                    );
                    parts.add(part);
                }
            }

        } catch(SQLException ex) {
            ex.printStackTrace();
        }

        return parts;
    }

    @Override
    public Collection<Part> fetchUnloadedParts() {
        List<Part>parts = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("SELECT * FROM unloading_bay_log;")) {
                ResultSet result = sql.executeQuery();
                while(result.next()) {
                    Part part = new Part(
                            UUID.fromString(result.getString("unloading_part")),
                            PartType.getType(result.getString("unloading_type"))
                    );
                    parts.add(part);
                }
            }

        } catch(SQLException ex) {
            ex.printStackTrace();
        }

        return parts;
    }

    /*@Override /*INCOMPLETO*/
    /*public void updatePart() {

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("SELECT * FROM part UPDATE part")) {
                ResultSet result = sql.executeQuery();
            }

        } catch(SQLException ex) {
            ex.printStackTrace();
        }

    }*/

    @Override
    public void updateProcessLog(Process process, Conveyor assembler, Part part) {

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("INSERT INTO process_log (assembler_id, duration, part_source_type, part_target_type, part_id) " +
                    "VALUES (?, ?, ?, ?, ?) ")) {
                sql.setInt(1,assembler.getId());
                sql.setInt(2, (int)process.getDuration().toSeconds());
                sql.setString(3, process.getSource().getName());
                sql.setString(4, process.getResult().getName());
                sql.setString(5, part.getId().toString());
            }

        } catch(SQLException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void updateUnloadingBayLog(UnloadOrder unloadingOrder, Part part) {

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("INSERT INTO unloading_bay_log (conveyor_id, unloading_part, unloading_type) " +
                    "VALUES (?, ?, ?) ")) {
                sql.setInt(1, unloadingOrder.getConveyorId());
                sql.setString(2, part.getId().toString());
                sql.setString(3, unloadingOrder.getPartType().getName());
            }

        } catch(SQLException ex) {
            ex.printStackTrace();
        }

    }

    @Override /*VERIFICAR*/
    public void updateTransformOrder(TransformationOrder transformationOrder) {

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("INSERT INTO transform_order (order_id, date, quantity, penalty, source_type, target_type, deadline) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) ")) {
                sql.setInt(1, transformationOrder.getOrderId());
                sql.setInt(2, transformationOrder.getDate().getDate());
                sql.setInt(3, transformationOrder.getQuantity());
                sql.setInt(4, transformationOrder.getDayPenalty());
                sql.setString(5, transformationOrder.getSourceType().toString());
                sql.setString(6, transformationOrder.getTargetType().toString());
                sql.setInt(7, transformationOrder.getDeadline().getDate());
            }

        } catch(SQLException ex) {
            ex.printStackTrace();
        }

    }

    @Override /*VERIFICAR*/
    public void updateUnloadOrder(UnloadOrder unloadOrder) {

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("INSERT INTO unload_order (order_id, conveyor_id, date, quantity, type) " +
                    "VALUES (?, ?, ?, ?, ?) ")) {
                sql.setInt(1, unloadOrder.getOrderId());
                sql.setInt(2, unloadOrder.getConveyorId());
                sql.setInt(3, unloadOrder.getDate().getDate());
                sql.setInt(4, unloadOrder.getQuantity());
                sql.setString(1, unloadOrder.getPartType().getName());
            }

        } catch(SQLException ex) {
            ex.printStackTrace();
        }

    }

    @Override /*VERIFICAR*/
    public void updateLoadOrder(LoadOrder loadOrder, Part part) {

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("INSERT INTO load_order (order_id, conveyor_id, date, type) " +
                    "VALUES (?, ?, ?, ?) ")) {
                sql.setInt(1, loadOrder.getOrderId());
                sql.setInt(2, loadOrder.getConveyorId());
                sql.setInt(3, loadOrder.getDate().getDate());
                sql.setString(4, part.getType().getName());
            }

        } catch(SQLException ex) {
            ex.printStackTrace();
        }

    }

    /*@Override /*COM ERROS*/
    /*public Collection<Process> fetchProcessDuration() {
        List<Process>processes = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("SELECT * FROM process_log;")) {
                ResultSet result = sql.executeQuery();
                while(result.next()) {
                    Process process = new Process(
                            result.getInt("assembler_id"),
                            PartType.getType(result.getString("part_source_type")),
                            PartType.getType(result.getString("part_target_type")),
                            result.getString("duration")
                    );
                    processes.add(process);
                }
            }

        } catch(SQLException ex) {
            ex.printStackTrace();
        }

        return processes;
    }*/
}

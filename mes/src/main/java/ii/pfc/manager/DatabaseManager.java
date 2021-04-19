package ii.pfc.manager;

import java.sql.*;
import java.time.Duration;
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
import org.postgresql.util.PGInterval;
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
                            result.getTimestamp("date"),
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
                            result.getTimestamp("date"),
                            result.getInt("quantity"),
                            result.getTimestamp("deadline"),
                            result.getInt("penalty"),
                            TransformationOrder.TransformationState.valueOf(result.getString("state")));
                    orders.add(order);
                }
            }

        } catch(SQLException ex) {
            ex.printStackTrace();
        }

        return orders;
    }

    @Override
    public Collection<TransformationOrder> fetchPendingTransformOrders() {
        List<TransformationOrder>orders = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("SELECT * FROM transform_order WHERE state = 'PENDING';")) {
                ResultSet result = sql.executeQuery();
                while(result.next()) {
                    TransformationOrder order = new TransformationOrder(
                            result.getInt("order_id"),
                            PartType.getType(result.getString("source_type")),
                            PartType.getType(result.getString("target_type")),
                            result.getTimestamp("date"),
                            result.getInt("quantity"),
                            result.getTimestamp("deadline"),
                            result.getInt("penalty"),
                            TransformationOrder.TransformationState.valueOf(result.getString("state")));
                    orders.add(order);
                }
            }

        } catch(SQLException ex) {
            ex.printStackTrace();
        }

        return orders;
    }

    @Override
    public Part fetchPart(UUID id) {

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("SELECT * FROM part WHERE id = ?;")) {
                sql.setString(1, id.toString());
                ResultSet result = sql.executeQuery();
                if(result.next()) {
                    Part part = new Part(
                            UUID.fromString(result.getString("id")),
                            PartType.getType(result.getString("type"))
                    );
                    return part;
                }
            }

        } catch(SQLException ex) {
            ex.printStackTrace();
        }

        return null;
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

    @Override
    public void updatePart(Part part) {

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("UPDATE part SET type = ? where id = ?")) {
                sql.setString(1, part.getType().getName());
                sql.setString(2, part.getId().toString());
            }

        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void updateProcessLog(Process process, Conveyor assembler, Part part) {

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("INSERT INTO process_log (assembler_id, duration, part_source_type, part_target_type, part_id) " +
                    "VALUES (?, '? seconds', ?, ?, ?) ")) {
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
                sql.setTimestamp(2, transformationOrder.getDate());
                sql.setInt(3, transformationOrder.getQuantity());
                sql.setInt(4, transformationOrder.getDayPenalty());
                sql.setString(5, transformationOrder.getSourceType().getName());
                sql.setString(6, transformationOrder.getTargetType().getName());
                sql.setTimestamp(7, transformationOrder.getDeadline());
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
                sql.setTimestamp(3, unloadOrder.getDate());
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
                sql.setTimestamp(3, loadOrder.getDate());
                sql.setString(4, part.getType().getName());
            }

        } catch(SQLException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public Duration fetchProcessDuration(int assemblerId, PartType type) {
            Duration duration = Duration.ofSeconds(0);
        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("SELECT duration FROM process_log where assembler_id = ? AND part_target_type = ?;")) {
                ResultSet result = sql.executeQuery();
                while(result.next()) {
                    PGInterval interval = (PGInterval) result.getObject("duration");
                    interval.add(duration);
                }
            }

        } catch(SQLException ex) {
            ex.printStackTrace();
        }

        return duration;
    }
}

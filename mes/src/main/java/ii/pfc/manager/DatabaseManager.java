package ii.pfc.manager;

import java.sql.*;
import java.time.Duration;
import java.util.*;

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

public class DatabaseManager implements IDatabaseManager {

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
                if (!sql.execute()) {
                    logger.error("Could not connect to SQL database.");
                    return false;
                }
            }

        } catch (SQLException ex) {
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

    private Collection<LoadOrder> _extractLoadOrders(ResultSet result) throws SQLException {
        List<LoadOrder> orders = new ArrayList<>();

        while (result.next()) {
            orders.add(new LoadOrder(
                    result.getInt("order_id"),
                    PartType.getType(result.getString("order_type")),
                    result.getInt("conveyor_id"),
                    result.getTimestamp("date").toLocalDateTime(),
                    LoadOrder.LoadState.valueOf(result.getString("state"))
            ));
        }

        return orders;
    }

    @Override
    public Collection<LoadOrder> fetchLoadOrders(LoadOrder.LoadState state) {
        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("SELECT * FROM load_order WHERE state=?::load_order_state;")) {
                sql.setString(1, state.name());

                return _extractLoadOrders(sql.executeQuery());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return Collections.emptySet();
    }

    @Override
    public Collection<LoadOrder> fetchAllLoadOrders() {
        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("SELECT * FROM load_order;")) {
                return _extractLoadOrders(sql.executeQuery());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return Collections.emptySet();
    }


    /*

     */

    private Collection<UnloadOrder> _extractUnloadOrders(ResultSet result) throws SQLException {
        List<UnloadOrder> orders = new ArrayList<>();

        while (result.next()) {
            orders.add(new UnloadOrder(
                    result.getInt("order_id"),
                    PartType.getType(result.getString("type")),
                    result.getInt("conveyor_id"),
                    result.getTimestamp("date").toLocalDateTime(),
                    result.getInt("quantity"),
                    UnloadOrder.UnloadState.valueOf(result.getString("state"))
            ));
        }

        return orders;
    }

    @Override
    public Collection<UnloadOrder> fetchUnloadOrders(UnloadOrder.UnloadState state) {
        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("SELECT * FROM unload_order WHERE state=?::unload_order_state;")) {
                sql.setString(1, state.name());

                return _extractUnloadOrders(sql.executeQuery());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return Collections.emptySet();
    }

    @Override
    public Collection<UnloadOrder> fetchAllUnloadOrders() {
        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("SELECT * FROM unload_order;")) {
                return _extractUnloadOrders(sql.executeQuery());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return Collections.emptySet();
    }

    /*

     */

    private Collection<TransformationOrder> _extractTransformationOrders(ResultSet result) throws SQLException {
        List<TransformationOrder> orders = new ArrayList<>();

        while (result.next()) {
            orders.add(new TransformationOrder(
                    result.getInt("order_id"),
                    PartType.getType(result.getString("source_type")),
                    PartType.getType(result.getString("target_type")),
                    result.getTimestamp("date").toLocalDateTime(),
                    result.getInt("quantity"),
                    result.getTimestamp("deadline").toLocalDateTime(),
                    result.getInt("penalty"),
                    TransformationOrder.TransformationState.valueOf(result.getString("state")))
            );
        }

        return orders;
    }

    @Override
    public Collection<TransformationOrder> fetchTransformOrders(TransformationOrder.TransformationState state) {
        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("SELECT * FROM transform_order WHERE state=?::transform_order_state;")) {
                sql.setString(1, state.name());
                return _extractTransformationOrders(sql.executeQuery());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return Collections.emptySet();
    }

    @Override
    public Collection<TransformationOrder> fetchAllTransformOrders() {
        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("SELECT * FROM transform_order;")) {
                return _extractTransformationOrders(sql.executeQuery());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return Collections.emptySet();
    }

    /*

     */

    @Override
    public Part fetchPart(UUID id) {

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("SELECT * FROM part WHERE id = ?;")) {
                sql.setString(1, id.toString());

                ResultSet result = sql.executeQuery();
                if (result.next()) {
                    return new Part(
                            UUID.fromString(result.getString("id")),
                            PartType.getType(result.getString("type"))
                    );
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /*

     */

    @Override
    public Collection<Part> fetchUnloadedParts() {
        List<Part> parts = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("SELECT * FROM unloading_bay_log;")) {
                ResultSet result = sql.executeQuery();
                while (result.next()) {
                    parts.add(new Part(
                            UUID.fromString(result.getString("unloading_part")),
                            PartType.getType(result.getString("unloading_type"))
                    ));
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return parts;
    }

    /*

     */

    @Override
    public Duration fetchProcessDuration(int assemblerId, PartType type) {
        Duration duration = Duration.ofSeconds(0);

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("SELECT duration FROM process_log where assembler_id = ? AND part_target_type = ?;")) {
                ResultSet result = sql.executeQuery();
                while (result.next()) {
                    PGInterval interval = (PGInterval) result.getObject("duration");
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return duration;
    }

    /*

     */

    @Override
    public void updatePartType(UUID partId, PartType type) {

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("UPDATE part SET type=? where id=?;")) {
                sql.setString(1, type.getName());
                sql.setString(2, partId.toString());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void insertProcessLog(Process process, Conveyor assembler, Part part) {

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement(
                    "INSERT INTO process_log (assembler_id, duration, part_source_type, part_target_type, part_id) " +
                    "VALUES (?, '? seconds', ?, ?, ?) "
            )) {
                sql.setInt(1, assembler.getId());
                sql.setInt(2, (int) process.getDuration().toSeconds());
                sql.setString(3, process.getSource().getName());
                sql.setString(4, process.getResult().getName());
                sql.setString(5, part.getId().toString());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void insertUnloadingBayLog(UnloadOrder order, Part part) {

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement(
                    "INSERT INTO unloading_bay_log (conveyor_id, unloading_part, unloading_type) " +
                    "VALUES (?, ?, ?) "
            )) {
                sql.setInt(1, order.getConveyorId());
                sql.setString(2, part.getId().toString());
                sql.setString(3, order.getPartType().getName());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    /*

     */

    @Override /*VERIFICAR*/
    public void insertTransformOrder(TransformationOrder transformationOrder) {

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("INSERT INTO transform_order (order_id, date, quantity, penalty, source_type, target_type, deadline) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) ")) {
                sql.setInt(1, transformationOrder.getOrderId());
                sql.setTimestamp(2, Timestamp.valueOf(transformationOrder.getDate()));
                sql.setInt(3, transformationOrder.getQuantity());
                sql.setInt(4, transformationOrder.getDayPenalty());
                sql.setString(5, transformationOrder.getSourceType().getName());
                sql.setString(6, transformationOrder.getTargetType().getName());
                sql.setTimestamp(7, Timestamp.valueOf(transformationOrder.getDeadline()));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void updateTransformOrderState(int orderId, TransformationOrder.TransformationState newState) {

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("UPDATE transform_order SET state=? where order_id=?;")) {
                sql.setInt(1, orderId);
                sql.setString(2, newState.name());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /*

     */

    @Override /*VERIFICAR*/
    public void insertUnloadOrder(UnloadOrder unloadOrder) {

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("INSERT INTO unload_order (order_id, conveyor_id, date, quantity, type) " +
                    "VALUES (?, ?, ?, ?, ?) ")) {
                sql.setInt(1, unloadOrder.getOrderId());
                sql.setInt(2, unloadOrder.getConveyorId());
                sql.setTimestamp(3, Timestamp.valueOf(unloadOrder.getDate()));
                sql.setInt(4, unloadOrder.getQuantity());
                sql.setString(1, unloadOrder.getPartType().getName());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void updateUnloadOrderState(int orderId, UnloadOrder.UnloadState newState) {

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("UPDATE unload_order SET state=? where order_id=?;")) {
                sql.setInt(1, orderId);
                sql.setString(2, newState.name());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /*

     */

    @Override /*VERIFICAR*/
    public void insertLoadOrder(LoadOrder loadOrder) {

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("INSERT INTO load_order (order_id, conveyor_id, date, type) " +
                    "VALUES (?, ?, ?, ?) ")) {
                sql.setInt(1, loadOrder.getOrderId());
                sql.setInt(2, loadOrder.getConveyorId());
                sql.setTimestamp(3, Timestamp.valueOf(loadOrder.getDate()));
                sql.setString(4, loadOrder.getType().getName());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void updateLoadOrderState(int orderId, LoadOrder.LoadState newState) {

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("UPDATE load_order SET state=? where order_id=?;")) {
                sql.setInt(1, orderId);
                sql.setString(2, newState.name());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

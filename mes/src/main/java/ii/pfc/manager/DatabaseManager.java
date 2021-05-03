package ii.pfc.manager;

import ii.pfc.conveyor.Conveyor;
import ii.pfc.order.TransformationOrder;
import ii.pfc.order.UnloadOrder;
import ii.pfc.part.EnumTool;
import ii.pfc.part.Part;
import ii.pfc.part.PartType;
import ii.pfc.part.Process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.*;

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

    private Part _extractPart(ResultSet result) throws SQLException {
        return new Part(
                result.getObject("id", UUID.class),
                result.getInt("order_id"),
                PartType.getType(result.getString("type")),
                Part.PartState.valueOf(result.getString("state")));
    }


    /*

     */

    private Collection<UnloadOrder> _extractUnloadOrders(ResultSet result) throws SQLException {
        List<UnloadOrder> orders = new ArrayList<>();

        while (result.next()) {
            orders.add(new UnloadOrder(
                    result.getInt("order_id"),
                    PartType.getType(result.getString("type")),
                    result.getShort("conveyor_id"),
                    result.getTimestamp("date").toLocalDateTime(),
                    result.getInt("quantity"),
                    result.getInt("remaining"),
                    result.getInt("completed")
            ));
        }

        return orders;
    }

    @Override
    public Collection<UnloadOrder> fetchPendingUnloadOrders() {
        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("SELECT * FROM unload_order_status WHERE remaining > 0;")) {
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

            try (PreparedStatement sql = connection.prepareStatement("SELECT * FROM unload_order_status;")) {
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
                    result.getInt("remaining"),
                    result.getInt("holding"),
                    result.getInt("completed"),
                    result.getTimestamp("deadline").toLocalDateTime(),
                    result.getInt("penalty")
            ));
        }

        return orders;
    }

    @Override
    public Collection<TransformationOrder> fetchPendingTransformOrders() {
        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection
                    .prepareStatement("SELECT * FROM transform_order_status WHERE remaining > 0;")) {
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

            try (PreparedStatement sql = connection.prepareStatement("SELECT * FROM transform_order_status;")) {
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
                sql.setObject(1, id);

                ResultSet result = sql.executeQuery();
                if (result.next()) {
                    return _extractPart(result);
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
    public Collection<Part> fetchParts() {
        List<Part> parts = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement(
                    "SELECT * FROM part;"
            )) {
                ResultSet result = sql.executeQuery();
                while (result.next()) {
                    parts.add(_extractPart(result));
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return parts;
    }

    @Override
    public Collection<Part> fetchParts(int orderId, Part.PartState state, int limit) {
        List<Part> parts = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement(
                    "SELECT * FROM part WHERE order_id=? AND state=?::part_state LIMIT ?;"
            )) {
                sql.setInt(1, orderId);
                sql.setString(2, state.name());
                sql.setInt(3, limit);

                ResultSet result = sql.executeQuery();
                while (result.next()) {
                    parts.add(_extractPart(result));
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return parts;
    }

    @Override
    public Collection<Part> fetchParts(int orderId, PartType type, Part.PartState state, int limit) {
        List<Part> parts = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement(
                    "SELECT * FROM part WHERE order_id=? AND type=? AND state=?::part_state LIMIT ?;"
            )) {
                sql.setInt(1, orderId);
                sql.setString(2, type.getName());
                sql.setString(3, state.name());
                sql.setInt(4, limit);

                ResultSet result = sql.executeQuery();
                while (result.next()) {
                    parts.add(_extractPart(result));
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
    public Collection<Part> fetchUnloadedParts() {
        List<Part> parts = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement(
                    "SELECT * FROM unloading_bay_log ub INNER JOIN part p ON ub.unloading_part=p.id;"
            )) {
                ResultSet result = sql.executeQuery();
                while (result.next()) {
                    parts.add(_extractPart(result));
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return parts;
    }

    @Override
    public Map<PartType, Integer> countPartsTypes(Part.PartState state) {
        Map<PartType, Integer> partsCount = new HashMap<>();

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement(
                    "SELECT type, COUNT(*) as total FROM part WHERE state=?::part_state GROUP BY type;"
            )) {
                sql.setString(1, state.name());

                ResultSet result = sql.executeQuery();
                while (result.next()) {
                    PartType type = PartType.getType(result.getString("type"));
                    if(type.isUnknown()) {
                        continue;
                    }

                    partsCount.put(
                            type,
                            result.getInt("total")
                    );
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return partsCount;
    }

    @Override
    public Collection<Process> fetchProcesses() {
        List<Process> processes = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement(
                    "SELECT * FROM process;"
            )) {
                ResultSet result = sql.executeQuery();
                while (result.next()) {
                    PGInterval interval = (PGInterval) result.getObject("duration");

                    processes.add(new Process(
                            PartType.getType(result.getString("source_type")),
                            PartType.getType(result.getString("target_type")),
                            EnumTool.valueOf(result.getString("tool")),
                            Duration.ofSeconds((long) interval.getSeconds())

                    ));
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return processes;
    }

    /*

     */

    @Override
    public Duration fetchProcessDuration(int assemblerId, PartType type) {
        Duration duration = Duration.ofSeconds(0);

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection
                    .prepareStatement("SELECT duration FROM process_log where assembler_id = ? AND part_target_type = ?;")) {
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
    public boolean clearAllParts() {

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("DELETE FROM part;")) {
                sql.executeUpdate();
                return true;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;

    }

    /*

     */

    @Override
    public boolean insertPart(Part part) {
        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement(
                    "INSERT INTO part (id, order_id, type, state) " +
                            "VALUES (?, ?, ?, ?::part_state) ON CONFLICT DO NOTHING"
            )) {
                sql.setObject(1, part.getId());
                sql.setInt(2, part.getOrderId());
                sql.setString(3, part.getType().getName());
                sql.setString(4, part.getState().name());
                sql.executeUpdate();
                return true;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    /*

     */

    @Override
    public boolean updatePartType(UUID partId, PartType type) {

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("UPDATE part SET type=? where id=?;")) {
                sql.setString(1, type.getName());
                sql.setObject(2, partId);
                sql.executeUpdate();
                return true;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean updatePartState(UUID partId, Part.PartState state) {

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("UPDATE part SET state=?::part_state where id=?;")) {
                sql.setString(1, state.name());
                sql.setObject(2, partId);
                sql.executeUpdate();
                return true;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean updatePartStateAndOrder(UUID partId, Part.PartState state, int orderId) {

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement("UPDATE part SET state=?::part_state, order_id=? where id=?;")) {
                sql.setString(1, state.name());
                sql.setInt(2, orderId);
                sql.setObject(3, partId);
                sql.executeUpdate();
                return true;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean insertProcessLog(Process process, Conveyor assembler, Part part) {

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement(
                    "INSERT INTO process_log (assembler_id, duration, part_source_type, part_target_type, part_id) " +
                            "VALUES (?, '? seconds', ?, ?, ?) "
            )) {
                sql.setInt(1, assembler.getId());
                sql.setInt(2, (int) process.getDuration().toSeconds());
                sql.setString(3, process.getSource().getName());
                sql.setString(4, process.getResult().getName());
                sql.setObject(5, part.getId());
                sql.executeUpdate();
                return true;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;

    }

    @Override
    public boolean insertUnloadingBayLog(UnloadOrder order, Part part) {

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection.prepareStatement(
                    "INSERT INTO unloading_bay_log (conveyor_id, unloading_part, unloading_type) " +
                            "VALUES (?, ?, ?) "
            )) {
                sql.setInt(1, order.getConveyorId());
                sql.setObject(2, part.getId());
                sql.setString(3, order.getPartType().getName());
                sql.executeUpdate();
                return true;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;

    }

    /*

     */

    @Override /*VERIFICAR*/
    public boolean insertTransformOrder(TransformationOrder transformationOrder) {

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection
                    .prepareStatement("INSERT INTO transform_order (order_id, date, quantity, penalty, source_type, target_type, deadline) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?) ")) {
                sql.setInt(1, transformationOrder.getOrderId());
                sql.setTimestamp(2, Timestamp.valueOf(transformationOrder.getDate()));
                sql.setInt(3, transformationOrder.getQuantity());
                sql.setInt(4, transformationOrder.getDayPenalty());
                sql.setString(5, transformationOrder.getSourceType().getName());
                sql.setString(6, transformationOrder.getTargetType().getName());
                sql.setTimestamp(7, Timestamp.valueOf(transformationOrder.getDeadline()));
                sql.executeUpdate();
                return true;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;

    }

    /*

     */

    @Override /*VERIFICAR*/
    public boolean insertUnloadOrder(UnloadOrder unloadOrder) {

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement sql = connection
                    .prepareStatement("INSERT INTO unload_order (order_id, conveyor_id, date, quantity, remaining, completed, type) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?) ")) {
                sql.setInt(1, unloadOrder.getOrderId());
                sql.setInt(2, unloadOrder.getConveyorId());
                sql.setTimestamp(3, Timestamp.valueOf(unloadOrder.getDate()));
                sql.setInt(4, unloadOrder.getQuantity());
                sql.setInt(5, unloadOrder.getQuantity());
                sql.setInt(6, 0);
                sql.setString(7, unloadOrder.getPartType().getName());
                sql.executeUpdate();
                return true;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;

    }
}

package ii.pfc.manager;

import ii.pfc.conveyor.Conveyor;
import ii.pfc.order.TransformationOrder;
import ii.pfc.order.UnloadOrder;
import ii.pfc.part.Part;
import ii.pfc.part.PartType;
import ii.pfc.part.Process;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IDatabaseManager {

    Connection getConnection() throws SQLException;

    boolean openConnection();

    void closeConnection();

    /*

     */

    List<UnloadOrder> fetchPendingUnloadOrders();

    List<UnloadOrder> fetchAllUnloadOrders();

    //

    TransformationOrder fetchTransformOrder(int orderId);

    List<TransformationOrder> fetchPendingTransformOrders();

    List<TransformationOrder> fetchAllTransformOrders();

    //

    Part fetchPart(UUID id);

    Collection<Part> fetchParts();

    Collection<Part> fetchParts(int orderId, Part.PartState state, int limit);

    Collection<Part> fetchParts(int orderId, PartType type, Part.PartState state, int limit);

    Map<PartType, Integer> countUnloadedParts(short conveyorId);

    Map<PartType, Integer> countPartsTypes(Part.PartState state);

    //

    Collection<Process> fetchProcesses();

    Duration fetchProcessDuration(int assemblerId, PartType type);

    Map<PartType, Duration> fetchProcessDurations(int assemblerId);

    int countProcessedParts(int assemblerId);

    /*

     */

    boolean clearAllParts();

    boolean insertPart(Part part);

    boolean insertParts(Collection<Part> parts);

    boolean updatePartType(UUID partId, PartType type);

    boolean updatePartTypeAndOrder(UUID partId, PartType type, int orderId);

    boolean updatePartState(UUID partId, Part.PartState state);

    boolean updatePartStateAndOrder(UUID partId, Part.PartState state, int orderId);

    //

    boolean insertProcessLog(Process process, Conveyor assembler, Part part);

    boolean insertUnloadingBayLog(UnloadOrder order, Part part);

    //

    boolean clearAllUnloadOrders();

    boolean insertUnloadOrder(UnloadOrder unloadOrder);

    //

    boolean clearAllTransformOrders();

    boolean insertTransformOrder(TransformationOrder transformationOrder);

    boolean incrementTransformOrderCompletions(int orderId, int quantity);

    boolean updateTransformOrderStart(int orderId, LocalDateTime startDate);

    boolean updateTransformOrderFinish(int orderId, LocalDateTime finishDate);
}

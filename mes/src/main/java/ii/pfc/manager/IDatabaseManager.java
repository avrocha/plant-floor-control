package ii.pfc.manager;

import ii.pfc.conveyor.Conveyor;
import ii.pfc.order.LoadOrder;
import ii.pfc.order.TransformationOrder;
import ii.pfc.order.UnloadOrder;
import ii.pfc.part.Part;
import ii.pfc.part.PartType;
import ii.pfc.part.Process;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Collection;
import java.util.UUID;

public interface IDatabaseManager {

    Connection getConnection() throws SQLException;

    boolean openConnection();

    void closeConnection();

    /*

     */

    Collection<LoadOrder> fetchLoadOrders(LoadOrder.LoadState state);

    Collection<LoadOrder> fetchAllLoadOrders();

    //

    Collection<UnloadOrder> fetchUnloadOrders(UnloadOrder.UnloadState state);

    Collection<UnloadOrder> fetchAllUnloadOrders();

    //

    Collection<TransformationOrder> fetchTransformOrders(TransformationOrder.TransformationState state);

    Collection<TransformationOrder> fetchAllTransformOrders();

    //

    Part fetchPart(UUID id);

    Collection<Part> fetchStoredParts(PartType type, int limit);

    Collection<Part> fetchUnloadedParts();

    //

    Duration fetchProcessDuration(int assemblerId, PartType type);

    /*

     */

    boolean clearAllParts();

    boolean insertPart(Part part);

    boolean updatePartType(UUID partId, PartType type);

    //

    boolean insertProcessLog(Process process, Conveyor assembler, Part part);

    boolean insertUnloadingBayLog(UnloadOrder order, Part part);

    //

    boolean insertUnloadOrder(UnloadOrder unloadOrder);

    boolean updateUnloadOrderState(int orderId, UnloadOrder.UnloadState newState);

    //

    boolean insertLoadOrder(LoadOrder loadOrder);

    boolean updateLoadOrderState(int orderId, LoadOrder.LoadState newState);

    //

    boolean insertTransformOrder(TransformationOrder transformationOrder);

    boolean updateTransformOrderState(int orderId, TransformationOrder.TransformationState newState);
}

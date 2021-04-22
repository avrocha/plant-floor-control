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

    Collection<Part> fetchUnloadedParts();

    //

    Duration fetchProcessDuration(int assemblerId, PartType type);

    /*

     */

    void updatePartType(UUID partId, PartType type);

    //

    void insertProcessLog(Process process, Conveyor assembler, Part part);

    void insertUnloadingBayLog(UnloadOrder order, Part part);

    //

    void insertUnloadOrder(UnloadOrder unloadOrder);

    void updateUnloadOrderState(int orderId, UnloadOrder.UnloadState newState);

    //

    void insertLoadOrder(LoadOrder loadOrder);

    void updateLoadOrderState(int orderId, LoadOrder.LoadState newState);

    //

    void insertTransformOrder(TransformationOrder transformationOrder);

    void updateTransformOrderState(int orderId, TransformationOrder.TransformationState newState);
}

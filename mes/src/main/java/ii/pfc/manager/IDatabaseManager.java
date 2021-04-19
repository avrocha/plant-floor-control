package ii.pfc.manager;

import ii.pfc.conveyor.Conveyor;
import ii.pfc.order.LoadOrder;
import ii.pfc.order.TransformationOrder;
import ii.pfc.order.UnloadOrder;
import ii.pfc.part.Part;
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

    Collection<UnloadOrder> fetchUnloadOrders();
    Collection<TransformationOrder> fetchAllTransformOrders();
    Collection<TransformationOrder> fetchPendingTransformOrders();
    Part fetchPart(UUID id);
    Collection<Part> fetchUnloadedParts();
    Duration fetchProcessDuration(int assemblerId, PartType type);
    void updateProcessLog(Process process, Conveyor assembler, Part part);
    void updatePart(Part);
    void updateUnloadingBayLog(UnloadOrder unloadingOrder, Part part);
    void updateTransformOrder(TransformationOrder transformationOrder);
    void updateUnloadOrder(UnloadOrder unloadOrder);
    void updateLoadOrder(LoadOrder loadOrder, Part part);
}

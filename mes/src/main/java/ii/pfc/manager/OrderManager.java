package ii.pfc.manager;

import ii.pfc.order.LoadOrder;
import ii.pfc.order.TransformationOrder;
import ii.pfc.order.UnloadOrder;
import ii.pfc.part.Part;
import java.util.Collection;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderManager implements IOrderManager {

    private static final Logger logger = LoggerFactory.getLogger(OrderManager.class);

    //

    private final IDatabaseManager databaseManager;

    private final IRoutingManager routingManager;

    public OrderManager(IDatabaseManager databaseManager, IRoutingManager routingManager) {
        this.databaseManager = databaseManager;
        this.routingManager = routingManager;
    }

    @Override
    public void pollLoadOrders() {
        Collection<LoadOrder> orders = databaseManager.fetchLoadOrders(LoadOrder.LoadState.PENDING);

        for(LoadOrder order : orders) {
            if (databaseManager.updateLoadOrderState(order.getOrderId(), LoadOrder.LoadState.IN_PROGRESS)) {
                Part part = new Part(UUID.randomUUID(), 0, order.getType());

                if (!databaseManager.insertPart(part)) {
                    logger.error("Could not insert part in the database!");
                }
            }
        }
    }

    @Override
    public void pollUnloadOrders() {
        Collection<UnloadOrder> orders = databaseManager.fetchUnloadOrders(UnloadOrder.UnloadState.PENDING);
    }

    @Override
    public void pollTransformOrders() {
        Collection<TransformationOrder> orders = databaseManager.fetchTransformOrders(TransformationOrder.TransformationState.PENDING);
    }

    /*

     */

    @Override
    public void enqueueLoadOrder(LoadOrder loadOrder) {
        databaseManager.insertLoadOrder(loadOrder);
    }

    @Override
    public void enqueueUnloadOrder(UnloadOrder unloadOrder) {
        databaseManager.insertUnloadOrder(unloadOrder);
    }

    @Override
    public void enqueueTransformationOrder(TransformationOrder transformationOrder) {
        databaseManager.insertTransformOrder(transformationOrder);
    }


}

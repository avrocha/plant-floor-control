package ii.pfc.manager;

import ii.pfc.order.LoadOrder;
import ii.pfc.order.TransformationOrder;
import ii.pfc.order.UnloadOrder;
import java.util.Collection;
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
        logger.info("Load: {}", orders.toString());
    }

    @Override
    public void pollUnloadOrders() {
        Collection<UnloadOrder> orders = databaseManager.fetchUnloadOrders(UnloadOrder.UnloadState.PENDING);
        logger.info("Unload: {}", orders.toString());
    }

    @Override
    public void pollTransformOrders() {
        Collection<TransformationOrder> orders = databaseManager.fetchTransformOrders(TransformationOrder.TransformationState.PENDING);
        logger.info("Transformation: {}", orders.toString());
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

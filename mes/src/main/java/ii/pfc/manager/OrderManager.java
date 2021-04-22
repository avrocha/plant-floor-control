package ii.pfc.manager;

import ii.pfc.order.LoadOrder;
import ii.pfc.order.TransformationOrder;
import ii.pfc.order.UnloadOrder;

import java.util.Collection;

public class OrderManager implements IOrderManager{

    private final IDatabaseManager databaseManager;

    public OrderManager(IDatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public Collection<LoadOrder> pollLoadOrders() {
        return databaseManager.fetchLoadOrders(LoadOrder.LoadState.PENDING);
    }

    @Override
    public Collection<UnloadOrder> pollUnloadOrders() {
        return databaseManager.fetchUnloadOrders(UnloadOrder.UnloadState.PENDING);
    }

    @Override
    public Collection<TransformationOrder> pollTransformOrders() {
        return databaseManager.fetchTransformOrders(TransformationOrder.TransformationState.PENDING);
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

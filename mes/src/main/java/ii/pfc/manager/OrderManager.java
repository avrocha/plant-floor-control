package ii.pfc.manager;

import ii.pfc.conveyor.Conveyor;
import ii.pfc.conveyor.EnumConveyorType;
import ii.pfc.order.LoadOrder;
import ii.pfc.order.TransformationOrder;
import ii.pfc.order.UnloadOrder;
import ii.pfc.part.Part;
import ii.pfc.route.Route;
import java.util.Collection;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderManager implements IOrderManager {

    private static final Logger logger = LoggerFactory.getLogger(OrderManager.class);

    //

    private final ICommsManager commsManager;

    private final IDatabaseManager databaseManager;

    private final IRoutingManager routingManager;

    public OrderManager(ICommsManager commsManager, IDatabaseManager databaseManager, IRoutingManager routingManager) {
        this.commsManager = commsManager;
        this.databaseManager = databaseManager;
        this.routingManager = routingManager;
    }

    @Override
    public void pollUnloadOrders() {
        Collection<UnloadOrder> orders = databaseManager.fetchUnloadOrders(UnloadOrder.UnloadState.PENDING);

        for(UnloadOrder order : orders) {
            Collection<Part> parts = databaseManager.fetchStoredParts(order.getPartType(), order.getQuantity());
            for(Part part : parts) {
                outer : for(Conveyor source : routingManager.getConveyors(EnumConveyorType.WAREHOUSE_OUT)) {
                    for(Conveyor target : routingManager.getConveyors(EnumConveyorType.SLIDER)) {
                        Route route = routingManager.traceRoute(part, source, target);

                        if (route == null) {
                            continue;
                        }

                        if (!commsManager.getWarehouseOutConveyorStatus(source.getId())) {
                            continue;
                        }

                        if (databaseManager.updateUnloadOrderState(order.getOrderId(), UnloadOrder.UnloadState.IN_PROGRESS)) {
                            commsManager.dispatchWarehouseOutConveyorExit(source.getId(), part.getType());
                            commsManager.sendPlcRoute(route);
                        }

                        continue outer;
                    }
                }
            }
        }
    }

    @Override
    public void pollTransformOrders() {
        Collection<TransformationOrder> orders = databaseManager.fetchTransformOrders(TransformationOrder.TransformationState.PENDING);
    }

    /*

     */

    @Override
    public void enqueueUnloadOrder(UnloadOrder unloadOrder) {
        databaseManager.insertUnloadOrder(unloadOrder);
    }

    @Override
    public void enqueueTransformationOrder(TransformationOrder transformationOrder) {
        databaseManager.insertTransformOrder(transformationOrder);
    }


}

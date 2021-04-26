package ii.pfc.manager;

import ii.pfc.conveyor.Conveyor;
import ii.pfc.conveyor.EnumConveyorType;
import ii.pfc.order.TransformationOrder;
import ii.pfc.order.UnloadOrder;
import ii.pfc.part.Part;
import ii.pfc.part.PartType;
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
    public void pollLoadOrders() {
        Collection<Conveyor> loadConveyors = routingManager.getConveyors(EnumConveyorType.LOAD);
        for(Conveyor conveyor : loadConveyors) {
            short conveyorId = conveyor.getId();
            boolean hasPart = commsManager.getLoadConveyorStatus(conveyorId);

            if (hasPart) {
                PartType type;

                switch(conveyorId) {
                    case 1: {
                        type = PartType.PART_2;
                        break;
                    }

                    default: {
                        type = PartType.PART_1;
                        break;
                    }
                }

                Part tempPart = new Part(UUID.randomUUID(), 0, type, Part.PartState.SPAWNED);
                Conveyor source = routingManager.getConveyor(conveyorId);

                for(Conveyor target : routingManager.getConveyors(EnumConveyorType.WAREHOUSE_IN)) {
                    Route route = routingManager.traceRoute(tempPart, source, target);

                    if (route == null) {
                        continue;
                    }

                    commsManager.sendPlcRoute(route);
                    break;
                }
            }
        }

        Collection<Conveyor> winConveyors = routingManager.getConveyors(EnumConveyorType.WAREHOUSE_IN);
        for(Conveyor conveyor : winConveyors) {
            Part part = commsManager.getWarehouseInConveyorPart(conveyor.getId());

            if (part != null) {
                if (databaseManager.insertPart(part)) {
                    commsManager.dispatchWarehouseInConveyorEntry(conveyor.getId());
                }
            }
        }
    }

    @Override
    public void pollUnloadOrders() {
        Collection<UnloadOrder> orders = databaseManager.fetchPendingUnloadOrders();

        for(UnloadOrder order : orders) {
            Collection<Part> parts = databaseManager.fetchParts(order.getPartType(), Part.PartState.STORED, order.getRemaining());

            for(Part part : parts) {
                for(Conveyor source : routingManager.getConveyors(EnumConveyorType.WAREHOUSE_OUT)) {
                    Conveyor target = routingManager.getConveyor(order.getConveyorId());
                    Route route = routingManager.traceRoute(part, source, target);

                    if (route == null) {
                        continue;
                    }

                    if (!commsManager.getWarehouseOutConveyorStatus(source.getId())) {
                        continue;
                    }

                    if (databaseManager.decreaseUnloadOrderRemaining(order.getOrderId(), 1)) {
                        databaseManager.updatePartState(part.getId(), Part.PartState.SPAWNED);

                        commsManager.dispatchWarehouseOutConveyorExit(source.getId(), part.getType());
                        commsManager.sendPlcRoute(route);
                        break;
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

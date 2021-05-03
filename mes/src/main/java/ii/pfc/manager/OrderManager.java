package ii.pfc.manager;

import ii.pfc.conveyor.Conveyor;
import ii.pfc.conveyor.EnumConveyorType;
import ii.pfc.order.TransformationOrder;
import ii.pfc.order.UnloadOrder;
import ii.pfc.part.Part;
import ii.pfc.part.PartType;
import ii.pfc.part.Process;
import ii.pfc.part.ProcessRegistry;
import ii.pfc.route.Route;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderManager implements IOrderManager {

    private static final Logger logger = LoggerFactory.getLogger(OrderManager.class);

    //

    private final ICommsManager commsManager;

    private final IDatabaseManager databaseManager;

    private final IRoutingManager routingManager;

    //

    //

    public OrderManager(ICommsManager commsManager, IDatabaseManager databaseManager, IRoutingManager routingManager) {
        this.commsManager = commsManager;
        this.databaseManager = databaseManager;
        this.routingManager = routingManager;
    }

    /*

     */

    @Override
    public void checkWarehouseEntries() {
        Collection<Conveyor> winConveyors = routingManager.getConveyors(EnumConveyorType.WAREHOUSE_IN);
        for(Conveyor conveyor : winConveyors) {
            Part tempPart = commsManager.getWarehouseInConveyorPart(conveyor.getId());

            if (tempPart != null) {
                Part part = databaseManager.fetchPart(tempPart.getId());
                if (part != null) {
                    if (!databaseManager.updatePartState(part.getId(), Part.PartState.STORED)) {
                        continue;
                    }


                } else {
                    if (!databaseManager.insertPart(tempPart)) {
                        continue;
                    }


                }

                commsManager.dispatchWarehouseInConveyorEntry(conveyor.getId());
            }
        }
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

                Part tempPart = new Part(UUID.randomUUID(), 0, type, Part.PartState.PROCESSING);
                Conveyor source = routingManager.getConveyor(conveyorId);

                Route minimumRoute = null;

                for(Conveyor target : routingManager.getConveyors(EnumConveyorType.WAREHOUSE_IN)) {
                    Route route = routingManager.traceRoute(tempPart, source, target);

                    if (route == null) {
                        continue;
                    }

                    if (minimumRoute == null || route.getWeight() < minimumRoute.getWeight()) {
                        minimumRoute = route;
                    }
                }

                if (minimumRoute != null) {
                    commsManager.sendPlcRoute(minimumRoute);
                }
            }
        }
    }

    @Override
    public void pollUnloadOrders() {
        Collection<UnloadOrder> orders = databaseManager.fetchPendingUnloadOrders();

        for(UnloadOrder order : orders) {
            //logger.info("#{} - {} part(s) remaining", order.getOrderId(), order.getRemaining());
            Collection<Part> parts = databaseManager.fetchParts(0, order.getPartType(), Part.PartState.STORED, 1);

            for(Part part : parts) {
                Conveyor minimumSource = null;
                Route minimumRoute = null;

                for(Conveyor source : routingManager.getConveyors(EnumConveyorType.WAREHOUSE_OUT)) {
                    Conveyor target = routingManager.getConveyor(order.getConveyorId());
                    Route route = routingManager.traceRoute(part, source, target);

                    if (route == null) {
                        continue;
                    }

                    if (!commsManager.getWarehouseOutConveyorStatus(source.getId())) {
                        continue;
                    }

                    if (minimumRoute == null || route.getWeight() < minimumRoute.getWeight()) {
                        minimumSource = source;
                        minimumRoute = route;
                    }
                }

                if (minimumRoute != null) {
                    if (databaseManager.updatePartStateAndOrder(part.getId(), Part.PartState.UNLOADING, order.getOrderId())) {
                        commsManager.dispatchWarehouseOutConveyorExit(minimumSource.getId(), part.getType());
                        commsManager.sendPlcRoute(minimumRoute);
                    }
                }
            }
        }
    }

    @Override
    public void pollTransformOrders() {
        Collection<TransformationOrder> orders = databaseManager.fetchPendingTransformOrders();

        for(TransformationOrder order : orders) {
            logger.info("#{} - {} part(s) remaining", order.getOrderId(), order.getRemaining());
            Collection<Part> parts = databaseManager.fetchParts(order.getOrderId(), Part.PartState.STORED, 1);

            for(Part part : parts) {
                List<Process> processes = ProcessRegistry.getProcesses(part.getType(), order.getTargetType());

                if(processes.isEmpty()) {
                    continue;
                }

                Conveyor minimumSource = null;
                Route minimumRoute = null;

                for(Conveyor source : routingManager.getConveyors(EnumConveyorType.WAREHOUSE_OUT)) {
                    if (!commsManager.getWarehouseOutConveyorStatus(source.getId())) {
                        continue;
                    }

                    for(Conveyor target : routingManager.getConveyors(EnumConveyorType.ASSEMBLY)) {
                        Route route = routingManager.traceRoute(part, source, target);

                        if (route == null) {
                            continue;
                        }

                        if (minimumRoute == null || route.getWeight() < minimumRoute.getWeight()) {
                            minimumSource = source;
                            minimumRoute = route;
                        }
                    }
                }

                if (minimumRoute != null) {
                    if (databaseManager.updatePartState(part.getId(), Part.PartState.PROCESSING)) {
                        commsManager.dispatchWarehouseOutConveyorExit(minimumSource.getId(), part.getType());
                        commsManager.sendPlcRoute(minimumRoute, processes.get(0));
                    }
                }
            }

            parts = databaseManager.fetchParts(0, order.getSourceType(), Part.PartState.STORED, 1);
            //logger.info("Received {} parts", parts.size());

            for(Part part : parts) {
                List<Process> processes = ProcessRegistry.getProcesses(part.getType(), order.getTargetType());

                if(processes.isEmpty()) {
                    System.out.println("NO PROCESS!");
                    continue;
                }

                Conveyor minimumSource = null;
                Route minimumRoute = null;

                for(Conveyor source : routingManager.getConveyors(EnumConveyorType.WAREHOUSE_OUT)) {
                    if (!commsManager.getWarehouseOutConveyorStatus(source.getId())) {
                        continue;
                    }

                    for(Conveyor target : routingManager.getConveyors(EnumConveyorType.ASSEMBLY)) {
                        Route route = routingManager.traceRoute(part, source, target);

                        if (route == null) {
                            continue;
                        }

                        if (minimumRoute == null || route.getWeight() < minimumRoute.getWeight()) {
                            minimumSource = source;
                            minimumRoute = route;
                        }
                    }
                }

                if (minimumRoute != null) {
                    if (databaseManager.updatePartStateAndOrder(part.getId(), Part.PartState.PROCESSING, order.getOrderId())) {
                        commsManager.dispatchWarehouseOutConveyorExit(minimumSource.getId(), part.getType());
                        commsManager.sendPlcRoute(minimumRoute, processes.get(0));
                    }
                }
            }
        }
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

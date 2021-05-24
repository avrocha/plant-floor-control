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
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderManager implements IOrderManager {

    private static final Logger logger = LoggerFactory.getLogger(OrderManager.class);

    //

    private final ProcessRegistry processRegistry;

    //

    private final ICommsManager commsManager;

    private final IDatabaseManager databaseManager;

    private final IRoutingManager routingManager;

    //

    //

    public OrderManager(ProcessRegistry processRegistry, ICommsManager commsManager, IDatabaseManager databaseManager,
        IRoutingManager routingManager) {
        this.processRegistry = processRegistry;
        this.commsManager = commsManager;
        this.databaseManager = databaseManager;
        this.routingManager = routingManager;
    }

    /*

     */

    @Override
    public void checkWarehouseEntries() {
        Collection<Conveyor> winConveyors = routingManager.getConveyors(EnumConveyorType.WAREHOUSE_IN);
        for (Conveyor conveyor : winConveyors) {
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
    public void checkAssemblyCompletions() {
        Collection<Conveyor> assConveyors = routingManager.getConveyors(EnumConveyorType.ASSEMBLY);
        for (Conveyor conveyor : assConveyors) {
            Pair<UUID, PartType> result = commsManager.getAssemblyConveyorCompletedStatus(conveyor.getId());
            if (result == null) {
                continue;
            }

            UUID partId = result.getKey();
            PartType targetType = result.getValue();
            if (targetType.isUnknown()) {
                continue;
            }

            Part part = databaseManager.fetchPart(partId);
            if (part == null) {
                continue;
            }

            // If we have finished the order or we are not bound to any order, return to the warehouse and dont do anything.
            if (part.getOrderId() != 0 && part.getType() != targetType) {
                Process process = processRegistry.getProcess(part.getType(), targetType);
                if (process != null) {
                    databaseManager.insertProcessLog(process, conveyor, part);
                }

                TransformationOrder order = databaseManager.fetchTransformOrder(part.getOrderId());

                if (order.getTargetType() == targetType) {
                    databaseManager.updatePartTypeAndOrder(part.getId(), targetType, 0);
                    databaseManager.incrementTransformOrderCompletions(order.getOrderId(), 1);

                    if (order.getCompleted() + 1 == order.getQuantity()) {
                        databaseManager.updateTransformOrderFinish(order.getOrderId(), LocalDateTime.now());
                    }
                } else {
                    databaseManager.updatePartType(part.getId(), targetType);
                }

                part = databaseManager.fetchPart(partId);

                if (part.getOrderId() != 0) {
                    List<Process> processes = processRegistry.getProcesses(part.getType(), order.getTargetType());

                    if (processes.isEmpty()) {
                        continue;
                    }

                    Route minimumRoute = null;

                    for (Conveyor target : routingManager.getConveyors(EnumConveyorType.ASSEMBLY)) {
                        if (target.equals(conveyor)) {
                            continue;
                        }

                        Route route = routingManager.traceRoute(part, processes.get(0), conveyor, target);

                        if (route == null) {
                            continue;
                        }

                        if (minimumRoute == null || route.getWeight() < minimumRoute.getWeight()) {
                            minimumRoute = route;
                        }
                    }

                    if (minimumRoute != null) {
                        logger.info("new machine");
                        commsManager.sendPlcRoute(minimumRoute, processes.get(0));
                        continue;
                    }
                }
            }
            logger.info("Send to ware");

            Route minimumRoute = null;

            for (Conveyor target : routingManager.getConveyors(EnumConveyorType.WAREHOUSE_IN)) {
                Route route = routingManager.traceRoute(part, null, conveyor, target);

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

    //

    @Override
    public void pollLoadOrders() {
        Collection<Conveyor> loadConveyors = routingManager.getConveyors(EnumConveyorType.LOAD);
        for (Conveyor conveyor : loadConveyors) {
            short conveyorId = conveyor.getId();
            boolean hasPart = commsManager.getLoadConveyorStatus(conveyorId);

            if (hasPart) {
                PartType type;

                switch (conveyorId) {
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

                for (Conveyor target : routingManager.getConveyors(EnumConveyorType.WAREHOUSE_IN)) {
                    Route route = routingManager.traceRoute(tempPart, null, source, target);

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
        List<UnloadOrder> orders = databaseManager.fetchPendingUnloadOrders();
        Collections.sort(orders, Collections.reverseOrder());

        for (UnloadOrder order : orders) {
            //logger.info("#{} - {} part(s) remaining", order.getOrderId(), order.getRemaining());
            Collection<Part> parts = databaseManager.fetchParts(0, order.getPartType(), Part.PartState.STORED, 1);

            for (Part part : parts) {
                Conveyor minimumSource = null;
                Route minimumRoute = null;

                for (Conveyor source : routingManager.getConveyors(EnumConveyorType.WAREHOUSE_OUT)) {
                    Conveyor target = routingManager.getConveyor(order.getConveyorId());
                    Route route = routingManager.traceRoute(part, null, source, target);

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
                        databaseManager.insertUnloadingBayLog(order, part);

                        commsManager.dispatchWarehouseOutConveyorExit(minimumSource.getId(), part.getType());
                        commsManager.sendPlcRoute(minimumRoute);
                    }
                }
            }
        }
    }

    @Override
    public void pollTransformOrders() {
        List<TransformationOrder> orders = databaseManager.fetchPendingTransformOrders();
        Collections.sort(orders, Collections.reverseOrder());

        for (TransformationOrder order : orders) {
            logger.info("#{} - {} part(s) remaining", order.getOrderId(), order.getRemaining());
            Collection<Part> parts = databaseManager.fetchParts(order.getOrderId(), Part.PartState.STORED, 1);
            logger.info("#{} - Received {} available part(s)", order.getOrderId(), parts.size());

            for (Part part : parts) {
                List<Process> processes = processRegistry.getProcesses(part.getType(), order.getTargetType());

                if (processes.isEmpty()) {
                    continue;
                }

                Conveyor minimumSource = null;
                Route minimumRoute = null;

                for (Conveyor source : routingManager.getConveyors(EnumConveyorType.WAREHOUSE_OUT)) {
                    if (!commsManager.getWarehouseOutConveyorStatus(source.getId())) {
                        continue;
                    }

                    for (Conveyor target : routingManager.getConveyors(EnumConveyorType.ASSEMBLY)) {
                        Route route = routingManager.traceRoute(part, processes.get(0), source, target);

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

            if (order.getRemaining() <= 0) {
                continue;
            }

            parts = databaseManager.fetchParts(0, order.getSourceType(), Part.PartState.STORED, 1);
            logger.info("Received {} parts", parts.size());

            boolean updatedStart = false;

            for (Part part : parts) {
                List<Process> processes = processRegistry.getProcesses(part.getType(), order.getTargetType());

                if (processes.isEmpty()) {
                    continue;
                }

                Conveyor minimumSource = null;
                Route minimumRoute = null;

                for (Conveyor source : routingManager.getConveyors(EnumConveyorType.WAREHOUSE_OUT)) {
                    if (!commsManager.getWarehouseOutConveyorStatus(source.getId())) {
                        continue;
                    }

                    for (Conveyor target : routingManager.getConveyors(EnumConveyorType.ASSEMBLY)) {
                        Route route = routingManager.traceRoute(part, processes.get(0), source, target);

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
                        if (!updatedStart && order.getRemaining() == order.getQuantity()) {
                            databaseManager.updateTransformOrderStart(order.getOrderId(), LocalDateTime.now());
                            updatedStart = true;
                        }

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

package ii.pfc.command.impl;

import ii.pfc.command.CommandRequest;
import ii.pfc.manager.ICommandManager;
import ii.pfc.manager.IDatabaseManager;
import ii.pfc.manager.IOrderManager;
import ii.pfc.order.TransformationOrder;
import ii.pfc.part.Process;
import ii.pfc.part.ProcessRegistry;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;

public class CommandRequestOrderList implements CommandRequest {

    /*

     */

    @Override
    public void onReceive(ICommandManager commandManager, IOrderManager orderManager, IDatabaseManager databaseManager,
                          InetSocketAddress source) {
        CommandResponseOrderList response = new CommandResponseOrderList();

        Collection<TransformationOrder> orders = databaseManager.fetchAllTransformOrders();
        for (TransformationOrder order : orders) {
            int date = (int) order.getDate().atZone(ZoneId.systemDefault()).toEpochSecond();

            LocalDateTime predictedStart = order.getStartDate();
            if (predictedStart == null) {
                predictedStart = LocalDateTime.now().plus(order.getDeadline().dividedBy(2));
            }

            LocalDateTime predictedFinish = order.getFinishDate();
            if (predictedFinish == null) {
                List<Process> processes = ProcessRegistry.INSTANCE.getProcesses(order.getSourceType(), order.getTargetType());

                Duration predictedDuration = Duration.ZERO;
                for(Process process : processes) {
                    predictedDuration = predictedDuration.plus(process.getDuration());
                }

                predictedFinish = predictedStart.plus(predictedDuration);
            }

            response.addOrder(
                    order.getOrderId(),
                    order.getSourceType(),
                    order.getTargetType(),
                    order.getQuantity(),
                    order.getCompleted(),
                    order.getQuantity() - order.getCompleted() - order.getHolding() - order.getRemaining(),
                    order.getHolding(),
                    date,
                    (int) order.getReceivedDate().atZone(ZoneId.systemDefault()).toEpochSecond(),
                    (int) order.getDeadline().toSeconds(),
                    order.getDayPenalty(),
                    (int) Duration.between(order.getReceivedDate(), predictedStart).toSeconds(),
                    (int) Duration.between(order.getReceivedDate(), predictedFinish).toSeconds(),
                    order.computePenalty(predictedFinish)
            );
        }

        commandManager.sendResponse(source, response);
    }
}

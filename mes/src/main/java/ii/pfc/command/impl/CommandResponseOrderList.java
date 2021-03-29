package ii.pfc.command.impl;

import ii.pfc.command.CommandResponse;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Order_Schedule")
public class CommandResponseOrderList implements CommandResponse {

    @XmlElement(name = "Order")
    private final Set<OrderData> responseList;

    public CommandResponseOrderList() {
        this.responseList = new HashSet<>();
    }

    /*

     */

    public void addOrder(int orderId, String sourceType, String targetType, int quantityTotal, int quantityProcessed,
        int quantityProcessing, int quantityHolding, int time, int timeReceived, int deadline, int penalty, int startTime, int endTime,
        int penaltyIncurred) {

        this.responseList.add(new OrderData(orderId,
            new OrderTransformData(sourceType, targetType, quantityTotal, quantityProcessed, quantityProcessing, quantityHolding, time,
                timeReceived, deadline, penalty, startTime, endTime, penaltyIncurred)));
    }

    /*

     */

    private static class OrderData {

        @XmlAttribute(name = "Number")
        private final int orderId;

        @XmlElement(name = "Transform")
        private final OrderTransformData transformData;

        private OrderData(int orderId, OrderTransformData transformData) {
            this.orderId = orderId;
            this.transformData = transformData;
        }

        /*

         */

        @Override
        public int hashCode() {
            return Objects.hash(orderId);
        }
    }

    private static class OrderTransformData {

        @XmlAttribute(name = "From")
        private final String sourceType;

        @XmlAttribute(name = "To")
        private final String targetType;

        @XmlAttribute(name = "Quantity")
        private final int quantityTotal;

        @XmlAttribute(name = "Quantity1")
        private final int quantityProcessed;

        @XmlAttribute(name = "Quantity2")
        private final int quantityProcessing;

        @XmlAttribute(name = "Quantity3")
        private final int quantityHolding;

        @XmlAttribute(name = "Time")
        private final int time;

        @XmlAttribute(name = "Time1")
        private final int timeReceived;

        @XmlAttribute(name = "MaxDelay")
        private final int deadline;

        @XmlAttribute(name = "Penalty")
        private final int penalty;

        @XmlAttribute(name = "Start")
        private final int startTime;

        @XmlAttribute(name = "End")
        private final int endTime;

        @XmlAttribute(name = "PenaltyIncurred")
        private final int penaltyIncurred;

        private OrderTransformData(String sourceType, String targetType, int quantityTotal, int quantityProcessed, int quantityProcessing,
            int quantityHolding, int time, int timeReceived, int deadline, int penalty, int startTime, int endTime, int penaltyIncurred) {

            this.sourceType = sourceType;
            this.targetType = targetType;
            this.quantityTotal = quantityTotal;
            this.quantityProcessed = quantityProcessed;
            this.quantityProcessing = quantityProcessing;
            this.quantityHolding = quantityHolding;
            this.time = time;
            this.timeReceived = timeReceived;
            this.deadline = deadline;
            this.penalty = penalty;
            this.startTime = startTime;
            this.endTime = endTime;
            this.penaltyIncurred = penaltyIncurred;
        }
    }

}

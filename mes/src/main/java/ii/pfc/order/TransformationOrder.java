package ii.pfc.order;

import java.util.Calendar;
import java.util.Date;

public class TransformationOrder {

    private final int orderId;

    private final Date date;

    private final int quantity;

    private final Date deadline;

    private final int penalty;

    public TransformationOrder(int orderId, Date date, int quantity, Date deadline, int penalty) {
        this.orderId = orderId;
        this.date = date;
        this.quantity = quantity;
        this.deadline = deadline;
        this.penalty = penalty;
    }

    /*

     */

    public int getOrderId() {
        return orderId;
    }

    public Date getDate() {
        return date;
    }

    public int getQuantity() {
        return quantity;
    }

    public Date getDeadline() {
        return deadline;
    }

    public int getDayPenalty() {
        return penalty;
    }

    public int computePenalty(Date currentDate) {
        if(currentDate.before(deadline)) {
            return 0;
        }

        // TODO calculate days
        int days = 1;

        return penalty * days;
    }
}

package ii.pfc.order;


import ii.pfc.part.PartType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement(name = "Unload")
public class UnloadOrder {

    private final int orderId;

    @XmlElement(name = "Type")
    private final PartType partType;

    @XmlElement(name = "Destination")
    private final int conveyorId;

    private final Date date;

    @XmlElement(name = "Quantity")
    private final int quantity;


    public UnloadOrder(int orderId, PartType partType, int conveyorId, Date date, int quantity) {
        this.orderId = orderId;
        this.partType = partType;
        this.conveyorId = conveyorId;
        this.date = date;
        this.quantity = quantity;
    }

    /*

     */

    public int getOrderId() {
        return orderId;
    }

    public PartType getPartType() {
        return partType;
    }

    public int getConveyorId() {
        return conveyorId;
    }

    public Date getDate() {
        return date;
    }

    public int getQuantity() {
        return quantity;
    }
}

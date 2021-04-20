package ii.pfc.command.impl;

import java.util.Arrays;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ORDERS")
public class BatchRequestOrderWrapper {

    @XmlElement(name = "Order")
    private RequestOrderWrapper[] orders;

    @XmlElement(name = "Request_Orders")
    private CommandRequestOrderList[] orderList;

    @XmlElement(name = "Request_Stores")
    private CommandRequestPartList[] partList;

    /*

     */

    public RequestOrderWrapper[] getOrders() {
        return orders;
    }

    public CommandRequestOrderList[] getOrderList() {
        return orderList;
    }

    public CommandRequestPartList[] getPartList() {
        return partList;
    }

    /*

     */

    @Override
    public String toString() {
        return "BatchRequestOrderWrapper{" +
            "orders=" + Arrays.toString(orders) +
            ", orderList=" + Arrays.toString(orderList) +
            ", partList=" + Arrays.toString(partList) +
            '}';
    }
}

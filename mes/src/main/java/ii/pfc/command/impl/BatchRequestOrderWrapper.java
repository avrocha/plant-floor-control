package ii.pfc.command.impl;

import java.util.Arrays;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ORDERS")
public class BatchRequestOrderWrapper {

    @XmlElement(name = "Order")
    private RequestOrderWrapper[] orders;

    /*

     */

    public RequestOrderWrapper[] getOrders() {
        return orders;
    }

    /*

     */

    @Override
    public String toString() {
        return "BatchRequestOrderWrapper{" +
            "orders=" + Arrays.toString(orders) +
            '}';
    }
}

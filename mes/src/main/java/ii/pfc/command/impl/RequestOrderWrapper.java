package ii.pfc.command.impl;

import ii.pfc.command.impl.CommandRequestUnload;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Order")
public class RequestOrderWrapper {

    @XmlAttribute(name = "Number")
    private int orderId;

    @XmlElement(name = "Unload")
    private CommandRequestUnload unload;

    @XmlElement(name = "Transform")
    private CommandRequestTransform transform;

    /*

     */

    public void onReceive() {
        if (unload != null) {
            unload.orderId = orderId;
            unload.onReceive();
        }

        if (transform != null) {
            transform.orderId = orderId;
            transform.onReceive();
        }
    }

    /*

     */

    @Override
    public String toString() {
        return "CommandRequestWrapper{" +
            "orderId=" + orderId +
            ", unload=" + unload +
            ", transform=" + transform +
            '}';
    }
}

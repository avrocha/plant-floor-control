package ii.pfc.command.impl;

import ii.pfc.command.CommandRequest;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class RequestOrderWrapper {

    @XmlAttribute(name = "Number")
    private int orderId;

    @XmlElement(name = "Unload")
    private CommandRequestUnload unload;

    @XmlElement(name = "Transform")
    private CommandRequestTransform transform;

    /*

     */

    public CommandRequest getRequest() {
        if (unload != null) {
            unload.orderId = orderId;
            return unload;
        }

        if (transform != null) {
            transform.orderId = orderId;
            return transform;
        }

        return null;
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

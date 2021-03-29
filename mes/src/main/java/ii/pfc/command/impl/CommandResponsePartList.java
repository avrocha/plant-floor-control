package ii.pfc.command.impl;

import ii.pfc.command.CommandResponse;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Current_Stores")
public class CommandResponsePartList implements CommandResponse {

    @XmlElement(name = "WorkPiece")
    private final Set<ResponsePartData> responseList;

    public CommandResponsePartList() {
        this.responseList = new HashSet<>();
    }

    /*

     */

    public void addPartList(String type, int quantity) {
        responseList.add(new ResponsePartData(type, quantity));
    }

    /*

     */

    private static class ResponsePartData {

        @XmlAttribute(name = "type")
        private final String type;

        @XmlAttribute(name = "quantity")
        private final int quantity;

        public ResponsePartData(String type, int quantity) {
            this.type = type;
            this.quantity = quantity;
        }

        /*

         */

        @Override
        public int hashCode() {
            return Objects.hash(type);
        }
    }

}

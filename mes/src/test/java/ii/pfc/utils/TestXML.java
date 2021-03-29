package ii.pfc.utils;

import ii.pfc.command.impl.CommandResponseOrderList;
import ii.pfc.command.impl.CommandResponsePartList;
import ii.pfc.command.impl.RequestOrderWrapper;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.bind.JAXB;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestXML {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testUnload() {

        try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream("xml/unload.xml")) {
            RequestOrderWrapper wrapper = JAXB.unmarshal(stream, RequestOrderWrapper.class);
            wrapper.onReceive();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Test
    public void testTransform() {

        try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream("xml/transform.xml")) {
            RequestOrderWrapper wrapper = JAXB.unmarshal(stream, RequestOrderWrapper.class);
            wrapper.onReceive();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Test
    public void testPartList() {

        CommandResponsePartList partList = new CommandResponsePartList();
        partList.addPartList("TESTE", 25);
        partList.addPartList("A", 15);
        partList.addPartList("B", 35);
        partList.addPartList("D", 4);

        JAXB.marshal(partList, System.out);

    }

    @Test
    public void testOrderList() {

        CommandResponseOrderList orderList = new CommandResponseOrderList();

        orderList.addOrder(1, "S1", "T1", 10, 2, 4, 4, 100, 0, 200, 20, 50, 150, 0);
        orderList.addOrder(2, "S2", "T2", 20, 5, 4, 2, 130, 0, 200, 20, 50, 150, 0);
        orderList.addOrder(3, "S3", "T3", 13, 2, 8, 4, 100, 0, 200, 20, 50, 150, 0);

        JAXB.marshal(orderList, System.out);

    }

}

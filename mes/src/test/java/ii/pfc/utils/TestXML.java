package ii.pfc.utils;

import ii.pfc.command.impl.CommandResponseOrderList;
import ii.pfc.command.impl.CommandResponsePartList;
import ii.pfc.command.impl.RequestOrderWrapper;
import ii.pfc.part.PartType;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXB;
import java.io.IOException;
import java.io.InputStream;

public class TestXML {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testUnload() {

        try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream("xml/unload.xml")) {
            RequestOrderWrapper wrapper = JAXB.unmarshal(stream, RequestOrderWrapper.class);
            logger.info(wrapper.getRequest().toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Test
    public void testTransform() {

        try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream("xml/transform.xml")) {
            RequestOrderWrapper wrapper = JAXB.unmarshal(stream, RequestOrderWrapper.class);
            logger.info(wrapper.getRequest().toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Test
    public void testPartList() {

        CommandResponsePartList partList = new CommandResponsePartList();
        partList.addPartList(PartType.PART_1, 25);
        partList.addPartList(PartType.PART_2, 15);
        partList.addPartList(PartType.PART_3, 35);
        partList.addPartList(PartType.PART_4, 4);

        JAXB.marshal(partList, System.out);

    }

    @Test
    public void testOrderList() {

        CommandResponseOrderList orderList = new CommandResponseOrderList();

        orderList.addOrder(1, PartType.PART_1, PartType.PART_4, 10, 2, 4, 4, 100, 0, 200, 20, 50, 150, 0);
        orderList.addOrder(2, PartType.PART_2, PartType.PART_3, 20, 5, 4, 2, 130, 0, 200, 20, 50, 150, 0);
        orderList.addOrder(3, PartType.PART_2, PartType.PART_5, 13, 2, 8, 4, 100, 0, 200, 20, 50, 150, 0);

        JAXB.marshal(orderList, System.out);

    }

}

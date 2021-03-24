package ii.pfc.utils;

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

}

package ii.pfc.utils;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.junit.Test;

public class TestXML {

    @Test
    public void testRead() {

        try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream("test.xml")) {
            JAXBContext context = JAXBContext.newInstance(Bean.class);
            Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
            Bean bean = (Bean) jaxbUnmarshaller.unmarshal(stream);
            System.out.println(bean);
        } catch (IOException | JAXBException ex) {
            ex.printStackTrace();
        }

    }

    @XmlRootElement
    public static class Bean {

        @XmlElement
        protected String name;

        @XmlElement
        protected int age;

        @XmlElement
        protected int id;

        @Override
        public String toString() {
            return "Bean{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", id=" + id +
                '}';
        }
    }

}

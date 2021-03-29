package ii.pfc.part.xml;

import ii.pfc.part.PartType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class PartTypeAdapter extends XmlAdapter<String, PartType> {

    @Override
    public PartType unmarshal(String v) throws Exception {
        return PartType.getType(v);
    }

    @Override
    public String marshal(PartType v) throws Exception {
        return v.getName();
    }
}

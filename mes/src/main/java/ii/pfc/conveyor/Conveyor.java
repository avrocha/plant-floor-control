package ii.pfc.conveyor;

import ii.pfc.part.Part;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Conveyor {

    private final short id;

    private final EnumConveyorType type;

    private final List<Part> partList;

    public Conveyor(int id, EnumConveyorType type) {
        this.id = (short) id;
        this.type = type;

        this.partList = new ArrayList<>();
    }

    /*

     */

    public short getId() {
        return id;
    }

    public EnumConveyorType getType() {
        return type;
    }

    public List<Part> getParts() {
        return Collections.unmodifiableList(partList);
    }

    /*

     */

    public boolean hasPart(Part part) {
        return partList.contains(part);
    }

    /*

     */

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}

package ii.pfc.region;

import ii.pfc.conveyor.Conveyor;
import ii.pfc.part.Part;
import ii.pfc.part.PartType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Region {

    private final String name;

    private final List<Conveyor> conveyorInputList;

    private final List<Conveyor> conveyorOutputList;

    private final List<Conveyor> conveyorAllList;

    public Region(String name) {
        this.name = name;

        this.conveyorInputList = new ArrayList<>();
        this.conveyorOutputList = new ArrayList<>();
        this.conveyorAllList = new ArrayList<>();
    }

    /*

     */

    public String getName() {
        return name;
    }

    public List<Conveyor> getInputConveyors() {
        return Collections.unmodifiableList(this.conveyorInputList);
    }

    public List<Conveyor> getOutputConveyors() {
        return Collections.unmodifiableList(this.conveyorOutputList);
    }

    public List<Conveyor> getAllConveyors() {
        return Collections.unmodifiableList(this.conveyorAllList);
    }

    /*

     */

    public boolean hasConveyor(Conveyor conveyor) {
        return this.conveyorAllList.contains(conveyor);
    }

    public boolean hasPart(Part part) {
        for (Conveyor conveyor : this.conveyorAllList) {
            if(conveyor.hasPart(part)) {
                return true;
            }
        }

        return false;
    }

    /*

     */

    public int getNumParts() {
        int count = 0;

        for (Conveyor conveyor : this.conveyorAllList) {
            count += conveyor.getParts().size();
        }

        return count;
    }

    public int getNumParts(PartType type) {
        int count = 0;

        for (Conveyor conveyor : this.conveyorAllList) {
            for (Part part : conveyor.getParts()) {
                if(part.getType() == type) {
                    count++;
                }
            }
        }

        return count;
    }

    /*

     */

    public boolean canReceiveFrom() {
        return true;
    }

    public void send(Conveyor conveyor) {

    }
}

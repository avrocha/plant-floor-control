package ii.pfc.route;

import ii.pfc.conveyor.Conveyor;
import ii.pfc.part.Part;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Route {

    private final Part part;

    private final List<Conveyor> conveyorList;

    public Route(Part part) {
        this.part = part;
        this.conveyorList = new ArrayList<>();
    }

    /*

     */

    public void addConveyor(Conveyor conveyor) {
        this.conveyorList.add(conveyor);
    }

    public List<Conveyor> getConveyors() {
        return Collections.unmodifiableList(this.conveyorList);
    }
}

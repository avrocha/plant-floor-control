package ii.pfc.route;

import ii.pfc.conveyor.Conveyor;
import ii.pfc.part.Part;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Route {

    private final Part part;

    private final List<Conveyor> conveyorList;

    private final double weight;

    public Route(Part part, double weight) {
        this.part = part;
        this.weight = weight;
        this.conveyorList = new ArrayList<>();
    }

    /*

     */

    public Part getPart() {
        return part;
    }

    public double getWeight() {
        return weight;
    }

    /*

     */

    public Conveyor getSource() {
        return conveyorList.isEmpty() ? null : conveyorList.get(0);
    }

    public Conveyor getTarget() {
        return conveyorList.isEmpty() ? null : conveyorList.get(conveyorList.size() - 1);
    }

    public void addConveyor(Conveyor conveyor) {
        this.conveyorList.add(conveyor);
    }

    public List<Conveyor> getConveyors() {
        return Collections.unmodifiableList(this.conveyorList);
    }

    /*

     */

    @Override
    public String toString() {
        return "Route{" +
                "part=" + part +
                ", conveyorList=" + conveyorList +
                '}';
    }
}

package ii.pfc.route;

import ii.pfc.conveyor.Conveyor;
import ii.pfc.part.Part;

public class RouteData {

    private final Part part;

    private final Conveyor start;

    private final Conveyor end;

    /*

     */

    public RouteData(Part part, Conveyor start, Conveyor end) {
        this.part = part;
        this.start = start;
        this.end = end;
    }

    /*

     */

    public Part getPart() {
        return part;
    }

    public Conveyor getStart() {
        return start;
    }

    public Conveyor getEnd() {
        return end;
    }
}

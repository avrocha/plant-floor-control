package ii.pfc.manager;

import ii.pfc.conveyor.Conveyor;
import ii.pfc.conveyor.EnumConveyorType;
import ii.pfc.part.Part;
import ii.pfc.route.Route;
import java.util.Collection;

public interface IRoutingManager {

    Conveyor getConveyor(short conveyorId);

    Collection<Conveyor> getConveyors(EnumConveyorType conveyorType);

    /*

     */

    Route traceRoute(Part part, Conveyor source, Conveyor target);

    Route[] traceRoutes(Part part, Conveyor source, Conveyor[] targets);

}

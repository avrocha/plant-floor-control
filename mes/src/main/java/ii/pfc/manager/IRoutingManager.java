package ii.pfc.manager;

import ii.pfc.conveyor.Conveyor;
import ii.pfc.conveyor.EnumConveyorType;
import ii.pfc.part.Part;
import ii.pfc.part.Process;
import ii.pfc.route.Route;

import java.util.Collection;

public interface IRoutingManager {

    Conveyor getConveyor(short conveyorId);

    Collection<Conveyor> getConveyors(EnumConveyorType conveyorType);

    /*

     */

    Route traceRoute(Part part, Process process, Conveyor source, Conveyor target);

}

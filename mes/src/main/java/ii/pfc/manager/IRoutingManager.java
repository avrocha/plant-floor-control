package ii.pfc.manager;

import ii.pfc.conveyor.Conveyor;
import ii.pfc.part.Part;
import ii.pfc.route.Route;

public interface IRoutingManager {

    Route traceRoute(Part part, Conveyor source, Conveyor target);

    Route[] traceRoutes(Part part, Conveyor source, Conveyor[] targets);

}

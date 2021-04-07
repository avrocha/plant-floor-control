package ii.pfc.manager;

import ii.pfc.conveyor.Conveyor;
import ii.pfc.route.Route;
import ii.pfc.route.RouteData;

public interface IRoutingManager {

    Route traceRoute(RouteData data);

    Route[] traceRoutes(Conveyor source, Conveyor[] targets);

}

package ii.pfc.manager;

import com.google.common.collect.Sets;
import ii.pfc.conveyor.Conveyor;
import ii.pfc.route.Route;
import ii.pfc.route.RouteData;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ManyToManyShortestPathsAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraManyToManyShortestPaths;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.AsWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

public class RoutingManager implements IRoutingManager {

    public static final int PATH_MAX_WEIGHT = 100000;

    /*

     */

    private Graph<Conveyor, ConveyorEdge> regionGraph;

    private RoutingManager(Graph<Conveyor, ConveyorEdge> graph) {
        this.regionGraph = new AsWeightedGraph<>(
                graph,
                Collections.emptyMap(),
                false
        ) {
            @Override
            public double getEdgeWeight(ConveyorEdge testEdge) {
                return testEdge.getWeight();
            }
        };
    }

    /*

     */

    @Override
    public Route traceRoute(RouteData data) {
        synchronized (ConveyorEdge.LOCK) {
            ConveyorEdge.currentRouteData = data;
            GraphPath<Conveyor, ConveyorEdge> path = DijkstraShortestPath.findPathBetween(this.regionGraph, data.getStart(), data.getEnd());
            ConveyorEdge.currentRouteData = null;

            if (path == null || path.getWeight() > PATH_MAX_WEIGHT) {
                return null;
            }

            Route route = new Route(data.getPart());
            route.addConveyor(data.getStart());

            for (ConveyorEdge edge : path.getEdgeList()) {
                route.addConveyor(this.regionGraph.getEdgeTarget(edge));
            }

            return route;
        }
    }

    @Override
    public Route[] traceRoutes(Conveyor source, Conveyor[] targets) {
        synchronized (ConveyorEdge.LOCK) {
            ConveyorEdge.currentRouteData = new RouteData(null, source, source);
            ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<Conveyor, ConveyorEdge> many = new DijkstraManyToManyShortestPaths<>(this.regionGraph).getManyToManyPaths(Sets.newHashSet(source), Sets.newHashSet(targets));

            Route[] routes = new Route[targets.length];
            for (int i = 0; i < targets.length; i++) {
                Conveyor target = targets[i];
                if (many.getWeight(source, target) > PATH_MAX_WEIGHT) {
                    routes[i] = null;
                    continue;
                }

                GraphPath<Conveyor, ConveyorEdge> path = many.getPath(source, target);

                Route route = new Route(null);
                route.addConveyor(source);

                for (ConveyorEdge edge : path.getEdgeList()) {
                    route.addConveyor(this.regionGraph.getEdgeTarget(edge));
                }

                routes[i] = route;
            }

            ConveyorEdge.currentRouteData = null;

            return routes;
        }
    }

    /*

     */

    public static Builder builder() {
        return new Builder();
    }

    /*

     */

    public static class Builder {

        private SimpleDirectedWeightedGraph<Conveyor, ConveyorEdge> _builderGraph;

        private Builder() {
            _builderGraph = new SimpleDirectedWeightedGraph(ConveyorEdge.class);
        }

        /*

         */

        public Builder unidirectional(Conveyor a, Conveyor b, Function<RouteData, Double> weightFunction) {
            this._builderGraph.addVertex(a);
            this._builderGraph.addVertex(b);

            this._builderGraph.addEdge(a, b, new ConveyorEdge(weightFunction));
            return this;
        }

        //

        public Builder bidirectional(Conveyor a, Conveyor b, Function<RouteData, Double> weightFunction) {
            return bidirectional(a, b, weightFunction, weightFunction);
        }

        public Builder bidirectional(Conveyor a, Conveyor b, Function<RouteData, Double> weightFunctionAB, Function<RouteData, Double> weightFunctionBA) {
            unidirectional(a, b, weightFunctionAB);
            unidirectional(b, a, weightFunctionBA);

            return this;
        }

        /*

         */

        public RoutingManager build() {
            return new RoutingManager(this._builderGraph);
        }

    }

    /*

     */

    private static class ConveyorEdge extends DefaultWeightedEdge {

        private static final Object LOCK = new Object();

        private static RouteData currentRouteData;

        /*

         */

        private final Function<RouteData, Double> weightFunction;

        private ConveyorEdge() {
            this(null);
        }

        private ConveyorEdge(Function<RouteData, Double> weightFunction) {
            this.weightFunction = weightFunction;
        }

        @Override
        protected double getWeight() {
            if (weightFunction != null) {
                return weightFunction.apply(currentRouteData);
            }

            return super.getWeight();
        }
    }

}

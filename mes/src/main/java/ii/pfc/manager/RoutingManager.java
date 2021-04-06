package ii.pfc.manager;

import ii.pfc.conveyor.Conveyor;
import ii.pfc.route.RouteData;
import java.util.Collections;
import java.util.function.Function;
import org.jgrapht.Graph;
import org.jgrapht.graph.AsWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

public class RoutingManager implements IRoutingManager {

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

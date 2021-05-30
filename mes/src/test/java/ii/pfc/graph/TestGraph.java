package ii.pfc.graph;

import junit.framework.TestCase;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.AsWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TestGraph extends TestCase {

    private static final Bean A = new Bean("A");
    private static final Bean B = new Bean("B");
    private static final Bean C = new Bean("C");
    private static final Bean D = new Bean("D");
    private static final Bean E = new Bean("E");
    private static final Bean F = new Bean("F");
    private static final Bean G = new Bean("G");
    private static final Bean H = new Bean("H");
    private static final Bean I = new Bean("I");
    private static final Bean J = new Bean("J");

    /*

     */

    Graph<Bean, TestEdge> graph = new AsWeightedGraph<>(
            SimpleDirectedWeightedGraph.<Bean, TestEdge>createBuilder(TestEdge.class)
                    .addEdge(A, B)
                    .addEdge(B, C)
                    .addEdge(C, D)
                    .addEdge(D, E)
                    .addEdge(D, F)
                    .addEdge(E, I)
                    .addEdge(F, G)
                    .addEdge(F, I)
                    .addEdge(G, J)
                    .addEdge(I, J)
                    .addEdge(D, J, new TestEdge((data) -> {
                        return data.id.startsWith("A-") ? 10000.0 : 0;
                    }))
                    .build(),
            Collections.emptyMap(),
            false
    ) {
        @Override
        public double getEdgeWeight(TestEdge testEdge) {
            return testEdge.getWeight();
        }
    };

    @Test
    public void testTraverser() {
        assertEquals("A-B-C-D-E-I-J", shortestPath(A, J));
        assertEquals("B-C-D-J", shortestPath(B, J));
        assertEquals("A-B-C-D", shortestPath(A, D));
        assertEquals("", shortestPath(D, A));
        assertEquals("B-C-D-F-G", shortestPath(B, G));
    }

    private String shortestPath(Bean source, Bean target) {
        synchronized (TestEdge.pathLock) {
            TestEdge.pathData = new TestEdge.PathData(source.name + "-" + target.name);
            GraphPath<Bean, TestEdge> path = DijkstraShortestPath.findPathBetween(graph, source, target);
            TestEdge.pathData = null;

            if (path == null) {
                return "";
            }

            Set<Bean> set = new LinkedHashSet<>();
            for (TestEdge edge : path.getEdgeList()) {
                set.add(graph.getEdgeSource(edge));
                set.add(graph.getEdgeTarget(edge));
            }

            return set.stream().map(b -> b.name).collect(Collectors.joining("-"));
        }
    }

    private static class Bean {

        private final String name;

        private Bean(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Bean{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    private static class TestEdge extends DefaultWeightedEdge {

        public static final Object pathLock = new Object();

        public static PathData pathData;

        private final Function<PathData, Double> weightOverride;

        private TestEdge() {
            this(null);
        }

        private TestEdge(Function<PathData, Double> weightOverride) {
            this.weightOverride = weightOverride;
        }

        /*

         */

        @Override
        protected double getWeight() {
            if (weightOverride != null) {
                return weightOverride.apply(pathData);
            }

            return super.getWeight();
        }

        private static class PathData {

            public String id;

            public PathData(String id) {
                this.id = id;
            }

        }
    }

}

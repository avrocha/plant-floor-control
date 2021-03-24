package ii.pfc.graph;

import com.google.common.graph.*;
import junit.framework.TestCase;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.GraphType;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.*;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
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

    Graph<Bean, DefaultWeightedEdge> graph = new AsWeightedGraph<>(
            SimpleDirectedWeightedGraph.<Bean, DefaultWeightedEdge>createBuilder(DefaultWeightedEdge.class)
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
                    .addEdge(D, J)
                    .build(),
            Collections.emptyMap(),
            false
    ) {
        @Override
        public double getEdgeWeight(DefaultWeightedEdge o) {
            Bean bean = getEdgeSource(o);
            Bean target = getEdgeTarget(o);
            return bean.equals(D) && target.equals(J) ? 10000 : 1;
        }
    };

    @Test
    public void testTraverser() {
        assertEquals("A-B-C-D-E-I-J", shortestPath(A, J));
        assertEquals("A-B-C-D", shortestPath(A, D));
        assertEquals("", shortestPath(D, A));
        assertEquals("B-C-D-F-G", shortestPath(B, G));
    }

    private String shortestPath(Bean source, Bean target) {
        GraphPath<Bean, DefaultWeightedEdge> path = DijkstraShortestPath.findPathBetween(graph, source, target);
        if (path == null) {
            return "";
        }

        Set<Bean> set = new LinkedHashSet<>();
        for(DefaultWeightedEdge edge : path.getEdgeList()) {
            set.add(graph.getEdgeSource(edge));
            set.add(graph.getEdgeTarget(edge));
        }

        return set.stream().map(b -> b.name).collect(Collectors.joining("-"));
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

}

package ii.pfc.part;

import com.google.common.collect.Lists;
import ii.pfc.conveyor.Conveyor;
import ii.pfc.manager.RoutingManager;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.SimpleGraph;

import java.util.Collection;
import java.util.List;

public class ProcessRegistry {

    private static final Graph<PartType, ProcessEdge> graph = new DefaultUndirectedGraph<>(ProcessEdge.class);

    public static void registerProcess(Process process) {
        System.out.println(process.toString());
        graph.addVertex(process.getSource());
        graph.addVertex(process.getResult());
        graph.addEdge(process.getSource(), process.getResult(), new ProcessEdge(process));
    }

    public static List<Process> getProcesses(PartType current, PartType target) {
        GraphPath<PartType, ProcessEdge> path = DijkstraShortestPath.findPathBetween(graph, current, target);

        List<Process> output = Lists.newArrayList();
        for (ProcessEdge edge : path.getEdgeList()) {
            output.add(edge.process);
        }

        return output;
    }

    public static Graph<PartType, ProcessEdge> getProcessGraph() {
        return graph;
    }

    /*

     */

    private static class ProcessEdge extends DefaultEdge {

        private final Process process;

        private ProcessEdge(Process process) {
            this.process = process;
        }
    }
}

package ii.pfc.part;

import com.google.common.collect.Lists;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

import java.util.List;

public class ProcessRegistry {

    public static ProcessRegistry INSTANCE;

    private final Graph<PartType, ProcessEdge> graph = new DefaultUndirectedGraph<>(ProcessEdge.class);

    public ProcessRegistry() {
        INSTANCE = this;
    }

    /*

     */

    public void registerProcess(Process process) {
        graph.addVertex(process.getSource());
        graph.addVertex(process.getResult());
        graph.addEdge(process.getSource(), process.getResult(), new ProcessEdge(process));
    }

    public Process getProcess(PartType current, PartType target) {
        ProcessEdge edge = graph.getEdge(current, target);
        return edge == null ? null : edge.process;
    }

    public List<Process> getProcesses(PartType current, PartType target) {
        GraphPath<PartType, ProcessEdge> path = DijkstraShortestPath.findPathBetween(graph, current, target);

        List<Process> output = Lists.newArrayList();
        for (ProcessEdge edge : path.getEdgeList()) {
            output.add(edge.process);
        }

        return output;
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

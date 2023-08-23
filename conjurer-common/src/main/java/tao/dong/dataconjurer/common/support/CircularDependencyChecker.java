package tao.dong.dataconjurer.common.support;

import jakarta.validation.constraints.NotNull;
import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Map;
import java.util.Set;

public class CircularDependencyChecker {
    public boolean hasCircular(@NotNull Map<String, Set<String>> nodes) {
        if (!nodes.isEmpty()) {
            var graph = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
            for (var node : nodes.entrySet()) {
                String sv = node.getKey();
                graph.addVertex(sv);
                var targets = node.getValue();
                for (var tv : targets) {
                    graph.addVertex(tv);
                    graph.addEdge(sv, tv);
                }
            }
            var cycleDetector = new CycleDetector<>(graph);
            return cycleDetector.detectCycles();
        }

        return false;
    }

}

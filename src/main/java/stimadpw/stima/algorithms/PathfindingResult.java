package stimadpw.stima.algorithms;

import stimadpw.stima.state.Node;
import java.util.List;

/**
 * A data class to hold the results of a pathfinding calculation,
 * including the path itself and statistics about the search.
 */
public class PathfindingResult {
    private final List<Node> path;
    private final int expandedNodes;

    public PathfindingResult(List<Node> path, int expandedNodes) {
        this.path = path;
        this.expandedNodes = expandedNodes;
    }

    /**
     * Gets the calculated path.
     */
    public List<Node> getPath() {
        return path;
    }

    /**
     * Gets the total number of nodes that were expanded (evaluated) by the algorithm.
     */
    public int getExpandedNodes() {
        return expandedNodes;
    }

    /**
     * Checks if a valid path was found.
     */
    public boolean hasPath() {
        return path != null && !path.isEmpty();
    }
}
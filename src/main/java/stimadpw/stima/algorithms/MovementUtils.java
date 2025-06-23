package stimadpw.stima.algorithms;

import stimadpw.stima.state.Node;
import net.minecraft.util.math.BlockPos;

/**
 * A utility class containing static helper methods for movement calculations.
 */
public final class MovementUtils {
    private MovementUtils() {}

    /**
     * Calculates the true 3D straight-line distance between two points.
     */
    public static double getEuclideanDistance(BlockPos startPos, BlockPos endPos) {
        return Math.sqrt(startPos.getSquaredDistance(endPos));
    }

    /**
     * Calculates the cost of a single move from a parent node to a new position.
     */
    public static double calculateMoveCost(Node startNode, BlockPos destinationPos, boolean isJump, double jumpStrength) {
        BlockPos startPos = startNode.getPos();

        // Calculate the 2D distance on the XZ plane for the move
        double deltaX = startPos.getX() - destinationPos.getX();
        double deltaZ = startPos.getZ() - destinationPos.getZ();
        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        if (isJump) {
            // Weaker/shorter jumps are more "expensive"
            horizontalDistance = horizontalDistance / Constants.JUMP_COST_DISTANCE_SCALAR * Constants.JUMP_COST_BENCHMARK_FORCE / jumpStrength;
        }

        return horizontalDistance;
    }
}
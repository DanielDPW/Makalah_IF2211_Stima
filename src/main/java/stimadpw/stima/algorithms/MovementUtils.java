package stimadpw.stima.algorithms;

import stimadpw.stima.state.Node;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

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

    /**
     * Checks if an entity's precise position is close enough to the center of a target block to be considered "arrived".
     */
    public static boolean hasArrived(Vec3d currentEntityPos, BlockPos targetBlock) {
        double distanceSquared = Math.pow(targetBlock.getX() + 0.5D - currentEntityPos.getX(), 2) + Math.pow(targetBlock.getZ() + 0.5D - currentEntityPos.getZ(), 2);
        return distanceSquared < 0.05;
    }

    /**
     * Checks if an entity is near the edge of a block, used to time jumps correctly.
     */
    public static boolean isOnEdge(Vec3d entityPos) {
        double xDecimal = Math.abs(entityPos.getX() % 1.0);
        double zDecimal = Math.abs(entityPos.getZ() % 1.0);
        return xDecimal > Constants.BLOCK_EDGE_UPPER_BOUND
                || xDecimal < Constants.BLOCK_EDGE_LOWER_BOUND
                || zDecimal > Constants.BLOCK_EDGE_UPPER_BOUND
                || zDecimal < Constants.BLOCK_EDGE_LOWER_BOUND;
    }
}
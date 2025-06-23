package stimadpw.stima.algorithms;

import stimadpw.stima.state.*;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.*;

/**
 * Class that implements A* pathfinding algorithm.
 */
public class PathfindingManager {
    private final BlockStateInterface bsi;
    private final BlockCache blockCache;
    private final World world;

    public PathfindingManager(World world) {
        this.world = world;
        this.bsi = new BlockStateInterface(world);
        this.blockCache = new BlockCache(bsi);
    }

    /**
     * Helper method to check, create, and add a valid neighbor to the list.
     */
    private void addNeighbor(List<Node> neighbors, Node parent, BlockPos goal, BlockPos offset, boolean isJump, double jumpForce) {
        BlockPos newPos = parent.getPos().add(offset);
        if (!isValid(newPos)) {
            return;
        }
        if (isJump && !isPathClear(parent.getPos(), newPos, isJump)) {
            return;
        }

        // Calculate G and H
        double g = parent.getG() + MovementUtils.calculateMoveCost(parent, newPos, isJump, jumpForce);;
        double h = MovementUtils.getEuclideanDistance(newPos, goal);

        neighbors.add(new Node(newPos, parent, g, h, isJump, jumpForce));
    }

    /**
     * Checks if a destination is valid (walkable, not dangerous, and clear for the player).
     */
    private boolean isValid(BlockPos pos) {
        BlockState ground = bsi.get(pos.getX(), pos.getY(), pos.getZ());
        BlockState feet = bsi.get(pos.getX(), pos.getY() + 1, pos.getZ());
        BlockState head = bsi.get(pos.getX(), pos.getY() + 2, pos.getZ());

        return blockCache.canWalkOn(pos.getX(), pos.getY(), pos.getZ(), ground)
                && !MovementHelper.isDangerous(ground)
                && blockCache.canWalkThrough(pos.getX(), pos.getY() + 1, pos.getZ(), feet)
                && blockCache.canWalkThrough(pos.getX(), pos.getY() + 2, pos.getZ(), head);
    }

    /**
     * Checks if the path between two points is clear of obstructions.
     */
    private boolean isPathClear(BlockPos src, BlockPos dest, boolean isJump) {
        // Check for head-bonking when jumping up
        if (dest.getY() > src.getY()) {
            BlockState headBonkBlock = bsi.get(src.getX(), src.getY() + 2, src.getZ());
            if (!blockCache.canWalkThrough(src.getX(), src.getY() + 2, src.getZ(), headBonkBlock)) {
                return false; // Path is blocked by a ceiling
            }
        }

        // Check for tight corners on 1-block diagonal moves
        int deltaX = Math.abs(src.getX() - dest.getX());
        int deltaZ = Math.abs(src.getZ() - dest.getZ());
        if (!isJump && src.getY() == dest.getY() && deltaX == 1 && deltaZ == 1) {
            BlockState corner1 = bsi.get(src.getX() + (dest.getX() - src.getX()), src.getY(), src.getZ());
            BlockState corner2 = bsi.get(src.getX(), src.getY(), src.getZ() + (dest.getZ() - src.getZ()));
            if (!blockCache.canWalkThrough(0,0,0, corner1) && !blockCache.canWalkThrough(0,0,0, corner2)) {
                return false;
            }
        }

        // Check for obstructions and runway space during long, flat jumps
        if (isJump && src.getY() == dest.getY() && src.getManhattanDistance(dest) > 1) {
            // Check for a low ceiling at the starting point of a same-level jump
            BlockState headBonkBlock = bsi.get(src.getX(), src.getY() + 2, src.getZ());
            if (!blockCache.canWalkThrough(src.getX(), src.getY() + 2, src.getZ(), headBonkBlock)) {
                return false; // Cannot jump if ceiling is too low at the start
            }

            // --- Runway Check ---
            int jumpDistance = Math.max(deltaX, deltaZ);
            if (jumpDistance >= 4) {
                BlockPos jumpVector = dest.subtract(src);
                BlockPos behind1 = src.subtract(new BlockPos(Integer.signum(jumpVector.getX()), 0, Integer.signum(jumpVector.getZ())));
                BlockPos behind2 = behind1.subtract(new BlockPos(Integer.signum(jumpVector.getX()), 0, Integer.signum(jumpVector.getZ())));
                if (!isValid(behind1) || !isValid(behind2)) {
                    return false;
                }
            }

            // Use a line-drawing algorithm to check the actual path of the jump.
            int steps = Math.max(deltaX, deltaZ);
            if (steps == 0) return true; // Same block, no line to check.

            float xInc = (float) deltaX / (float) steps;
            float zInc = (float) deltaZ / (float) steps;

            // Ensure the sign is correct for negative directions
            if (dest.getX() < src.getX()) xInc = -xInc;
            if (dest.getZ() < src.getZ()) zInc = -zInc;

            float currentX = src.getX();
            float currentZ = src.getZ();

            // Check each block along the line of the jump
            for (int i = 0; i < steps; i++) {
                currentX += xInc;
                currentZ += zInc;
                BlockPos currentBlock = new BlockPos(Math.round(currentX), src.getY(), Math.round(currentZ));

                // Check for a 3-block-high clearance tunnel (feet, head, and jump arc)
                for (int yOffset = 1; yOffset <= 3; yOffset++) {
                    BlockPos checkPos = currentBlock.up(yOffset);
                    BlockState state = bsi.get(checkPos.getX(), checkPos.getY(), checkPos.getZ());
                    if (!blockCache.canWalkThrough(checkPos.getX(), checkPos.getY(), checkPos.getZ(), state)) {
                        return false; // Path is blocked
                    }
                }
            }
        }

        return true;
    }

    /**
     * Generates all valid neighboring nodes by translating your original getList method.
     * This is where you define all possible moves: walking, jumping, falling, etc.
     */
    private List<Node> getNeighbors(Node currentNode, BlockPos goal) {
        List<Node> neighbors = new ArrayList<>();

        // Walking moves
        addNeighbor(neighbors, currentNode, goal, new BlockPos(1, 0, 0), false, Constants.DEFAULT_WALKING_FORCE);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-1, 0, 0), false, Constants.DEFAULT_WALKING_FORCE);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(0, 0, 1), false, Constants.DEFAULT_WALKING_FORCE);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(0, 0, -1), false, Constants.DEFAULT_WALKING_FORCE);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(1, 0, 1), false, Constants.DEFAULT_WALKING_FORCE);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-1, 0, -1), false, Constants.DEFAULT_WALKING_FORCE);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-1, 0, 1), false, Constants.DEFAULT_WALKING_FORCE);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(1, 0, -1), false, Constants.DEFAULT_WALKING_FORCE);

        // Jump-up moves
        addNeighbor(neighbors, currentNode, goal, new BlockPos(1, 1, 0), true, Constants.FORCE_SHORT_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-1, 1, 0), true, Constants.FORCE_SHORT_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(0, 1, 1), true, Constants.FORCE_SHORT_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(0, 1, -1), true, Constants.FORCE_SHORT_JUMP_B);

        // Falling moves
        addNeighbor(neighbors, currentNode, goal, new BlockPos(1, -1, 0), false, Constants.FORCE_FALLING_MOVE);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-1, -1, 0), false, Constants.FORCE_FALLING_MOVE);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(0, -1, 1), false, Constants.FORCE_FALLING_MOVE);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(0, -1, -1), false, Constants.FORCE_FALLING_MOVE);

        // Sprint-jump moves
        // Long-jump
        addNeighbor(neighbors, currentNode, goal, new BlockPos(5, 0, 0), true, Constants.FORCE_LONG_JUMP_A);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(0, 0, 5), true, Constants.FORCE_LONG_JUMP_A);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-5, 0, 0), true, Constants.FORCE_LONG_JUMP_A);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(0, 0, -5), true, Constants.FORCE_LONG_JUMP_A);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(4, 0, 3), true, Constants.FORCE_LONG_JUMP_A);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(4, 0, -3), true, Constants.FORCE_LONG_JUMP_A);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(3, 0, 4), true, Constants.FORCE_LONG_JUMP_A);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-3, 0, 4), true, Constants.FORCE_LONG_JUMP_A);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-4, 0, 3), true, Constants.FORCE_LONG_JUMP_A);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-4, 0, -3), true, Constants.FORCE_LONG_JUMP_A);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(3, 0, -4), true, Constants.FORCE_LONG_JUMP_A);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-3, 0, -4), true, Constants.FORCE_LONG_JUMP_A);

        addNeighbor(neighbors, currentNode, goal, new BlockPos(4, 0, 1), true, Constants.FORCE_LONG_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(4, 0, -1), true, Constants.FORCE_LONG_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(4, 0, 2), true, Constants.FORCE_LONG_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(4, 0, -2), true, Constants.FORCE_LONG_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(1, 0, 4), true, Constants.FORCE_LONG_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-1, 0, 4), true, Constants.FORCE_LONG_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(2, 0, 4), true, Constants.FORCE_LONG_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-2, 0, 4), true, Constants.FORCE_LONG_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-4, 0, 1), true, Constants.FORCE_LONG_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-4, 0, -1), true, Constants.FORCE_LONG_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-4, 0, 2), true, Constants.FORCE_LONG_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-4, 0, -2), true, Constants.FORCE_LONG_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(1, 0, -4), true, Constants.FORCE_LONG_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-1, 0, -4), true, Constants.FORCE_LONG_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(2, 0, -4), true, Constants.FORCE_LONG_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-2, 0, -4), true, Constants.FORCE_LONG_JUMP_B);

        // Medium-jump
        addNeighbor(neighbors, currentNode, goal, new BlockPos(4, 0, 0), true, Constants.FORCE_MEDIUM_JUMP_A);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(0, 0, 4), true, Constants.FORCE_MEDIUM_JUMP_A);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-4, 0, 0), true, Constants.FORCE_MEDIUM_JUMP_A);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(0, 0, -4), true, Constants.FORCE_MEDIUM_JUMP_A);

        addNeighbor(neighbors, currentNode, goal, new BlockPos(3, 0, 3), true, Constants.FORCE_MEDIUM_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(3, 0, -3), true, Constants.FORCE_MEDIUM_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-3, 0, 3), true, Constants.FORCE_MEDIUM_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-3, 0, -3), true, Constants.FORCE_MEDIUM_JUMP_B);

        addNeighbor(neighbors, currentNode, goal, new BlockPos(3, 0, 1), true, Constants.FORCE_MEDIUM_JUMP_C);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(3, 0, -1), true, Constants.FORCE_MEDIUM_JUMP_C);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(3, 0, 2), true, Constants.FORCE_MEDIUM_JUMP_C);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(3, 0, -2), true, Constants.FORCE_MEDIUM_JUMP_C);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-3, 0, 1), true, Constants.FORCE_MEDIUM_JUMP_C);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-3, 0, -1), true, Constants.FORCE_MEDIUM_JUMP_C);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-3, 0, 2), true, Constants.FORCE_MEDIUM_JUMP_C);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-3, 0, -2), true, Constants.FORCE_MEDIUM_JUMP_C);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(1, 0, 3), true, Constants.FORCE_MEDIUM_JUMP_C);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-1, 0, 3), true, Constants.FORCE_MEDIUM_JUMP_C);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(2, 0, 3), true, Constants.FORCE_MEDIUM_JUMP_C);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-2, 0, 3), true, Constants.FORCE_MEDIUM_JUMP_C);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(1, 0, -3), true, Constants.FORCE_MEDIUM_JUMP_C);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-1, 0, -3), true, Constants.FORCE_MEDIUM_JUMP_C);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(2, 0, -3), true, Constants.FORCE_MEDIUM_JUMP_C);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-2, 0, -3), true, Constants.FORCE_MEDIUM_JUMP_C);

        // Short-jump
        addNeighbor(neighbors, currentNode, goal, new BlockPos(3, 0, 0), true, Constants.FORCE_SHORT_JUMP_A);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(0, 0, 3), true, Constants.FORCE_SHORT_JUMP_A);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-3, 0, 0), true, Constants.FORCE_SHORT_JUMP_A);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(0, 0, -3), true, Constants.FORCE_SHORT_JUMP_A);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(2, 0, 2), true, Constants.FORCE_SHORT_JUMP_A);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(2, 0, -2), true, Constants.FORCE_SHORT_JUMP_A);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-2, 0, 2), true, Constants.FORCE_SHORT_JUMP_A);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-2, 0, -2), true, Constants.FORCE_SHORT_JUMP_A);

        addNeighbor(neighbors, currentNode, goal, new BlockPos(2, 0, 1), true, Constants.FORCE_SHORT_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(2, 0, -1), true, Constants.FORCE_SHORT_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(1, 0, 2), true, Constants.FORCE_SHORT_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-1, 0, 2), true, Constants.FORCE_SHORT_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-2, 0, 1), true, Constants.FORCE_SHORT_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-2, 0, -1), true, Constants.FORCE_SHORT_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(1, 0, -2), true, Constants.FORCE_SHORT_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-1, 0, -2), true, Constants.FORCE_SHORT_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(2, 0, 2), true, Constants.FORCE_SHORT_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(2, 0, -2), true, Constants.FORCE_SHORT_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-2, 0, 2), true, Constants.FORCE_SHORT_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-2, 0, -2), true, Constants.FORCE_SHORT_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(2, 0, 0), true, Constants.FORCE_SHORT_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(-2, 0, 0), true, Constants.FORCE_SHORT_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(0, 0, 2), true, Constants.FORCE_SHORT_JUMP_B);
        addNeighbor(neighbors, currentNode, goal, new BlockPos(0, 0, -2), true, Constants.FORCE_SHORT_JUMP_B);

        return neighbors;
    }

    /**
     * Find a path from the start position to the end position using A* algorithm
     */
    public PathfindingResult findPath(BlockPos startPos, BlockPos endPos) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparing(Node::getF));
        Set<BlockPos> closedSet = new HashSet<>();

        Node startNode = new Node(startPos, null, 0, MovementUtils.getEuclideanDistance(startPos, endPos), false, 0);
        openSet.add(startNode);

        long startTime = System.currentTimeMillis();

        while (!openSet.isEmpty()) {
            if (System.currentTimeMillis() - startTime > Constants.PATHFINDING_TIMEOUT_MS) {
                System.out.println("Pathfinding took too long, timed out!");
                return new PathfindingResult(null, closedSet.size());
            }

            Node currentNode = openSet.poll();

            if (currentNode.getPos().isWithinDistance(endPos, 2.0)) {
                List<Node> path = reconstructPath(currentNode);
                return new PathfindingResult(path, closedSet.size());
            }

            closedSet.add(currentNode.getPos());

            for (Node neighborNode : getNeighbors(currentNode, endPos)) {
                if (closedSet.contains(neighborNode.getPos())) {
                    continue;
                }

                Node existingNode = openSet.stream().filter(n -> n.getPos().equals(neighborNode.getPos())).findFirst().orElse(null);
                if (existingNode == null || neighborNode.getG() < existingNode.getG()) {
                    if (existingNode != null) {
                        openSet.remove(existingNode);
                    }
                    openSet.add(neighborNode);
                }
            }
        }
        return new PathfindingResult(null, closedSet.size()); // No path found
    }

    /**
     * Reconstructs the path by walking backwards from the goal node via its parents.
     */
    private List<Node> reconstructPath(Node goalNode) {
        List<Node> path = new ArrayList<>();
        Node currentNode = goalNode;
        while (currentNode != null) {
            path.add(currentNode);
            currentNode = currentNode.getParent();
        }
        Collections.reverse(path);
        return path;
    }
}
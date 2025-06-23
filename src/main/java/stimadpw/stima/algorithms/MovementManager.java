package stimadpw.stima.algorithms;

import net.minecraft.fluid.Fluids;
import stimadpw.stima.state.Node;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import java.util.List;

/**
 * Takes a path and controls the player's movement on each game tick.
 */
public class MovementManager {
    private List<Node> path;
    private int pathIndex;

    private long timeStuck;
    private double stuckX, stuckZ;
    private boolean hasJumpedFlag;
    private boolean inAir;
    private boolean hasLanded;
    private boolean isSlow;
    private long slowStartTime;

    public MovementManager(List<Node> path) {
        this.path = path;
        this.pathIndex = 0;
        this.timeStuck = System.currentTimeMillis();
        this.hasJumpedFlag = false;
        this.inAir = false;
        this.hasLanded = true;
        this.isSlow = false;
    }

    public boolean isFollowingPath() {
        return path != null && pathIndex < path.size();
    }

    public void tick(PlayerEntity player) {
        if (!isFollowingPath()) {
            player.setVelocity(0, 0, 0);
            return;
        }

        Node currentNode = path.get(pathIndex);
        Vec3d playerPos = player.getPos();

        if (MovementUtils.hasArrived(playerPos, currentNode.getPos())) {
            pathIndex++;
            hasJumpedFlag = false;
            if (!isFollowingPath()) return;
        }

        currentNode = path.get(pathIndex);
        BlockPos targetPos = currentNode.getPos();

        // Check if stuck
        if (playerPos.squaredDistanceTo(stuckX, playerPos.y, stuckZ) < 0.01) {
            if (System.currentTimeMillis() - timeStuck > Constants.STUCK_THRESHOLD_MS && player.isOnGround()) {
                player.jump();
                inAir = true;
                timeStuck = System.currentTimeMillis();
            }
        } else {
            timeStuck = System.currentTimeMillis();
            stuckX = playerPos.x;
            stuckZ = playerPos.z;
        }

        // Force calculation
        double force;
        if (!player.isOnGround() && inAir) {
            force = currentNode.getJumpStrength();
        } else {
            force = Constants.DEFAULT_WALKING_FORCE;
            inAir = false;
        }

        // Slow logic
        if (currentNode.getJumpStrength() == Constants.FORCE_FALLING_MOVE) {
            isSlow = true;
            slowStartTime = System.currentTimeMillis();
        }
        if (isSlow && (System.currentTimeMillis() - slowStartTime < Constants.SLOW_DURATION_MS)) {
            force = Constants.GO_SLOW_FORCE;
        } else {
            isSlow = false;
        }

        // Vertical Velocity & Water Physics
        double verticalVelocity = player.getVelocity().y;
        if (playerPos.y - targetPos.getY() > 0.01) { // Falling towards target
            if (hasLanded) {
                hasJumpedFlag = true;
                hasLanded = false;
            }
            verticalVelocity *= 0.5; // Dampen fall speed
        } else {
            hasLanded = true;
        }

        BlockState playerBlockState = player.getWorld().getBlockState(player.getBlockPos());
        if (playerBlockState.getFluidState().getFluid() == Fluids.WATER) {
            verticalVelocity = Constants.IN_WATER_MOVEMENT_FORCE;
            force = Constants.IN_WATER_MOVEMENT_FORCE;
        }

        // Movement execution
        double angle = Math.atan2(targetPos.getX() + 0.5 - playerPos.x, targetPos.getZ() + 0.5 - playerPos.z);
        player.setVelocity(Math.sin(angle) * force, verticalVelocity, Math.cos(angle) * force);

        // Jump logic
        if (currentNode.isJump() && player.isOnGround() && !hasJumpedFlag && MovementUtils.isOnEdge(playerPos)) {
            player.jump();
            inAir = true;
            hasJumpedFlag = true;
        }
    }
}
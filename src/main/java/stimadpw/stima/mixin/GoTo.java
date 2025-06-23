package stimadpw.stima.mixin;

import stimadpw.stima.algorithms.IPathfindingController;
import stimadpw.stima.algorithms.MovementManager;
import stimadpw.stima.algorithms.PathfindingManager;
import stimadpw.stima.state.Node;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * The main controller mixin.
 */
@Mixin(PlayerEntity.class)
public abstract class GoTo implements IPathfindingController {

	private volatile MovementManager movementManager;
	private volatile boolean isCalculatingPath = false;

	/**
	 * Handles the entire process of starting a new path.
	 */
	@Override
	public void startPathfinding(BlockPos endPos) {
		// If already pathing, stop the old path first.
		if (this.movementManager != null && this.movementManager.isFollowingPath()) {
			stopPathfinding();
		}

		PlayerEntity player = (PlayerEntity) (Object) this;

		// Set the flag to freeze the player while we calculate.
		this.isCalculatingPath = true;

		System.out.println("Calculating path to " + endPos + "...");

		// Run pathfinding on a new thread to prevent the game from freezing.
		new Thread(() -> {
			PathfindingManager pathfinder = new PathfindingManager(player.getWorld());
			List<Node> path = pathfinder.findPath(player.getBlockPos().down(), endPos);

			// Unfreeze the player.
			this.isCalculatingPath = false;

			if (path != null && !path.isEmpty()) {
				System.out.println("Path found! Starting movement.");
				this.movementManager = new MovementManager(path);
			} else {
				System.out.println("Could not find a path to the destination.");
			}
		}).start();
	}

	/**
	 * Public method to stop all pathfinding activity.
	 */
	@Override
	public void stopPathfinding() {
		if (this.movementManager != null) {
			this.movementManager = null;
			System.out.println("Pathing stopped.");
		}
	}

	/**
	 * On each game tick, this method handles freezing the player or running the MovementManager.
	 */
	@Inject(method = "tick", at = @At("HEAD"))
	public void onTick(CallbackInfo ci) {
		// If the 'isCalculatingPath' flag is true, freeze the player.
		if (this.isCalculatingPath) {
			PlayerEntity player = (PlayerEntity) (Object) this;
			player.setVelocity(Vec3d.ZERO); // Set velocity to zero to prevent movement.
			return;
		}

		// If a path is being followed, delegate to the MovementManager.
		if (this.movementManager != null && this.movementManager.isFollowingPath()) {
			PlayerEntity player = (PlayerEntity) (Object) this;
			this.movementManager.tick(player);
		}
	}
}
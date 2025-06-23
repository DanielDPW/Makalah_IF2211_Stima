package stimadpw.stima.mixin;

import stimadpw.stima.algorithms.IPathfindingController;
import stimadpw.stima.algorithms.PathVisualizer;
import stimadpw.stima.algorithms.PathfindingManager;
import stimadpw.stima.algorithms.PathfindingResult;
import stimadpw.stima.state.Node;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class PathTo implements IPathfindingController {

	private volatile PathVisualizer pathVisualizer;
	private volatile boolean isCalculatingPath = false;

	@Override
	public void startPathfinding(BlockPos endPos) {
		if (this.pathVisualizer != null) {
			stopPathfinding();
		}

		PlayerEntity player = (PlayerEntity) (Object) this;
		this.isCalculatingPath = true;
		player.sendMessage(Text.literal("Calculating path to " + endPos.getX() + " " + endPos.getY() + " " + endPos.getZ() + "..."), false);

		new Thread(() -> {
			long startTime = System.currentTimeMillis();
			PathfindingManager pathfinder = new PathfindingManager(player.getWorld());
			PathfindingResult result = pathfinder.findPath(player.getBlockPos().down(), endPos);
			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime;

			this.isCalculatingPath = false;
			int expandedNodes = result.getExpandedNodes();

			if (result.hasPath()) {
				List<Node> path = result.getPath();
				player.sendMessage(Text.literal(String.format("§aPath found! §7(Nodes: %d, Expanded: %d, Time: %dms)", path.size(), expandedNodes, duration)), false);
				this.pathVisualizer = new PathVisualizer(path, player.getWorld());
			} else {
				player.sendMessage(Text.literal(String.format("§cPathfinding failed! §7(Expanded: %d, Time: %dms)", expandedNodes, duration)), false);
			}
		}).start();
	}

	@Override
	public void stopPathfinding() {
		if (this.pathVisualizer != null) {
			this.pathVisualizer.stop();
			this.pathVisualizer = null;
		}
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void onTick(CallbackInfo ci) {

		// Tick the visualizer if it exists
		if (this.pathVisualizer != null) {
			this.pathVisualizer.tick();
		}
	}
}
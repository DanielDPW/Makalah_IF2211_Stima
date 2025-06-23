package stimadpw.stima.algorithms;

import net.minecraft.util.math.BlockPos;

public interface IPathfindingController {
    void startPathfinding(BlockPos goal);
    void stopPathfinding();
}
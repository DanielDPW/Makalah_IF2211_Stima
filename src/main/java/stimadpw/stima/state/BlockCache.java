package stimadpw.stima.state;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public class BlockCache {
    private final BlockStateInterface bsi;

    public BlockCache(BlockStateInterface bsi) {
        this.bsi = bsi;
    }

    /**
     * Check if a block can be walked on.
     */
    public boolean canWalkOn(int x, int y, int z, BlockState state) {
        return MovementHelper.canWalkOn(this.bsi, x, y, z, state);
    }

    /**
     * Check if a block can be walked through.
     */
    public boolean canWalkThrough(int x, int y, int z, BlockState state) {
        return MovementHelper.canWalkThrough(this.bsi, x, y, z, state);
    }
}
package stimadpw.stima.state;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public class BlockCache {

    private final int[] data = new int[Block.STATE_IDS.size()];
    private final BlockStateInterface bsi;

    private static final int CACHED_MASK      = 1 << 0;
    private static final int CAN_WALK_ON_MASK    = 1 << 1;
    private static final int CAN_WALK_THROUGH_MASK = 1 << 2;

    public BlockCache(BlockStateInterface bsi) {
        this.bsi = bsi;
    }

    /**
     * Computes the properties for a given BlockState and stores them in the cache.
     */
    private void computeBlockData(int id, BlockState state) {
        int blockData = 0;

        if (MovementHelper.canWalkOn(this.bsi, 0, 0, 0, state)) {
            blockData |= CAN_WALK_ON_MASK;
        }

        if (MovementHelper.canWalkThrough(this.bsi, 0, 0, 0, state)) {
            blockData |= CAN_WALK_THROUGH_MASK;
        }

        blockData |= CACHED_MASK; // Mark this block state as computed.
        this.data[id] = blockData;
    }

    /**
     * Retrieves the cached data for a BlockState, computing it if it doesn't exist yet.
     */
    private int getBlockData(BlockState state) {
        int id = Block.getRawIdFromState(state);
        int blockData = this.data[id];

        if ((blockData & CACHED_MASK) == 0) {
            computeBlockData(id, state);
            blockData = this.data[id];
        }
        return blockData;
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
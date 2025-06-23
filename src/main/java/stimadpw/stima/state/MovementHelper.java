package stimadpw.stima.state;

import net.minecraft.block.*;
import net.minecraft.block.enums.SlabType;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.Direction;

public class MovementHelper {

    /**
     * Checks if the player can stand on top of a block at a specific position.
     */
    public static boolean canWalkOn(BlockStateInterface bsi, int x, int y, int z, BlockState state) {
        Block block = state.getBlock();

        if (block instanceof SlabBlock) {
            // Slabs are walkable if they are bottom slabs or double slabs.
            return state.get(SlabBlock.TYPE) != SlabType.TOP;
        }

        if (state.getFluidState().isStill() && state.getFluidState().getFluid() == Fluids.WATER) {
            return bsi.get(x, y + 1, z).getFluidState().isEmpty();
        }

        if (state.isSideSolidFullSquare(bsi.getWorld(), bsi.mutable.set(x, y, z), Direction.UP)) {
            return true;
        }

        return block instanceof FenceBlock || block instanceof WallBlock;
    }

    /**
     * Checks if the player can move through the space occupied by a block.
     */
    public static boolean canWalkThrough(BlockStateInterface bsi, int x, int y, int z, BlockState state) {
        return state.getCollisionShape(bsi.getWorld(), bsi.mutable.set(x, y, z)).isEmpty()
                || state.isReplaceable()
                || !state.getFluidState().isEmpty();
    }

    /**
     * Checks if a block is dangerous to stand on.
     */
    public static boolean isDangerous(BlockState state) {
        Block block = state.getBlock();
        if (!state.getFluidState().isEmpty() && state.getFluidState().getFluid() == Fluids.LAVA) {
            return true;
        }

        return block instanceof CactusBlock ||
                block instanceof MagmaBlock ||
                block == Blocks.SWEET_BERRY_BUSH ||
                block instanceof AbstractFireBlock;
    }
}
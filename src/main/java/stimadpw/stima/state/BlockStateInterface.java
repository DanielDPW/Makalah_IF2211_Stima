package stimadpw.stima.state;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockStateInterface {

    private final World world;
    public final BlockPos.Mutable mutable = new BlockPos.Mutable();

    public BlockStateInterface(World world) {
        this.world = world;
    }

    public World getWorld() {
        return this.world;
    }

    public BlockState get(int x, int y, int z) {
        return this.world.getBlockState(mutable.set(x, y, z));
    }
}
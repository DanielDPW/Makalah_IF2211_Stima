package stimadpw.stima.algorithms;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;
import stimadpw.stima.state.Node;

import java.util.List;

public class PathVisualizer {
    private final List<Node> path;
    private final World world;
    private boolean active = true;

    public static final DustParticleEffect RED_DUST_BLOB = new DustParticleEffect(DustParticleEffect.RED, 2.0f);

    public PathVisualizer(List<Node> path, World world) {
        this.path = path;
        this.world = world;
    }

    public void tick() {
        if (!active || path == null || path.isEmpty()) {
            return;
        }

        // Spawn a particle on each node of the path
        for (Node node : path) {
            ((ServerWorld) world).spawnParticles(RED_DUST_BLOB,
                    node.getPos().getX() + 0.5,
                    node.getPos().getY() + 2.0,
                    node.getPos().getZ() + 0.5,
                    0,
                    0,
                    0,
                    0,
                    0.5
            );
        }
    }

    /**
     * Stops the particle effect from running.
     */
    public void stop() {
        this.active = false;
    }
}
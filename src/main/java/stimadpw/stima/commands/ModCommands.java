package stimadpw.stima.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType; // Use Integer for BlockPos
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import stimadpw.stima.Stima;
import stimadpw.stima.algorithms.IPathfindingController; // Import the interface we designed

public class ModCommands {

    public static void registerAllCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("goto")
                // Require operator level 2 to use the command, can be changed or removed
                .requires(source -> source.hasPermissionLevel(2))
                .then(argument("x", IntegerArgumentType.integer())
                        .then(argument("y", IntegerArgumentType.integer())
                                .then(argument("z", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            try {
                                                // Get the player who ran the command
                                                final ServerPlayerEntity player = context.getSource().getPlayer();

                                                // Get the integer arguments from the command
                                                final int x = IntegerArgumentType.getInteger(context, "x");
                                                final int y = IntegerArgumentType.getInteger(context, "y");
                                                final int z = IntegerArgumentType.getInteger(context, "z");

                                                // Create the goal position
                                                final BlockPos goalPos = new BlockPos(x, y, z);

                                                // Call the startPathfinding method on our mixin via the interface
                                                ((IPathfindingController) player).startPathfinding(goalPos);

                                                context.getSource().sendFeedback(() -> Text.literal("Pathfinding started to " + x + " " + y + " " + z), false);
                                                return Command.SINGLE_SUCCESS; // Return 1 for success

                                            } catch (Exception e) {
                                                context.getSource().sendError(Text.literal("Error executing command: " + e.getMessage()));
                                                Stima.LOGGER.error("Error executing /goto command", e);
                                                return 0; // Return 0 for failure
                                            }
                                        })))));
    }
}
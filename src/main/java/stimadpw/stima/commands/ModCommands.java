package stimadpw.stima.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import stimadpw.stima.Stima;
import stimadpw.stima.algorithms.IPathfindingController;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ModCommands {

    public static void registerAllCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("pathto")
                .requires(source -> source.hasPermissionLevel(2))
                .then(argument("x", IntegerArgumentType.integer())
                        .then(argument("y", IntegerArgumentType.integer())
                                .then(argument("z", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            try {
                                                final ServerPlayerEntity player = context.getSource().getPlayer();
                                                final int x = IntegerArgumentType.getInteger(context, "x");
                                                final int y = IntegerArgumentType.getInteger(context, "y");
                                                final int z = IntegerArgumentType.getInteger(context, "z");
                                                final BlockPos goalPos = new BlockPos(x, y, z);

                                                ((IPathfindingController) player).startPathfinding(goalPos);

                                                return Command.SINGLE_SUCCESS;
                                            } catch (Exception e) {
                                                context.getSource().sendError(Text.literal("Error executing command: " + e.getMessage()));
                                                Stima.LOGGER.error("Error executing /goto command", e);
                                                return 0;
                                            }
                                        })))));

        dispatcher.register(literal("stoppath")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> {
                    try {
                        final ServerPlayerEntity player = context.getSource().getPlayer();
                        ((IPathfindingController) player).stopPathfinding();
                        context.getSource().sendFeedback(() -> Text.literal("Path visualization stopped."), false);
                        return Command.SINGLE_SUCCESS;
                    } catch (Exception e) {
                        context.getSource().sendError(Text.literal("Error executing command: " + e.getMessage()));
                        Stima.LOGGER.error("Error executing /stoppath command", e);
                        return 0;
                    }
                }));
    }
}
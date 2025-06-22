package stimadpw.stima.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;

import stimadpw.stima.Stima;

public class ModCommands {

    public static void registerAllCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        // --- /hello command ---
        dispatcher.register(literal("hello")
                .executes(context -> {
                    context.getSource().sendFeedback(() -> Text.literal("Hello from your Fabric Mod!"), false);
                    return Command.SINGLE_SUCCESS;
                })
        );

        // --- /sayhi <player> command ---
        dispatcher.register(literal("sayhi")
                .then(argument("target_player", EntityArgumentType.player())
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            try {
                                PlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "target_player");
                                String targetPlayerName = targetPlayer.getName().getString();
                                String senderName = source.getName();

                                source.sendFeedback(() -> Text.literal("You said hi to " + targetPlayerName + "!"), false);
                                targetPlayer.sendMessage(Text.literal(senderName + " said hi to you!"), false);

                            } catch (Exception e) {
                                source.sendError(Text.literal("Error: Could not find player or an issue occurred."));
                                Stima.LOGGER.error("Error executing /sayhi command", e);
                                return 0;
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                )
        );

        // --- /ophello command (requires operator level 2) ---
        dispatcher.register(literal("ophello")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> {
                    context.getSource().sendFeedback(() -> Text.literal("Hello, Operator! You have permission."), false);
                    return Command.SINGLE_SUCCESS;
                })
        );
    }
}

//package org.stima.commands;
//
//import com.mojang.brigadier.Command;
//import com.mojang.brigadier.CommandDispatcher;
//import com.mojang.brigadier.arguments.DoubleArgumentType; // Import for double arguments
//import net.minecraft.command.argument.EntityArgumentType;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.server.command.ServerCommandSource;
//import net.minecraft.text.Text;
//
//// Import your MyMod class to access the gotoPlayer method
//import org.stima.MyMod; // <--- ADD THIS IMPORT
//
//// Static imports for cleaner command building
//import static net.minecraft.server.command.CommandManager.argument;
//import static net.minecraft.server.command.CommandManager.literal;
//
//public class ModCommands {
//
//    public static void registerAllCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
//        // ... (existing /hello, /sayhi, /ophello commands) ...
//
//        // --- /goto <x> <y> <z> command ---
//        dispatcher.register(literal("goto")
//                        // First argument: X coordinate
//                        .then(argument("x", DoubleArgumentType.doubleArg())
//                                // Second argument: Y coordinate
//                                .then(argument("y", DoubleArgumentType.doubleArg())
//                                        // Third argument: Z coordinate
//                                        .then(argument("z", DoubleArgumentType.doubleArg())
//                                                .executes(context -> {
//                                                    ServerCommandSource source = context.getSource();
//
//                                                    // Ensure the command sender is a player
//                                                    if (!(source.getEntity() instanceof PlayerEntity player)) {
//                                                        source.sendError(Text.literal("This command can only be executed by a player."));
//                                                        return 0; // Indicate failure
//                                                    }
//
//                                                    // Get the argument values
//                                                    double x = DoubleArgumentType.getDouble(context, "x");
//                                                    double y = DoubleArgumentType.getDouble(context, "y");
//                                                    double z = DoubleArgumentType.getDouble(context, "z");
//
//                                                    try {
//                                                        // Execute your goto method
//                                                        MyMod.gotoPlayer(player, x, y, z);
//                                                        source.sendFeedback(() -> Text.literal(String.format("Teleported to X: %.2f, Y: %.2f, Z: %.2f", x, y, z)), false);
//                                                    } catch (Exception e) {
//                                                        source.sendError(Text.literal("An error occurred during teleportation."));
//                                                        MyMod.LOGGER.error("Error teleporting player with /goto command", e);
//                                                        return 0; // Indicate failure
//                                                    }
//
//                                                    return Command.SINGLE_SUCCESS;
//                                                })
//                                        )
//                                )
//                        )
//                // Optional: Add a permission check for the /goto command
//                // .requires(source -> source.hasPermissionLevel(2)) // Example: Requires op level 2
//        );
//    }
//}
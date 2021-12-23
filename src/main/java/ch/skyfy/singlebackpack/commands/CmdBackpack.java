package ch.skyfy.singlebackpack.commands;

import ch.skyfy.singlebackpack.BackpackManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class CmdBackpack {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("inventory")
                .then(
                        CommandManager.argument("open", StringArgumentType.string())
                                .suggests((context, builder) -> {
                                    builder.suggest("open");
                                    return builder.buildFuture();
                                }).executes(context -> {
                                    BackpackManager.openInventory(context.getSource().getPlayer());
                                    return Command.SINGLE_SUCCESS;
                                })
                )
        );

    }

}

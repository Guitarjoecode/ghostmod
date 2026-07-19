package net.ghostmod.client.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.ghostmod.client.GhostModClient;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.*;

public class GhostEnableCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext context) {
        dispatcher.register(literal("ghostenable")
                .then(literal("ghostblocks")
                        .executes(ctx -> ghostEnable(ctx.getSource()))));
    }

    private static int ghostEnable(FabricClientCommandSource source) {
        GhostModClient.Ghostblocks_Enabled = true;
        source.sendFeedback(Component.translatable("command.ghostenable.success"));

        return Command.SINGLE_SUCCESS;

    }

}

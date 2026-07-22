package net.guitarjoe.ghostmod.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.guitarjoe.ghostmod.util.ModUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.*;
import static dev.xpple.clientarguments.arguments.CBlockPosArgument.*;

public class GhostDestroyBlockCommand {
    private static final SimpleCommandExceptionType DESTROY_FAILED_EXCEPTION = new SimpleCommandExceptionType(Component.translatable("command.ghostdestroy.failed"));

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("ghostdestroy")
                .then(argument("pos", blockPos())
                        .executes(ctx -> destroyBlock(ctx.getSource(), getBlockPos(ctx, "pos"))
                        )));
    }

    private static int destroyBlock(FabricClientCommandSource source, BlockPos pos) throws CommandSyntaxException {
        ClientLevel level = source.getLevel();

        ModUtils.checkLoaded(level, pos);

        if (level.destroyBlock(pos, true)) {
            source.sendFeedback(Component.translatable("command.ghostdestroy.success"));
            return  Command.SINGLE_SUCCESS;
        } else {
            throw DESTROY_FAILED_EXCEPTION.create();
        }
    }
}

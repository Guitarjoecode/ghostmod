package net.guitarjoe.ghostmod.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.guitarjoe.ghostmod.GhostModClient;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

import static dev.xpple.clientarguments.arguments.CBlockPosArgument.OUT_OF_WORLD_EXCEPTION;
import static dev.xpple.clientarguments.arguments.CBlockPosArgument.UNLOADED_EXCEPTION;

public class ModUtils {
    public static void checkLoaded(ClientLevel level, BlockPos pos) throws CommandSyntaxException {
        if (!level.isInWorldBounds(pos)) {
            throw OUT_OF_WORLD_EXCEPTION.create();
        }
        if (!level.isLoaded(pos)) {
            throw UNLOADED_EXCEPTION.create();
        }
    }

    public static boolean check_GhostBlocks_enabled(FabricClientCommandSource source) {
        if (!GhostModClient.Ghostblocks_Enabled) {
            MutableComponent text = Component.translatable("command.ghostblock.enable");

            Component message = text.withStyle(style -> style.applyFormat(ChatFormatting.UNDERLINE)
                    .withColor(ChatFormatting.RED)
                    .withClickEvent(new ClickEvent.RunCommand("/ghostenable ghostblocks"))
                    .withHoverEvent(new HoverEvent.ShowText(Component.translatable("command.ghostblock.click"))));

            Component warnung = Component.translatable("command.ghostblock.warning")
                    .withStyle(style -> style.applyFormat(ChatFormatting.YELLOW));

            source.sendFeedback(warnung);
            source.sendFeedback(message);
            return true;
        }
        return false;
    }
}

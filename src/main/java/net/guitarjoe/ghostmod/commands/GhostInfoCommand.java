package net.guitarjoe.ghostmod.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class GhostInfoCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext context) {
        dispatcher.register(ClientCommands.literal("ghostinfo")
                .executes(GhostInfoCommand::ghostInfo));
    }

    private static int ghostInfo(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        Minecraft client = source.getClient();
        ClientLevel level = source.getLevel();
        Player player = client.player;

        source.sendFeedback(Component.translatable("command.ghostinfo.player_info"));

        sendStat("command.ghostinfo.profile_name", player.getGameProfile().name());
        sendStat("command.ghostinfo.uuid", player.getUUID().toString());

        source.sendFeedback(Component.translatable("command.ghostinfo.general_info"));

        String coordinates = String.format(java.util.Locale.US, "X: %.2f | Y: %.2f | Z: %.2f", player.getX(), player.getY(), player.getZ());
        String rotation = String.format(java.util.Locale.US, "Yaw: %.1f | Pitch: %.1f", player.getYRot(), player.getXRot());
        sendStat("command.ghostinfo.coordinates", coordinates);
        sendStat("command.ghostinfo.rotation", rotation);
        sendStat("command.ghostinfo.armor", player.getArmorValue()); //armor
        if (client.player.getActiveEffects().isEmpty()) {
            sendStat("command.ghostinfo.effects", "none");
        } else {
            String Effects = client.player.getActiveEffects().stream()
                    .map(effect -> {
                        String effectId = net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT
                                .getKey(effect.getEffect().value())
                                .toString();

                        int amplifier = effect.getAmplifier() + 1;
                        int durationSeconds = effect.getDuration() / 20;

                        return effectId + " " + amplifier + " (" + durationSeconds + "s)";
                    })
                    .collect(java.util.stream.Collectors.joining(", "));
            sendStat("command.ghostinfo.effects", Effects);
        }
        String deathLocString = player.getLastDeathLocation()
                .map(globalPos -> {
                    String dimension = globalPos.dimension().toString();
                    var pos = globalPos.pos();

                    return dimension.replace("minecraft:", "") + " @ X: " + pos.getX() + ", Y: " + pos.getY() + ", Z: " + pos.getZ();
                })
                .orElse("none");
        sendStat("command.ghostinfo.deathlocation", deathLocString);
        sendStat("command.ghostinfo.difficulty", level.getDifficulty());

        source.sendFeedback(Component.translatable("command.ghostinfo.inventory"));

        sendStat("command.ghostinfo.mainhand", player.getMainHandItem());
        sendStat("command.ghostinfo.offhand", player.getOffhandItem());


        return Command.SINGLE_SUCCESS;
    }

    private static void sendStat(String translationKey, Object value) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        Component label = Component.translatable(translationKey);
        Component valuePart = Component.literal(": " + value.toString());

        client.player.sendSystemMessage(label.copy().append(valuePart));
    }
}

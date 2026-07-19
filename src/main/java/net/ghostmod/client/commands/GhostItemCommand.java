package net.ghostmod.client.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.nbt.CompoundTag;

public class GhostItemCommand {

    private static final SimpleCommandExceptionType ITEM_FAILED_EXCEPTION =
            new SimpleCommandExceptionType(Component.translatable("command.giveghostitem.failure"));

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext context) {
        dispatcher.register(ClientCommands.literal("ghostitem")
                .then(ClientCommands.argument("item", ItemArgument.item(context))
                        .executes(ctx -> giveGhostitem(ctx, ItemArgument.getItem(ctx, "item"), 1))
                        .then(ClientCommands.argument("count", IntegerArgumentType.integer(1))
                                .executes(ctx -> giveGhostitem(ctx, ItemArgument.getItem(ctx, "item"), IntegerArgumentType.getInteger(ctx, "count"))))));
    }

    private static int giveGhostitem(CommandContext<FabricClientCommandSource> context, final ItemInput itemInput, final int count) throws CommandSyntaxException {
        FabricClientCommandSource source = context.getSource();
        Minecraft client = source.getClient();

        if (client.player == null || client.gameMode == null) {
            throw ITEM_FAILED_EXCEPTION.create();
        }

        ItemStack targetItem = itemInput.createItemStack(count);

        String itemKey = BuiltInRegistries.ITEM.getKey(targetItem.getItem()).toString();

        CompoundTag ghostNbt = new CompoundTag();
        ghostNbt.putString("GhostItemKey", itemKey);
        targetItem.set(DataComponents.CUSTOM_DATA, CustomData.of(ghostNbt));

        if (client.player.isCreative()) {
            int slot = 36 + client.player.getInventory().getSelectedSlot();
            client.gameMode.handleCreativeModeItemAdd(targetItem, slot);

            source.sendFeedback(Component.translatable("command.giveghostitem.creative"));
        } else {
            if (!client.player.getInventory().add(targetItem)) {
                client.player.drop(targetItem, false);
            }
            source.sendFeedback(Component.translatable("command.giveghostitem.success"));
        }

        return Command.SINGLE_SUCCESS;
    }
}

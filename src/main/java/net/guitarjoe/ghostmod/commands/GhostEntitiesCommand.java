package net.guitarjoe.ghostmod.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import static dev.xpple.clientarguments.arguments.CBlockPosArgument.*;
import static dev.xpple.clientarguments.arguments.CResourceArgument.getEntityType;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.*;

public class GhostEntitiesCommand {
    private static final java.util.Set<Integer> spawnedGhostEntityIds = new java.util.HashSet<>();

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext context) {
        dispatcher.register(literal("ghostentity")
                .then(literal("clear")
                        .executes(ctx -> clearAllGhostEntities(ctx.getSource())))
                .then(argument("entity", net.minecraft.commands.arguments.ResourceArgument.resource(context, net.minecraft.core.registries.Registries.ENTITY_TYPE))
                        .executes(ctx -> spawnGhostEntity(
                                ctx.getSource(),
                                getEntityType(ctx, "entity"),
                                ctx.getSource().getPlayer().blockPosition()))
                        .then(argument("pos", blockPos())
                                .executes(ctx -> spawnGhostEntity(
                                        ctx.getSource(),
                                        getEntityType(ctx, "entity"),
                                        getLoadedBlockPos(ctx, "pos"))
                                ))));
    }

    private static int spawnGhostEntity(FabricClientCommandSource source, Holder.Reference<EntityType<?>> entityTypeHolder, BlockPos pos) throws CommandSyntaxException {
        ClientLevel level = source.getLevel();

        Entity entity = entityTypeHolder.value().create(level, EntitySpawnReason.COMMAND);
        if (entity == null) {
            throw new SimpleCommandExceptionType(Component.translatable("commands.summon.failed")).create();
        }

        entity.setPos(new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));

        int clientEntityId = -(level.getEntityCount() + 500);
        entity.setId(clientEntityId);

        spawnedGhostEntityIds.add(clientEntityId);
        level.addEntity(entity);

        source.sendFeedback(Component.translatable("command.ghostentities.success"));
        return Command.SINGLE_SUCCESS;
    }

    private static int clearAllGhostEntities(FabricClientCommandSource source) {
        ClientLevel level = source.getLevel();

        if (spawnedGhostEntityIds.isEmpty()) {
            source.sendFeedback(Component.translatable("command.clearallghostentities.no_current_entities"));
            return 0;
        }

        int removeCount = 0;
        for (int id : spawnedGhostEntityIds) {
            Entity entity = level.getEntity(id);
            if (entity != null) {
                entity.remove(Entity.RemovalReason.DISCARDED);
                removeCount++;
            }
        }

        spawnedGhostEntityIds.clear();

        source.sendFeedback(Component.translatable("command.clearallghostentities.success", removeCount));
        return Command.SINGLE_SUCCESS;
    }

}

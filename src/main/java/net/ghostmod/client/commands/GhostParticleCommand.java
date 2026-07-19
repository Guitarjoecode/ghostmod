package net.ghostmod.client.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

import static dev.xpple.clientarguments.arguments.CParticleArgument.*;
import static dev.xpple.clientarguments.arguments.CBlockPosArgument.*;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.*;

public class GhostParticleCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext context) {
        dispatcher.register(literal("ghostparticle")
                .then(argument("name", particle(context))
                        .executes(ctx -> spawnGhostParticle(ctx.getSource(), getParticle(ctx, "name"), ctx.getSource().getPlayer().blockPosition()))
                        .then(argument("pos", blockPos())
                                .executes(ctx -> spawnGhostParticle(ctx.getSource(), getParticle(ctx, "name"), getBlockPos(ctx, "pos"))
                                ))));
    }

    private static int spawnGhostParticle(FabricClientCommandSource source, ParticleOptions parameters, BlockPos pos) {
        source.getLevel().addAlwaysVisibleParticle(parameters, false, pos.getX(), pos.getY(), pos.getZ(), Vec3.ZERO.x * 1, Vec3.ZERO.y * 1, Vec3.ZERO.z * 1);

        source.sendFeedback(Component.translatable("command.ghostparticle.success"));
        return Command.SINGLE_SUCCESS;
    }
}
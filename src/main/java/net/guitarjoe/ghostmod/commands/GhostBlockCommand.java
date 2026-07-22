package net.guitarjoe.ghostmod.commands;

/*
 * Copyright (c) 2021 Earthcomputer
 * Copyright (c) 2026 Guitarjoe
 *
 * This file has been modified by Guitarjoe on Juli 2026
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * and a copy of GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

/*
 * Origin Sources: https://github.com/Earthcomputer/clientcommands/blob/fabric/src/main/java/net/earthcomputer/clientcommands/command/GhostBlockCommand.java
 */

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.guitarjoe.ghostmod.util.ModUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.function.Predicate;

import static dev.xpple.clientarguments.arguments.CBlockPosArgument.*;
import static dev.xpple.clientarguments.arguments.CBlockPredicateArgument.*;
import static dev.xpple.clientarguments.arguments.CBlockStateArgument.*;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.*;

public class GhostBlockCommand {

    private static final SimpleCommandExceptionType SET_FAILED_EXCEPTION = new SimpleCommandExceptionType(Component.translatable("commands.setblock.failed"));
    private static final SimpleCommandExceptionType FILL_FAILED_EXCEPTION = new SimpleCommandExceptionType(Component.translatable("commands.fill.failed"));

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext context) {
        dispatcher.register(literal("ghostblock")
                .then(literal("set")
                        .then(argument("pos", blockPos())
                                .then(argument("block", blockState(context))
                                        .executes(ctx -> setGhostBlock(ctx.getSource(), getBlockPos(ctx, "pos"), getBlockState(ctx, "block").getState())))))
                .then(literal("fill")
                        .then(argument("from", blockPos())
                                .then(argument("to", blockPos())
                                        .then(argument("block", blockState(context))
                                                .executes(ctx -> fillGhostBlocks(ctx.getSource(), getBlockPos(ctx, "from"), getBlockPos(ctx, "to"), getBlockState(ctx, "block").getState(), pos -> true))
                                                .then(literal("replace")
                                                        .then(argument("filter", blockPredicate(context))
                                                                .executes(ctx -> fillGhostBlocks(ctx.getSource(), getBlockPos(ctx, "from"), getBlockPos(ctx, "to"), getBlockState(ctx, "block").getState(), getBlockPredicate(ctx, "filter"))))))))));
    }

    private static int setGhostBlock(FabricClientCommandSource source, BlockPos pos, BlockState state) throws CommandSyntaxException {
        ClientLevel level = source.getLevel();

        if (ModUtils.check_GhostBlocks_enabled(source)) {
            return 0;
        }

        ModUtils.checkLoaded(level, pos);

        boolean result = level.setBlock(pos, state, 18);
        if (result) {
            source.sendFeedback(Component.translatable("command.ghostblock.set.success"));
            return Command.SINGLE_SUCCESS;
        } else {
            throw SET_FAILED_EXCEPTION.create();
        }
    }

    private static int fillGhostBlocks(FabricClientCommandSource source, BlockPos from, BlockPos to, BlockState state, Predicate<BlockInWorld> filter) throws CommandSyntaxException {
        ClientLevel level = source.getLevel();

        if (ModUtils.check_GhostBlocks_enabled(source)) {
            return 0;
        }

        ModUtils.checkLoaded(level, from);
        ModUtils.checkLoaded(level, to);

        BoundingBox range = BoundingBox.fromCorners(from, to);
        int successCount = 0;
        for (BlockPos pos : BlockPos.betweenClosed(range.minX(), range.minY(), range.minZ(), range.maxX(), range.maxY(), range.maxZ())) {
            if (filter.test(new BlockInWorld(level, pos, true))) {
                if (level.setBlock(pos, state, 18)) {
                    successCount++;
                }
            }
        }

        if (successCount == 0) {
            throw FILL_FAILED_EXCEPTION.create();
        }

        source.sendFeedback(Component.translatable("command.ghostblock.fill.success", successCount));

        return Command.SINGLE_SUCCESS;
    }
}
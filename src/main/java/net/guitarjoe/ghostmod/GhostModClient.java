package net.guitarjoe.ghostmod;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.guitarjoe.ghostmod.commands.*;
import net.minecraft.commands.CommandBuildContext;

public class GhostModClient implements ClientModInitializer {
	public static boolean Ghostblocks_Enabled = false;

	@Override
	public void onInitializeClient() {
		ClientCommandRegistrationCallback.EVENT.register(GhostModClient::registerCommands);

	}

	private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext context) {
		GhostBlockCommand.register(dispatcher, context);
		GhostEnableCommand.register(dispatcher);
		GhostEntitiesCommand.register(dispatcher, context);
		GhostInfoCommand.register(dispatcher);
		GhostItemCommand.register(dispatcher, context);
		GhostParticleCommand.register(dispatcher, context);
		GhostDestroyBlockCommand.register(dispatcher);
	}

}
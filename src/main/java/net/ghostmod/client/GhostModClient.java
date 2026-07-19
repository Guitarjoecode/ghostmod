package net.ghostmod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.ghostmod.client.commands.*;

public class GhostModClient implements ClientModInitializer {
	public static boolean Ghostblocks_Enabled = false;

	@Override
	public void onInitializeClient() {
		ClientCommandRegistrationCallback.EVENT.register(GhostItemCommand::register);
		ClientCommandRegistrationCallback.EVENT.register(GhostBlockCommand::register);
		ClientCommandRegistrationCallback.EVENT.register(GhostEntitiesCommand::register);
		ClientCommandRegistrationCallback.EVENT.register(GhostInfoCommand::register);
		ClientCommandRegistrationCallback.EVENT.register(GhostParticleCommand::register);
		ClientCommandRegistrationCallback.EVENT.register(GhostEnableCommand::register);


	}
}
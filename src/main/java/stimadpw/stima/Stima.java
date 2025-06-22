package stimadpw.stima;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stimadpw.stima.commands.ModCommands;

public class Stima implements ModInitializer {
	public static final String MOD_ID = "stima";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				ModCommands.registerAllCommands(dispatcher) // Call the static method from your ModCommands class
		);
	}
}
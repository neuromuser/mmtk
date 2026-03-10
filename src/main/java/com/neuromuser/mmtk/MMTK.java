package com.neuromuser.mmtk;

import com.neuromuser.mmtk.command.ModCommandRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.net.command.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.ItemInitEntrypoint;

public class MMTK implements ModInitializer, ItemInitEntrypoint {
	public static final String MOD_ID = "mmtk";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		CommandManager.registerCommand(new ModCommandRegistry());
	}


	@Override
	public void afterItemInit() {}
}

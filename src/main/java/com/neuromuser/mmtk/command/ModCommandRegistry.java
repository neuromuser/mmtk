package com.neuromuser.mmtk.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.CommandManager;

public class ModCommandRegistry implements CommandManager.CommandRegistry {

	@Override
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		// Specify <CommandSource> in the literal call to match your CMDUnbreakable class
		ArgumentBuilderLiteral<CommandSource> mmtkBase = ArgumentBuilderLiteral.<CommandSource>literal("mmtk");

		ArgumentBuilderLiteral<CommandSource> unbreakableSub = ArgumentBuilderLiteral.<CommandSource>literal("unbreakable")
			.executes(new CMDUnbreakable());

		mmtkBase.requires(CommandSource::hasAdmin)
			.then(unbreakableSub);

		dispatcher.register(mmtkBase);
	}
}

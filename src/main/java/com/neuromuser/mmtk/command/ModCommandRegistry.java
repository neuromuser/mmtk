package com.neuromuser.mmtk.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeBlock;
import net.minecraft.core.net.command.helpers.BlockInput;

public class ModCommandRegistry implements CommandManager.CommandRegistry {

	@Override
	public void register(CommandDispatcher<CommandSource> dispatcher) {

		ArgumentBuilderLiteral<CommandSource> mmtkBase =
			ArgumentBuilderLiteral.literal("mmtk");

		ArgumentBuilderLiteral<CommandSource> canBreakSub =
			ArgumentBuilderLiteral.literal("canbreak");

		ArgumentBuilderLiteral<CommandSource> unbreakableSub = ArgumentBuilderLiteral.<CommandSource>literal("unbreakable")
			.executes(new CMDUnbreakable());

		ArgumentBuilderRequired<CommandSource, BlockInput> blockbreakArg =
			ArgumentBuilderRequired.<CommandSource, BlockInput>argument("block", ArgumentTypeBlock.block())
				.executes(new CMDCanBreak());

		ArgumentBuilderLiteral<CommandSource> canPlaceOnSub =
			ArgumentBuilderLiteral.literal("canplaceon");

		ArgumentBuilderRequired<CommandSource, BlockInput> blockplaceArg =
			ArgumentBuilderRequired.<CommandSource, BlockInput>argument("block", ArgumentTypeBlock.block())
				.executes(new CMDCanPlaceOn());

		mmtkBase.requires(CommandSource::hasAdmin)
			.then(unbreakableSub)
			.then(canBreakSub.then(blockbreakArg))
			.then(canPlaceOnSub.then(blockplaceArg));


		dispatcher.register(mmtkBase);
	}
}

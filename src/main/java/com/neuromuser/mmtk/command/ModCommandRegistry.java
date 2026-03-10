package com.neuromuser.mmtk.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeBool;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeBlock;
import net.minecraft.core.net.command.helpers.BlockInput;

public class ModCommandRegistry implements CommandManager.CommandRegistry {

	@Override
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		ArgumentBuilderLiteral<CommandSource> mmtkBase = ArgumentBuilderLiteral.literal("mmtk");


		ArgumentBuilderLiteral<CommandSource> globalSub = ArgumentBuilderLiteral.literal("global");

		ArgumentBuilderLiteral<CommandSource> globalCanBreak = ArgumentBuilderLiteral.literal("canbreak");

		globalCanBreak.then(
			ArgumentBuilderRequired.<CommandSource, BlockInput>argument("block_id", ArgumentTypeBlock.block())
				.then(ArgumentBuilderRequired.<CommandSource, Boolean>argument("state", ArgumentTypeBool.bool())
					.executes(new CMDGlobalRule(true)))
		);

		ArgumentBuilderLiteral<CommandSource> globalCanPlaceOn = ArgumentBuilderLiteral.literal("canplaceon");

		globalCanPlaceOn.then(
			ArgumentBuilderRequired.<CommandSource, BlockInput>argument("block_id", ArgumentTypeBlock.block())
				.then(ArgumentBuilderRequired.<CommandSource, Boolean>argument("state", ArgumentTypeBool.bool())
					.executes(new CMDGlobalRule(false)))
		);

		globalSub.then(globalCanBreak).then(globalCanPlaceOn);

		mmtkBase.requires(CommandSource::hasAdmin)
			.then(ArgumentBuilderLiteral.<CommandSource>literal("unbreakable").executes(new CMDUnbreakable()))
			.then(ArgumentBuilderLiteral.<CommandSource>literal("canbreak")
				.then(ArgumentBuilderRequired.<CommandSource, BlockInput>argument("block", ArgumentTypeBlock.block())
					.executes(new CMDCanBreak())))
			.then(ArgumentBuilderLiteral.<CommandSource>literal("canplaceon")
				.then(ArgumentBuilderRequired.<CommandSource, BlockInput>argument("block", ArgumentTypeBlock.block())
					.executes(new CMDCanPlaceOn())))
			.then(globalSub);

		dispatcher.register(mmtkBase);
	}}
